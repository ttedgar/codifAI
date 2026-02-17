import { useState, useEffect } from 'react';
import { ChallengeList } from './pages/ChallengeList';
import { ChallengeDetail } from './pages/ChallengeDetail';
import { ChallengeResponse } from './api';

interface AuthState {
  username: string;
  email: string;
  acceptedChallengeIds: number[];
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
    const acceptedChallengeIdsStr = localStorage.getItem('acceptedChallengeIds');

    if (token && username && email) {
      setAuth({
        username,
        email,
        acceptedChallengeIds: acceptedChallengeIdsStr ? JSON.parse(acceptedChallengeIdsStr) : [],
      });
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    localStorage.removeItem('acceptedChallengeIds');
    setAuth(null);
  };

  const handleAuthSuccess = (
    username: string,
    email: string,
    acceptedChallengeIds: number[] = []
  ) => {
    setAuth({ username, email, acceptedChallengeIds });
  };

  const addAcceptedChallenge = (challengeId: number) => {
    if (auth) {
      const updated = [...auth.acceptedChallengeIds, challengeId];
      const updatedAuth = { ...auth, acceptedChallengeIds: updated };
      setAuth(updatedAuth);
      localStorage.setItem('acceptedChallengeIds', JSON.stringify(updated));
    }
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
          onSubmissionAccepted={addAcceptedChallenge}
          isAccepted={auth ? auth.acceptedChallengeIds.includes(selectedChallenge!.id!) : false}
        />
      )}
    </>
  );
}

export default App;
