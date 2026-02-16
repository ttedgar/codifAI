/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export type ChallengeRequest = {
    prompt: string;
    difficulty: ChallengeRequest.difficulty;
};
export namespace ChallengeRequest {
    export enum difficulty {
        EASY = 'EASY',
        MEDIUM = 'MEDIUM',
        HARD = 'HARD',
    }
}

