package com.openenglish.hr;

import com.openenglish.hr.service.JwtTokenService;
import graphql.execution.instrumentation.SimpleInstrumentation;
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

        return environment -> {
            if (!argNameClaimNamePairs.isEmpty()) {
                verifyPairs(argNameClaimNamePairs, environment);
            }

            return dataFetcher.get(environment);
        };
    }

    private void verifyPairs(List<Pair<String, String>> argNameClaimNamePairs, DataFetchingEnvironment environment) {

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken)
                (SecurityContextHolder.getContext().getAuthentication());
        String accessToken = jwtAuthenticationToken.getToken().getTokenValue();

        for (Pair<String, String> claimAndArgNames : argNameClaimNamePairs) {
            String argumentToCheck = claimAndArgNames.getLeft();
            String claimToCheck = claimAndArgNames.getRight();

            String claimValue = jwtTokenService.getUserInfoClaim(accessToken, claimToCheck).orElseThrow(
                    () -> {
                        throw new IllegalArgumentException("Missing required claim :" + claimToCheck);
                    }
            );

            String argumentValue = environment.getArgument(argumentToCheck);

            if (!StringUtils.equals(claimValue, argumentValue)) {
                logger.error("Claim vs Argument mismatch: query={} token={}", argumentValue, claimValue);
                throw new IllegalArgumentException("Claim vs Argument mismatch: query=" + argumentValue + " token=" + claimValue);
            }
        }
    }
}