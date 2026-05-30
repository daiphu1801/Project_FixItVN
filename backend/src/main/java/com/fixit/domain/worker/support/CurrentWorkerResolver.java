package com.fixit.domain.worker.support;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentWorkerResolver {

    private final HttpServletRequest request;

    public UUID getCurrentWorkerId() {
        String value = request.getHeader("X-Debug-Worker-Id");

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Thiếu header X-Debug-Worker-Id trong môi trường dev");
        }

        return UUID.fromString(value);
    }
}