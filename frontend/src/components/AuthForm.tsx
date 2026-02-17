import { useState } from 'react';
import { AuthenticationService } from '../api';

interface AuthFormProps {
  onAuthSuccess: (username: string, email: string, acceptedChallengeIds?: number[]) => void;
}

export function AuthForm({ onAuthSuccess }: AuthFormProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      if (mode === 'login') {
        const response = await AuthenticationService.login({
          email,
          password,
        });

        if (response.token) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('username', response.username || '');
          localStorage.setItem('email', response.email || '');
          localStorage.setItem(
            'acceptedChallengeIds',
            JSON.stringify(response.acceptedChallengeIds || [])
          );
          onAuthSuccess(
            response.username || '',
            response.email || '',
            response.acceptedChallengeIds
          );
          setIsOpen(false);
          // Reset form
          setEmail('');
          setPassword('');
          setUsername('');
        }
      } else {
        const response = await AuthenticationService.register({
          username,
          email,
          password,
        });

        if (response.token) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('username', response.username || '');
          localStorage.setItem('email', response.email || '');
          localStorage.setItem(
            'acceptedChallengeIds',
            JSON.stringify(response.acceptedChallengeIds || [])
          );
          onAuthSuccess(
            response.username || '',
            response.email || '',
            response.acceptedChallengeIds
          );
          setIsOpen(false);
          // Reset form
          setEmail('');
          setPassword('');
          setUsername('');
        }
      }
    } catch (err: any) {
      setError(err?.body?.message || 'Authentication failed. Please try again.');
      console.error('Auth error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="px-4 py-2 text-gray-700 dark:text-gray-300 hover:text-CodePurple dark:hover:text-CodeLightPurple font-medium transition-colors"
      >
        Sign In
      </button>

      {/* Dropdown Form */}
      {isOpen && (
        <>
          {/* Backdrop */}
          <div
            className="fixed inset-0 z-40"
            onClick={() => setIsOpen(false)}
          />

          {/* Form */}
          <div className="absolute right-0 mt-2 w-80 bg-white dark:bg-gray-800 rounded-lg shadow-xl border border-gray-200 dark:border-gray-700 p-6 z-50">
            {/* Mode Selector */}
            <div className="flex gap-4 mb-6">
              <button
                onClick={() => {
                  setMode('login');
                  setError(null);
                  setUsername('');
                }}
                className={`flex-1 py-2 px-3 rounded-lg font-medium transition-colors text-sm ${
                  mode === 'login'
                    ? 'bg-CodePurple text-white'
                    : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
                }`}
              >
                Login
              </button>
              <button
                onClick={() => {
                  setMode('register');
                  setError(null);
                }}
                className={`flex-1 py-2 px-3 rounded-lg font-medium transition-colors text-sm ${
                  mode === 'register'
                    ? 'bg-CodePurple text-white'
                    : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
                }`}
              >
                Register
              </button>
            </div>

            {/* Form Fields */}
            <form onSubmit={handleSubmit}>
              {/* Username (register only) */}
              {mode === 'register' && (
                <div className="mb-4">
                  <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    className="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-CodePurple"
                  />
                </div>
              )}

              {/* Email */}
              <div className="mb-4">
                <input
                  type="email"
                  placeholder="Email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-CodePurple"
                />
              </div>

              {/* Password */}
              <div className="mb-6">
                <input
                  type="password"
                  placeholder="Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  className="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-CodePurple"
                />
              </div>

              {/* Error Message */}
              {error && (
                <div className="mb-4 p-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
                  <p className="text-sm text-red-700 dark:text-red-300">{error}</p>
                </div>
              )}

              {/* Submit Button */}
              <button
                type="submit"
                disabled={loading}
                className="w-full py-2 px-3 bg-CodePurple hover:bg-CodeDarkPurple disabled:bg-gray-400 text-white font-medium rounded-lg transition-colors text-sm"
              >
                {loading ? 'Loading...' : mode === 'login' ? 'Login' : 'Register'}
              </button>
            </form>
          </div>
        </>
      )}
    </div>
  );
}
