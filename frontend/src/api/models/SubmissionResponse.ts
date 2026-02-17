/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type SubmissionResponse = {
    id?: number;
    userId?: number;
    challengeId?: number;
    status?: SubmissionResponse.status;
    stdout?: string;
    stderr?: string;
    executionTime?: number;
    memory?: number;
    createdAt?: string;
};
export namespace SubmissionResponse {
    export enum status {
        PENDING = 'PENDING',
        ACCEPTED = 'ACCEPTED',
        WRONG_ANSWER = 'WRONG_ANSWER',
        RUNTIME_ERROR = 'RUNTIME_ERROR',
        COMPILATION_ERROR = 'COMPILATION_ERROR',
        TIME_LIMIT_EXCEEDED = 'TIME_LIMIT_EXCEEDED',
    }
}

