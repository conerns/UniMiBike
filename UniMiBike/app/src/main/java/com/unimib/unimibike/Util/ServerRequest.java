package com.unimib.unimibike.Util;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
public class ServerRequest {
        static final int NO_CONNECTION = 550;
        static final int GENERIC_ERROR = 600;
        private static final int DEFAULT_TIMEOUT = 10000;
        static final int TIMEOUT_ERROR = 408;

        private static ServerRequest mInstance;
        private RequestQueue mRequestQueue;
        private static Context mContext;

        private ServerRequest(Context context) {
            mContext = context;
            mRequestQueue = getRequestQueue();
        }

        static synchronized ServerRequest getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new ServerRequest(context);
            }
            return mInstance;
        }

        private RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
            }
            return mRequestQueue;
        }

        private <T> void addToRequestQueue(Request<T> request) {
            getRequestQueue().add(request);
        }

        void getBasicRequest(String url, final NetworkCallback<JSONObject> networkCallback) {
            Log.d("basic request-", "non entra");
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    networkCallback.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    networkCallback.onError(getErrorStatusCode(error), null);
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            ServerRequest.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
        }

        void postBasicRequest(String url, JSONObject jsonObject, final NetworkCallback<JSONObject> networkCallback) {
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("postBasicRequest", response.toString());
                    networkCallback.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    networkCallback.onError(getErrorStatusCode(error), null);
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            ServerRequest.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
        }

        void putBasicRequest(String url, JSONObject jsonObject, final NetworkCallback<JSONObject> networkCallback) {
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    networkCallback.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    networkCallback.onError(getErrorStatusCode(error), null);
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            ServerRequest.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
        }

        // Return the Status Code value of a Volley Error
        private int getErrorStatusCode(VolleyError error) {
            if (error instanceof NoConnectionError)
                return NO_CONNECTION;
            else if (error instanceof TimeoutError)
                return TIMEOUT_ERROR;
            else
                return GENERIC_ERROR;
        }

    }

