package com.fixit.domain.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PendingAssignmentResponse {

    private Integer totalItems;
    private Boolean empty;
    private List<PendingAssignmentItemResponse> items;
}