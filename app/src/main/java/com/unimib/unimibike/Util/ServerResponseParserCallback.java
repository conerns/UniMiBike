package com.unimib.unimibike.Util;

public interface ServerResponseParserCallback<T> {
    void onSuccess(T response);
    void onError(String errorTitle, String errorMessage);
}
