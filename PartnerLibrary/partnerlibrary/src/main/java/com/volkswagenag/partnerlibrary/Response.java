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
         * Partner application failed validation
         */
        VALIDATION_FAILURE,
        /**
         * Partner application doesnot have permission to access this method API
         */
        NO_PERMISSION_TO_ACCESS_API,
        /**
         * Low level initialization failed
         */
        INITIALIZATION_FAILURE,
        /**
         * Other internal failure in the partner service
         */
        INTERNAL_FAILURE,
    };

    Error error;
    T value;
}
