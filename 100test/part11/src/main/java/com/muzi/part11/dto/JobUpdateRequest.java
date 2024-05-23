package com.muzi.part11.dto;

import lombok.Data;


@Data
public class JobUpdateRequest extends JobCreateRequest {
    private String id;
}
