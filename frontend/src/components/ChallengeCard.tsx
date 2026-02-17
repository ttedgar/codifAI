import { ChallengeResponse } from '../api';

interface ChallengeCardProps {
  challenge: ChallengeResponse;
  onClick?: () => void;
  isAccepted?: boolean;
}

const difficultyColors: Record<string, string> = {
  EASY: 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400',
  MEDIUM: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400',
  HARD: 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
};

export function ChallengeCard({ challenge, onClick, isAccepted }: ChallengeCardProps) {
  return (
    <div
      onClick={onClick}
      className="bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-xl transition-all duration-300 p-6 cursor-pointer border border-gray-200 dark:border-gray-700 hover:border-CodePurple dark:hover:border-CodePurple group"
    >
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white group-hover:text-CodePurple dark:group-hover:text-CodeLightPurple transition-colors">
          {challenge.title}
        </h3>
        <div className="flex items-center gap-2 ml-2">
          {isAccepted && (
            <div className="bg-green-100 dark:bg-green-900/30 rounded-full p-1.5">
              <svg
                className="w-4 h-4 text-green-600 dark:text-green-400"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
            </div>
          )}
          <span
            className={`px-3 py-1 text-xs font-medium rounded-full whitespace-nowrap ${
              difficultyColors[challenge.difficulty || 'EASY']
            }`}
          >
            {challenge.difficulty || 'EASY'}
          </span>
        </div>
      </div>

      {/* Description Preview */}
      <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-3 mb-4">
        {challenge.description?.replace(/[#*`]/g, '').substring(0, 150)}...
      </p>

      {/* Tags */}
      {challenge.tags && challenge.tags.length > 0 && (
        <div className="flex flex-wrap gap-2 mb-4">
          {challenge.tags.slice(0, 3).map((tag, index) => (
            <span
              key={index}
              className="px-2 py-1 text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded"
            >
              {tag}
            </span>
          ))}
          {challenge.tags.length > 3 && (
            <span className="px-2 py-1 text-xs font-medium text-gray-500 dark:text-gray-400">
              +{challenge.tags.length - 3} more
            </span>
          )}
        </div>
      )}

      {/* Footer */}
      <div className="flex items-center justify-between pt-4 border-t border-gray-100 dark:border-gray-700">
        <div className="flex items-center space-x-2">
          <svg
            className="w-4 h-4 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
            />
          </svg>
          <span className="text-xs text-gray-500 dark:text-gray-400">
            {challenge.createdAt
              ? new Date(challenge.createdAt).toLocaleDateString()
              : 'Recently added'}
          </span>
        </div>
        <div className="text-xs font-medium text-CodePurple dark:text-CodeLightPurple group-hover:underline">
          View Challenge →
        </div>
      </div>
    </div>
  );
}
