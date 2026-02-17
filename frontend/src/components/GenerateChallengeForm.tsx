import { useState } from 'react';
import { ChallengesService } from '../api';

interface GenerateChallengeFormProps {
  isAuthenticated: boolean;
  onChallengeGenerated: (challengeId: number) => void;
}

export function GenerateChallengeForm({ isAuthenticated, onChallengeGenerated }: GenerateChallengeFormProps) {
  const [prompt, setPrompt] = useState('');
  const [difficulty, setDifficulty] = useState<'EASY' | 'MEDIUM' | 'HARD'>('EASY');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!prompt.trim()) {
      setError('Please enter a challenge prompt');
      return;
    }

    setError(null);
    setLoading(true);

    try {
      const response = await ChallengesService.createChallenge({
        prompt,
        difficulty,
      });

      if (response.id) {
        // Clear form
        setPrompt('');
        setDifficulty('EASY');
        // Navigate to the new challenge
        onChallengeGenerated(response.id);
      }
    } catch (err: any) {
      setError(err?.body?.message || 'Failed to generate challenge. Please try again.');
      console.error('Generation error:', err);
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-6 mb-8 text-center">
        <p className="text-blue-700 dark:text-blue-300 font-medium">
          Log in to generate AI-powered challenges
        </p>
      </div>
    );
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-8">
      <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4">
        Generate a new AI challenge
      </h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Prompt Input */}
        <div>
          <label htmlFor="prompt" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Challenge Prompt
          </label>
          <textarea
            id="prompt"
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            disabled={loading}
            placeholder="e.g., 'Create a challenge about superheroes'"
            className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-CodePurple disabled:opacity-50 disabled:cursor-not-allowed"
            rows={3}
            minLength={10}
            maxLength={100}
          />
          <p className="mt-1 text-xs text-gray-500 dark:text-gray-400">
            {prompt.length}/100 characters
          </p>
        </div>

        {/* Difficulty Selector */}
        <div>
          <label htmlFor="difficulty" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Difficulty Level
          </label>
          <select
            id="difficulty"
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value as 'EASY' | 'MEDIUM' | 'HARD')}
            disabled={loading}
            className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-CodePurple disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>

        {/* Error Message */}
        {error && (
          <div className="p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
            <p className="text-sm text-red-700 dark:text-red-300">{error}</p>
          </div>
        )}

        {/* Submit Button */}
        <div className="space-y-2">
          <div className="flex gap-3">
            <button
              type="submit"
              disabled={loading || !prompt.trim()}
              className="flex-1 py-2 px-4 bg-CodePurple hover:bg-CodeDarkPurple disabled:bg-gray-400 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  Generating...
                </>
              ) : (
                'Generate Challenge'
              )}
            </button>
          </div>
          <p className="text-xs text-gray-500 dark:text-gray-400 text-center">
            Be patient, this could take some time!
          </p>
        </div>
      </form>
    </div>
  );
}
