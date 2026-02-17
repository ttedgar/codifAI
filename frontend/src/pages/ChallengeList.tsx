import { useEffect, useState } from 'react';
import { ChallengesService, ChallengeResponse } from '../api';
import { ChallengeCard } from '../components/ChallengeCard';
import { AuthForm } from '../components/AuthForm';

interface ChallengeListProps {
  auth: { username: string; email: string; acceptedChallengeIds: number[] } | null;
  onLogout: () => void;
  onAuthSuccess: (username: string, email: string, acceptedChallengeIds?: number[]) => void;
  onChallengeClick: (challenge: ChallengeResponse) => void;
}

export function ChallengeList({ auth, onLogout, onAuthSuccess, onChallengeClick }: ChallengeListProps) {
  const [challenges, setChallenges] = useState<ChallengeResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadChallenges();
  }, []);

  const loadChallenges = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await ChallengesService.getAllChallenges();
      setChallenges(response.content || []);
    } catch (err) {
      setError('Failed to load challenges. Please try again later.');
      console.error('Error loading challenges:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950">
      {/* Header */}
      <header className="bg-white dark:bg-gray-900 shadow-sm border-b border-gray-200 dark:border-gray-800">
        <div className="container mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
                Codif<span className="text-CodePurple">AI</span>
              </h1>
              <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                AI-generated coding challenges to sharpen your skills
              </p>
            </div>
            <div className="flex items-center gap-6">
              <button className="px-4 py-2 bg-CodePurple hover:bg-CodeDarkPurple text-white rounded-lg font-medium transition-colors">
                Generate Challenge
              </button>

              {/* Auth Section */}
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
                <AuthForm onAuthSuccess={onAuthSuccess} />
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        {/* Stats Bar */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 text-center">
            <div className="text-3xl font-bold text-CodePurple dark:text-CodeLightPurple mb-2">
              {challenges.length}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400">
              Total Challenges
            </div>
          </div>
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 text-center">
            <div className="text-3xl font-bold text-green-600 dark:text-green-400 mb-2">
              {challenges.filter((c) => c.difficulty === 'EASY').length}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400">
              Easy Challenges
            </div>
          </div>
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 text-center">
            <div className="text-3xl font-bold text-red-600 dark:text-red-400 mb-2">
              {challenges.filter((c) => c.difficulty === 'HARD').length}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400">
              Hard Challenges
            </div>
          </div>
        </div>

        {/* Challenges Grid */}
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
            Available Challenges
          </h2>

          {/* Loading State */}
          {loading && (
            <div className="flex justify-center items-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-CodePurple"></div>
            </div>
          )}

          {/* Error State */}
          {error && (
            <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-6 text-center">
              <svg
                className="w-12 h-12 text-red-600 dark:text-red-400 mx-auto mb-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              <p className="text-red-700 dark:text-red-300 font-medium">
                {error}
              </p>
              <button
                onClick={loadChallenges}
                className="mt-4 px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-medium transition-colors"
              >
                Try Again
              </button>
            </div>
          )}

          {/* Empty State */}
          {!loading && !error && challenges.length === 0 && (
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-12 text-center">
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
                  d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                />
              </svg>
              <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                No challenges yet
              </h3>
              <p className="text-gray-600 dark:text-gray-400 mb-4">
                Get started by generating your first AI challenge!
              </p>
              <button className="px-6 py-3 bg-CodePurple hover:bg-CodeDarkPurple text-white rounded-lg font-medium transition-colors">
                Generate Challenge
              </button>
            </div>
          )}

          {/* Challenges Grid */}
          {!loading && !error && challenges.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {challenges.map((challenge) => (
                <ChallengeCard
                  key={challenge.id}
                  challenge={challenge}
                  onClick={() => onChallengeClick(challenge)}
                  isAccepted={auth ? auth.acceptedChallengeIds.includes(challenge.id!) : false}
                />
              ))}
            </div>
          )}
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
