package com.assurant.cph.api.dto;

public record CustomerRequestDTO(
        String name,
        String email,
        String documentId
) {}
