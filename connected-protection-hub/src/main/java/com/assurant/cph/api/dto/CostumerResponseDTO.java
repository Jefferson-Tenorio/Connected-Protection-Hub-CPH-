package com.assurant.cph.api.dto;

import java.util.UUID;

public record CostumerResponseDTO (
    UUID id,
    String name,
    String email
){}
