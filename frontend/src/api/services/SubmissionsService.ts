/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { SubmissionRequest } from '../models/SubmissionRequest';
import type { SubmissionResponse } from '../models/SubmissionResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class SubmissionsService {
    /**
     * Submit code for evaluation
     * Submit code solution for a challenge (requires authentication)
     * @param requestBody
     * @returns SubmissionResponse OK
     * @throws ApiError
     */
    public static submitCode(
        requestBody: SubmissionRequest,
    ): CancelablePromise<SubmissionResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/submissions',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
