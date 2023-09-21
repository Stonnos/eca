package eca.client.http;

import eca.client.dto.AccessToken;
import eca.client.instances.Oauth2TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

/**
 * Oauth2 token interceptor.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class Oauth2TokenInterceptor implements HttpRequestInterceptor {

    private final Oauth2TokenProvider oauth2TokenProvider;

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) {
        AccessToken accessToken = oauth2TokenProvider.getToken();
        String authHeader = String.format("%s %s", accessToken.getTokenType(), accessToken.getAccessToken());
        httpRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }
}