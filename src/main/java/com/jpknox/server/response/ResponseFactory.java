package com.jpknox.server.response;

/**
 * Created by joaok on 25/12/2017.
 * This enforces the interface of a response factory. All responses from the
 * factory will come in the form of a {@code String}, both the content and
 * format of the {@code String} will be delegated to the factory. The response
 * is requested by providing a standard {@code code} parameter relating to
 * each type of response.
 */
public interface ResponseFactory {

    String createResponse(int code);

    String createResponse(int code, String... param);

}
