package com.edi.backend.service;

import com.edi.backend.dto.CodeExecutionResult;
public interface CodeExecutor {
    CodeExecutionResult execute(String sourceCode, String stdin);
}
