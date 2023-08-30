package com.volkswagenag.partnerlibrary;

/**
 * Response type for all the APIs
 * @param <T> Type of the response for that specific API
 */
public class Response<T> {
    /**
     * Errors that happen during API call
     */
    public enum Status {
        /**
         * Success
         */
        SUCCESS,
        /**
         * Partner application failed validation and is denied permission to access the APIs
         */
        PERMISSION_DENIED,
        /**
         * Unable to find and connect to the service
         */
        SERVICE_CONNECTION_FAILURE,
        /**
         * Failure in communicating the data to the service
         */
        SERVICE_COMMUNICATION_FAILURE,
        /**
         * Value not available
         */
        VALUE_NOT_AVAILABLE,
        /**
         * Low level initialization failed
         */
        INITIALIZATION_FAILURE,
        /**
         * Other internal failure in the partner service
         */
        INTERNAL_FAILURE,
    };
    public Status status;
    public T value;

    public Response(Status status) {
        this.status = status;
    }

    public Response(Status status, T value) {
        this.status = status;
        this.value = value;
    }
}
