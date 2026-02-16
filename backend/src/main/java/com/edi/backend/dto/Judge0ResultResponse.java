package com.edi.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Judge0ResultResponse {
    private Judge0Status status;
    private String stdout;
    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    private String time;
    private Integer memory;

    @JsonProperty("exit_code")
    private Integer exitCode;

    @Data
    public static class Judge0Status {
        private Integer id;
        private String description;
    }
}
