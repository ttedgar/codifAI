import { useState, useEffect } from 'react';
import { ChallengeList } from './pages/ChallengeList';
import { ChallengeDetail } from './pages/ChallengeDetail';
import { ChallengeResponse } from './api';

interface AuthState {
  username: string;
  email: string;
}

function App() {
  const [auth, setAuth] = useState<AuthState | null>(null);
  const [currentPage, setCurrentPage] = useState<'list' | 'detail'>('list');
  const [selectedChallenge, setSelectedChallenge] = useState<ChallengeResponse | null>(null);

  useEffect(() => {
    // Check if user is already logged in
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const email = localStorage.getItem('email');

    if (token && username && email) {
      setAuth({ username, email });
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    setAuth(null);
  };

  const handleAuthSuccess = (username: string, email: string) => {
    setAuth({ username, email });
  };

  const handleChallengeClick = (challenge: ChallengeResponse) => {
    setSelectedChallenge(challenge);
    setCurrentPage('detail');
  };

  const handleBack = () => {
    setCurrentPage('list');
    setSelectedChallenge(null);
  };

  return (
    <>
      {currentPage === 'list' ? (
        <ChallengeList
          auth={auth}
          onLogout={handleLogout}
          onAuthSuccess={handleAuthSuccess}
          onChallengeClick={handleChallengeClick}
        />
      ) : (
        <ChallengeDetail
          challenge={selectedChallenge!}
          auth={auth}
          onBack={handleBack}
          onLogout={handleLogout}
        />
      )}
    </>
  );
}

export default App;
