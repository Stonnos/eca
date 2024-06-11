package eca.client.instances;

import eca.client.dto.AccessToken;
import eca.client.dto.TokenResponse;
import eca.client.http.HttpRequestExecutor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Oauth2 token provider.
 *
 * @author Roman Batygin
 */
@Slf4j
public class Oauth2TokenProvider {

    private static final String BASIC_HEADER_FORMAT = "Basic %s";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String SCOPE = "scope";
    private static final String DATA_LOADER_API_SCOPE = "data-loader-api";

    /**
     * Token url
     */
    @Setter
    @Getter
    private String tokenUrl;

    /**
     * Client id
     */
    @Setter
    @Getter
    private String clientId;

    /**
     * Client secret
     */
    @Setter
    @Getter
    private String clientSecret;

    private final HttpRequestExecutor httpRequestExecutor = new HttpRequestExecutor();
    private AccessToken accessToken;

    /**
     * Gets access token.
     *
     * @return access token
     */
    public AccessToken getToken() {
        log.debug("Gets access token");
        if (accessToken == null || accessToken.isExpired()) {
            accessToken = obtainAccessToken();
        }
        log.debug("Valid access token has been fetched from cache");
        return accessToken;
    }

    private AccessToken obtainAccessToken() {
        log.info("Request to get access token from [{}]", tokenUrl);
        try {
            HttpPost httpUriRequest = createHttpRequest();
            TokenResponse tokenResponse = httpRequestExecutor.execute(httpUriRequest, TokenResponse.class);
            log.info("Access token has been fetched from [{}]", tokenUrl);
            LocalDateTime expireAt = LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn());
            return new AccessToken(tokenResponse.getAccessToken(), tokenResponse.getTokenType(), expireAt);
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private HttpPost createHttpRequest() throws URISyntaxException {
        HttpPost httpUriRequest = new HttpPost();
        httpUriRequest.setURI(new URI(tokenUrl));
        HttpEntity httpEntity = createHttpEntity();
        httpUriRequest.setEntity(httpEntity);
        httpUriRequest.setHeader(HttpHeaders.CONTENT_TYPE,
                ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        String authHeader = getBasicAuthorizationHeader();
        httpUriRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        return httpUriRequest;
    }

    private HttpEntity createHttpEntity() {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair(GRANT_TYPE_PARAM, CLIENT_CREDENTIALS));
        nameValuePairs.add(new BasicNameValuePair(SCOPE, DATA_LOADER_API_SCOPE));
        return new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8);
    }

    private String getBasicAuthorizationHeader() {
        byte[] credentialsAsBytes =
                String.format("%s:%s", clientId, clientSecret).getBytes(StandardCharsets.UTF_8);
        String base64Auth = Base64.getEncoder().encodeToString(credentialsAsBytes);
        return String.format(BASIC_HEADER_FORMAT, base64Auth);
    }
}
