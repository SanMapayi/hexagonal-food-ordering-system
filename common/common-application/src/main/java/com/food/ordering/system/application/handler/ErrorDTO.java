package com.food.ordering.system.application.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ErrorDTO {
    private final String statusCode;
    private final String message;
}
