package com.volkswagenag.partnerlibrary;

/**
 * Response for all the APIs
 * @param <T> Type of the response for that specific API
 */
public class Response<T> {
    /**
     * Errors that happen during API call
     */
    public enum Error {
        /**
         * Success
         */
        NONE,
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
    public Error error;
    public T value;

    public Response(Error error) {
        this.error = error;
    }

    public Response(Error error, T value) {
        this.error = error;
        this.value = value;
    }
}
