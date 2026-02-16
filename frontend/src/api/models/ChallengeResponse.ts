/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type ChallengeResponse = {
    id?: number;
    title?: string;
    description?: string;
    difficulty?: ChallengeResponse.difficulty;
    starterCode?: string;
    sampleTests?: string;
    tags?: Array<string>;
    createdAt?: string;
};
export namespace ChallengeResponse {
    export enum difficulty {
        EASY = 'EASY',
        MEDIUM = 'MEDIUM',
        HARD = 'HARD',
    }
}

