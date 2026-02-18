package com.edi.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeExecutionResult {
    private Integer statusId;
    private String stdout;
    private String stderr;
    private String compileOutput;
    private String time;
    private Integer memory;
}
