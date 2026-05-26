package com.menko.comptabilite.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Enveloppe standard de toutes les réponses API. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, data, "Créé avec succès");
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(true, null, message);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
