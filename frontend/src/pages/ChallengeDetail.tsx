import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { ChallengesService, ChallengeResponse, SubmissionsService, SubmissionResponse } from '../api';
import { AuthForm } from '../components/AuthForm';
import { Fireworks } from '../components/Fireworks';

interface ChallengeDetailProps {
  auth: { username: string; email: string } | null;
  onBack: () => void;
  onLogout: () => void;
  isAccepted?: number[];
  onSubmissionAccepted?: (challengeId: number) => void;
}

const statusColors = {
  PENDING: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300',
  ACCEPTED: 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400',
  WRONG_ANSWER: 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
  RUNTIME_ERROR: 'bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400',
  COMPILATION_ERROR: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400',
  TIME_LIMIT_EXCEEDED: 'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400',
};

export function ChallengeDetail({
  auth,
  onBack,
  onLogout,
  isAccepted = [],
  onSubmissionAccepted,
}: ChallengeDetailProps) {
  const [searchParams] = useSearchParams();
  const [challenge, setChallenge] = useState<ChallengeResponse | null>(null);
  const [code, setCode] = useState('');
  const [submission, setSubmission] = useState<SubmissionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const challengeId = searchParams.get('id');
    if (!challengeId) {
      setError('Challenge ID not provided');
      setLoading(false);
      return;
    }

    const fetchChallenge = async () => {
      try {
        setLoading(true);
        setError(null);
        const ch = await ChallengesService.getChallengeById(parseInt(challengeId));
        setChallenge(ch);
        setCode(ch.starterCode || '');
      } catch (err) {
        setError('Failed to load challenge. Please try again later.');
        console.error('Error loading challenge:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchChallenge();
  }, [searchParams]);

  const handleSubmit = async () => {
    if (!auth) {
      setError('You must be logged in to submit code');
      return;
    }

    setError(null);
    setSubmitting(true);

    try {
      const response = await SubmissionsService.submitCode({
        challengeId: challenge.id!,
        code,
      });
      setSubmission(response);
      if (response.status === 'ACCEPTED' && onSubmissionAccepted) {
        onSubmissionAccepted(challenge.id!);
      }
    } catch (err: any) {
      setError(err?.body?.message || 'Submission failed. Please try again.');
      console.error('Submission error:', err);
    } finally {
      setSubmitting(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'ACCEPTED':
        return (
          <svg
            className="w-4 h-4 text-green-600 dark:text-green-400"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
          </svg>
        );
      default:
        return (
          <svg
            className="w-4 h-4 text-red-600 dark:text-red-400"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
          </svg>
        );
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-950 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-CodePurple"></div>
      </div>
    );
  }

  if (error || !challenge) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-950">
        <header className="bg-white dark:bg-gray-900 shadow-sm border-b border-gray-200 dark:border-gray-800">
          <div className="container mx-auto px-4 py-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                <button
                  onClick={onBack}
                  className="px-4 py-2 text-gray-700 dark:text-gray-300 hover:text-CodePurple dark:hover:text-CodeLightPurple font-medium transition-colors"
                >
                  ← Back
                </button>
                <div>
                  <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                    Codif<span className="text-CodePurple">AI</span>
                  </h1>
                </div>
              </div>
            </div>
          </div>
        </header>
        <main className="container mx-auto px-4 py-8">
          <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-6 text-center">
            <p className="text-red-700 dark:text-red-300 font-medium">
              {error || 'Challenge not found'}
            </p>
            <button
              onClick={onBack}
              className="mt-4 px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-medium transition-colors"
            >
              Go Back
            </button>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950">
      {submission?.status === 'ACCEPTED' && <Fireworks />}
      {/* Header */}
      <header className="bg-white dark:bg-gray-900 shadow-sm border-b border-gray-200 dark:border-gray-800">
        <div className="container mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <button
                onClick={onBack}
                className="px-4 py-2 text-gray-700 dark:text-gray-300 hover:text-CodePurple dark:hover:text-CodeLightPurple font-medium transition-colors"
              >
                ← Back
              </button>
              <div>
                <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                  Codif<span className="text-CodePurple">AI</span>
                </h1>
              </div>
            </div>
            <div className="flex items-center gap-6">
              {auth ? (
                <div className="flex items-center gap-4">
                  <div className="text-sm text-gray-600 dark:text-gray-400">
                    Logged in as <span className="font-medium text-gray-900 dark:text-white">{auth.username}</span>
                  </div>
                  <button
                    onClick={onLogout}
                    className="px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:text-red-600 dark:hover:text-red-400 font-medium transition-colors"
                  >
                    Logout
                  </button>
                </div>
              ) : (
                <AuthForm onAuthSuccess={() => window.location.reload()} />
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        {/* Challenge Title and Info */}
        <div className="mb-8">
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-4">
              <h2 className="text-3xl font-bold text-gray-900 dark:text-white">
                {challenge.title}
              </h2>
              {isAccepted.includes(challenge.id!) && (
                <div className="flex items-center gap-2 px-3 py-1 bg-green-100 dark:bg-green-900/30 rounded-full">
                  <svg
                    className="w-5 h-5 text-green-600 dark:text-green-400"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                  <span className="text-sm font-medium text-green-700 dark:text-green-400">Solved</span>
                </div>
              )}
            </div>
            <span
              className={`px-4 py-2 text-sm font-medium rounded-full ${
                challenge.difficulty === 'EASY'
                  ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
                  : challenge.difficulty === 'MEDIUM'
                  ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'
                  : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
              }`}
            >
              {challenge.difficulty}
            </span>
          </div>

          {/* Description */}
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">
              Description
            </h3>
            <div className="prose prose-invert max-w-none text-gray-700 dark:text-gray-300 whitespace-pre-wrap">
              {challenge.description}
            </div>
          </div>

          {/* Sample Tests */}
          {challenge.sampleTests && (
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">
                Sample Tests
              </h3>
              <pre className="bg-gray-100 dark:bg-gray-900 rounded p-4 text-sm text-gray-700 dark:text-gray-300 overflow-auto max-h-48">
                {challenge.sampleTests}
              </pre>
            </div>
          )}
        </div>

        {/* Code Editor and Results */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Code Editor */}
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              Your Solution
            </h3>
            <textarea
              value={code}
              onChange={(e) => setCode(e.target.value)}
              disabled={!auth}
              className="w-full h-64 px-4 py-3 font-mono text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-gray-100 placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-CodePurple disabled:opacity-50 disabled:cursor-not-allowed"
              placeholder={!auth ? 'Log in to write code' : 'Write your code here...'}
            />

            {/* Error Message */}
            {error && (
              <div className="mt-4 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
                <p className="text-sm text-red-700 dark:text-red-300">{error}</p>
              </div>
            )}

            {/* Login Required Message */}
            {!auth && (
              <div className="mt-4 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
                <p className="text-sm text-red-700 dark:text-red-300">
                  You must be logged in to submit solutions
                </p>
              </div>
            )}

            {/* Submit Button */}
            <button
              onClick={handleSubmit}
              disabled={!auth || submitting}
              className="mt-4 w-full py-3 px-4 bg-CodePurple hover:bg-CodeDarkPurple disabled:bg-gray-400 text-white font-semibold rounded-lg transition-colors"
            >
              {submitting ? 'Submitting...' : 'Submit Solution'}
            </button>
          </div>

          {/* Results */}
          <div
            style={{
              borderColor: submission?.status === 'ACCEPTED'
                ? '#10b981'
                : submission?.status
                ? '#ef4444'
                : undefined,
              borderWidth: submission ? '4px' : undefined,
            }}
            className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6"
          >
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              Results
            </h3>

            {submitting ? (
              <div className="h-64 flex items-center justify-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-CodePurple"></div>
              </div>
            ) : !submission ? (
              <div className="h-64 flex items-center justify-center">
                <div className="text-center">
                  <svg
                    className="w-16 h-16 text-gray-400 mx-auto mb-4"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                  <p className="text-gray-600 dark:text-gray-400">
                    Submit your code to see results
                  </p>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                {/* Status */}
                <div className="flex items-center gap-3">
                  {getStatusIcon(submission.status!)}
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400">Status</p>
                    <span
                      className={`inline-block px-3 py-1 text-sm font-medium rounded-full ${
                        statusColors[submission.status as keyof typeof statusColors]
                      }`}
                    >
                      {submission.status}
                    </span>
                  </div>
                </div>

                {/* Execution Time */}
                {submission.executionTime !== undefined && (
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400">Execution Time</p>
                    <p className="text-gray-900 dark:text-white font-medium">
                      {submission.executionTime}ms
                    </p>
                  </div>
                )}

                {/* Memory */}
                {submission.memory !== undefined && (
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400">Memory Used</p>
                    <p className="text-gray-900 dark:text-white font-medium">
                      {submission.memory}MB
                    </p>
                  </div>
                )}

                {/* Standard Output */}
                {submission.stdout && (
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">Output</p>
                    <pre className="bg-gray-100 dark:bg-gray-900 rounded p-3 text-sm text-gray-700 dark:text-gray-300 overflow-auto max-h-32">
                      {submission.stdout}
                    </pre>
                  </div>
                )}

                {/* Standard Error */}
                {submission.stderr && (
                  <div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">Error Output</p>
                    <pre className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded p-3 text-sm text-red-700 dark:text-red-300 overflow-auto max-h-32">
                      {submission.stderr}
                    </pre>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-800 mt-12">
        <div className="container mx-auto px-4 py-6">
          <p className="text-center text-sm text-gray-600 dark:text-gray-400">
            © 2026 CodifAI - AI-powered coding challenge platform
          </p>
        </div>
      </footer>
    </div>
  );
}
