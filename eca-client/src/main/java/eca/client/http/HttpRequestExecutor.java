package eca.client.http;

import eca.client.exception.WebClientErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Http request interceptor.
 *
 * @author Roman Batygin
 */
@Slf4j
public class HttpRequestExecutor {

    private final List<HttpRequestInterceptor> httpRequestInterceptors = newArrayList();
    private final ResponseConverter responseConverter = new JsonResponseConverter();

    /**
     * Adds http request interceptor.
     *
     * @param httpRequestInterceptor - http request interceptor
     */
    public void addInterceptor(HttpRequestInterceptor httpRequestInterceptor) {
        this.httpRequestInterceptors.add(httpRequestInterceptor);
    }

    /**
     * Executes http request.
     *
     * @param httpUriRequest - http uri request
     * @param responseClass  - response class
     * @param <T>            - response generic type
     * @return response object
     */
    public <T> T execute(HttpUriRequest httpUriRequest, Class<T> responseClass) {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            return httpClient.execute(httpUriRequest, new ResponseHandlerImpl<>(responseConverter, responseClass));
        } catch (IOException ex) {
            log.error("Error while sent http request to url [{}]: {}", httpUriRequest.getURI(), ex.getMessage());
            throw new WebClientErrorException(ex.getMessage());
        }
    }

    private CloseableHttpClient createHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpRequestInterceptors.forEach(httpClientBuilder::addInterceptorLast);
        return httpClientBuilder.build();
    }
}
