package eca.client.http;

import eca.client.exception.WebClientResponseException;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Response handler impl.
 *
 * @param <T> - response generic type
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class ResponseHandlerImpl<T> implements ResponseHandler<T> {

    private static final int OK = 200;

    private final ResponseConverter responseConverter;

    private final Class<T> targetClass;

    @Override
    public T handleResponse(HttpResponse response) throws IOException {
        String responseBody = EntityUtils.toString(response.getEntity());
        if (response.getStatusLine().getStatusCode() != OK) {
            throw new WebClientResponseException(response.getStatusLine().getStatusCode(), responseBody,
                    "Web client error response");
        }
        return responseConverter.convert(responseBody, targetClass);
    }
}
