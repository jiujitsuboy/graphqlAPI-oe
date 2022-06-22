package com.openenglish.hr;

import com.openenglish.hr.service.JwtTokenService;
import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecuredByClaimDataAccessInstrumentation extends SimpleInstrumentation {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String SECURED_BY_CLAIM_DIRECTIVE_NAME = "SecuredByClaim";
    private static final String SECURED_BY_CLAIM_ARGUMENT_NAME = "claim";

    @Value("${hrportal.purchaserIdSecurityCheck.enabled:true}")
    private boolean purchaserIdSecurityCheck = true;

    private final JwtTokenService jwtTokenService;

    private static final class DataAccessState implements InstrumentationState {
        private Map<String, String> userAttributes;
    }

    @Override
    public InstrumentationState createState() {
        return new DataAccessState();
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        DataAccessState dataAccessState = parameters.getInstrumentationState();

        if(purchaserIdSecurityCheck) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken)
                    (SecurityContextHolder.getContext().getAuthentication());
            String accessToken = jwtAuthenticationToken.getToken().getTokenValue();

            // stores the user attributes in cognito,
            // later used in all DataFetchers executed for the current GraphQL request
            // to check data assertions
            dataAccessState.userAttributes = jwtTokenService.getUserAttributes(accessToken);
        }

        return super.beginExecution(parameters);
    }

    private List<Pair<String, String>> extractClaimCheckBindings(InstrumentationFieldFetchParameters parameters) {
        return parameters.getField().getArguments().stream()
                .filter(arg -> arg.getDirective(SECURED_BY_CLAIM_DIRECTIVE_NAME) != null)
                .map(this::toArgClaimPair)
                .collect(Collectors.toList());
    }

    private Pair<String, String> toArgClaimPair(GraphQLArgument arg) {
        String argumentName = arg.getName();
        String claimName = (String) arg.getDirective(SECURED_BY_CLAIM_DIRECTIVE_NAME)
                .getArgument(SECURED_BY_CLAIM_ARGUMENT_NAME).getValue();

        return Pair.of(argumentName, claimName);
    }

    @Override
    public DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher, InstrumentationFieldFetchParameters parameters) {
        // Only if security layer is enabled and only on user code
        if (!purchaserIdSecurityCheck || parameters.isTrivialDataFetcher()) {
            return dataFetcher;
        }

        List<Pair<String, String>> argNameClaimNamePairs = extractClaimCheckBindings(parameters);
        DataAccessState state = parameters.getInstrumentationState();

        return environment -> {
            if (!argNameClaimNamePairs.isEmpty()) {
                verifyDataAccessAssertions(argNameClaimNamePairs, state, environment);
            }

            return dataFetcher.get(environment);
        };
    }

    private void verifyDataAccessAssertions(
            List<Pair<String, String>> argNameClaimNamePairs,
            DataAccessState state,
            DataFetchingEnvironment environment
    ) {
        for (Pair<String, String> argNameClaimNamePair : argNameClaimNamePairs) {
            // A pair represents a data access security assertion, there can be 0..n assertions, all must pass
            // to execute the dataFetcher. Required empty claims are treated as errors
            //
            // Given a graphql definition :
            //   getPersons(salesforcePurchaserId: String! @SecuredByClaim(claim: "custom:purchaserId")): [Person]!
            // the Pair under test here would be:
            // argumentName = salesforcePurchaserId
            // claimName = custom:purchaserId
            // Using the names then the values are computed and compared
            // - argumentValue from the GraphQL environment model
            // - claimValue from the userAttributes retrieved from cognito
            //
            // Claim name in SecuredByClaim defaults to "custom:purchaserId" and is usually omitted.
            // But it can be another field in the userAttributes, as an example:
            // sendContactUsMessage(
            //      salesforcePurchaserId: String! @SecuredByClaim,
            //      name: String!,
            //      email: String! @SecuredByClaim(claim: "email"),
            //      message: String!
            // ): MutationResult!
            // would check both salesforcePurchaserId against custom:purchaserId and query param email against
            // user attributes email

            String argumentName = argNameClaimNamePair.getLeft();
            String claimName = argNameClaimNamePair.getRight();

            String claimValue = state.userAttributes.get(claimName);

            if (StringUtils.isEmpty(claimValue)) {
                throw new IllegalArgumentException("Missing required claim :" + claimName);
            }

            String argumentValue = environment.getArgument(argumentName);

            if (!StringUtils.equals(argumentValue, claimValue)) {
                logger.error("Claim vs Argument mismatch: query={} token={}", argumentValue, claimValue);
                throw new IllegalArgumentException("Argument vs Claim mismatch: query="
                        + argumentValue + " token=" + claimValue);
            }
        }
    }
}