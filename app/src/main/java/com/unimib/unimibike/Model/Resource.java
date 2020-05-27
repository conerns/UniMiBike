package com.unimib.unimibike.Model;

/**
 * Generic class that can be used to contain the data and the status of request.
 *
 * @param <T> The type of data associated with the request.
 */
public class Resource<T> {

    private T data;
    private int totalResults;
    private int statusCode;
    private String statusMessage;

    public Resource() {}

    public Resource(T data, int totalResults, int statusCode, String statusMessage) {
        this.data = data;
        this.totalResults = totalResults;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "data=" + data +
                ", totalResults=" + totalResults +
                ", statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
