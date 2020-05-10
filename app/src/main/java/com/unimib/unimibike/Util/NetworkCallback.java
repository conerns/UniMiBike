package com.unimib.unimibike.Util;

import org.json.JSONObject;

public interface NetworkCallback<T> {
    void onSuccess(T response);
    void onError(int statusCode, JSONObject cache);
}
