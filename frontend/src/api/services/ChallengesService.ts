/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ChallengeRequest } from '../models/ChallengeRequest';
import type { ChallengeResponse } from '../models/ChallengeResponse';
import type { PageResponseChallengeResponse } from '../models/PageResponseChallengeResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ChallengesService {
    /**
     * Get all challenges
     * Returns paginated list of all challenges
     * @param page
     * @param size
     * @param sortBy
     * @returns PageResponseChallengeResponse OK
     * @throws ApiError
     */
    public static getAllChallenges(
        page?: number,
        size: number = 100,
        sortBy: string = 'createdAt',
    ): CancelablePromise<PageResponseChallengeResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/challenges',
            query: {
                'page': page,
                'size': size,
                'sortBy': sortBy,
            },
        });
    }
    /**
     * Create a new challenge
     * Triggers AI to generate a new challenge (requires authentication)
     * @param requestBody
     * @returns ChallengeResponse OK
     * @throws ApiError
     */
    public static createChallenge(
        requestBody: ChallengeRequest,
    ): CancelablePromise<ChallengeResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/challenges',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * Get challenge by ID
     * Returns a single challenge by its ID
     * @param id
     * @returns ChallengeResponse OK
     * @throws ApiError
     */
    public static getChallengeById(
        id: number,
    ): CancelablePromise<ChallengeResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/challenges/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Delete challenge
     * Deletes a challenge (requires ADMIN role)
     * @param id
     * @returns any OK
     * @throws ApiError
     */
    public static deleteChallenge(
        id: number,
    ): CancelablePromise<any> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/challenges/{id}',
            path: {
                'id': id,
            },
        });
    }
}
