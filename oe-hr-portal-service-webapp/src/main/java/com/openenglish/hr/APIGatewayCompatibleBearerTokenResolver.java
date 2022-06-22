package com.openenglish.hr;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * Modified copy of {@link DefaultBearerTokenResolver} to match Authorization headers having the  format :
 * <pre> Authorization: access_token</pre>
 * instead of
 * <pre> Authorization: Bearer access_token</pre>
 * There are 2 comments
 * <pre>
 *     APIGatewayCompatibleBearerTokenResolver divergence from DefaultBearerTokenResolver
 * </pre>
 * where this implementation differs from DefaultBearerTokenResolver. All the rest is an exact copy of
 * DefaultBearerTokenResolver
 * <hr>
 * <p>The default {@link BearerTokenResolver} implementation based on RFC 6750.</p>
 *
 * @author Vedran Pavic
 * @see <a href="https://tools.ietf.org/html/rfc6750#section-2" target="_blank">RFC 6750 Section 2: Authenticated
 * Requests</a>
 * @since 5.1
 */
public final class APIGatewayCompatibleBearerTokenResolver implements BearerTokenResolver {

  // APIGatewayCompatibleBearerTokenResolver divergence from DefaultBearerTokenResolver
  // source class was using pattern : "^Bearer (?<token>[a-zA-Z0-9-._~+/]+)=*$"
  private static final Pattern authorizationPattern = Pattern.compile("^(?<token>[a-zA-Z0-9-._~+/]+)=*$");

  private boolean allowFormEncodedBodyParameter = false;

  private boolean allowUriQueryParameter = false;

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolve(HttpServletRequest request) {
    String authorizationHeaderToken = resolveFromAuthorizationHeader(request);
    String parameterToken = resolveFromRequestParameters(request);
    if (authorizationHeaderToken != null) {
      if (parameterToken != null) {
        BearerTokenError error = new BearerTokenError(BearerTokenErrorCodes.INVALID_REQUEST,
                                                      HttpStatus.BAD_REQUEST,
                                                      "Found multiple bearer tokens in the request",
                                                      "https://tools.ietf.org/html/rfc6750#section-3.1");
        throw new OAuth2AuthenticationException(error);
      }
      return authorizationHeaderToken;
    } else if (parameterToken != null && isParameterTokenSupportedForRequest(request)) {
      return parameterToken;
    }
    return null;
  }

  /**
   * Set if transport of access token using form-encoded body parameter is supported. Defaults to {@code false}.
   *
   * @param allowFormEncodedBodyParameter if the form-encoded body parameter is supported
   */
  public void setAllowFormEncodedBodyParameter(boolean allowFormEncodedBodyParameter) {
    this.allowFormEncodedBodyParameter = allowFormEncodedBodyParameter;
  }

  /**
   * Set if transport of access token using URI query parameter is supported. Defaults to {@code false}.
   *
   * The spec recommends against using this mechanism for sending bearer tokens, and even goes as far as stating that it
   * was only included for completeness.
   *
   * @param allowUriQueryParameter if the URI query parameter is supported
   */
  public void setAllowUriQueryParameter(boolean allowUriQueryParameter) {
    this.allowUriQueryParameter = allowUriQueryParameter;
  }

  private static String resolveFromAuthorizationHeader(HttpServletRequest request) {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    // APIGatewayCompatibleBearerTokenResolver divergence from DefaultBearerTokenResolver
    // source class was also checking that the authorization string starts with "Bearer"
    if (StringUtils.hasText(authorization)) {
      Matcher matcher = authorizationPattern.matcher(authorization);

      if (!matcher.matches()) {
        BearerTokenError error = new BearerTokenError(BearerTokenErrorCodes.INVALID_TOKEN,
                                                      HttpStatus.UNAUTHORIZED,
                                                      "Bearer token is malformed",
                                                      "https://tools.ietf.org/html/rfc6750#section-3.1");
        throw new OAuth2AuthenticationException(error);
      }

      return matcher.group("token");
    }
    return null;
  }

  private static String resolveFromRequestParameters(HttpServletRequest request) {
    String[] values = request.getParameterValues("access_token");
    if (values == null || values.length == 0) {
      return null;
    }

    if (values.length == 1) {
      return values[0];
    }

    BearerTokenError error = new BearerTokenError(BearerTokenErrorCodes.INVALID_REQUEST,
                                                  HttpStatus.BAD_REQUEST,
                                                  "Found multiple bearer tokens in the request",
                                                  "https://tools.ietf.org/html/rfc6750#section-3.1");
    throw new OAuth2AuthenticationException(error);
  }

  private boolean isParameterTokenSupportedForRequest(HttpServletRequest request) {
    return ((this.allowFormEncodedBodyParameter && "POST".equals(request.getMethod()))
            || (this.allowUriQueryParameter && "GET".equals(request.getMethod())));
  }
}
