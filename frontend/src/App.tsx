import { useState, useEffect } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import { ChallengeList } from './pages/ChallengeList';
import { ChallengeDetail } from './pages/ChallengeDetail';

interface AuthState {
  username: string;
  email: string;
  acceptedChallengeIds: number[];
}

function App() {
  const [auth, setAuth] = useState<AuthState | null>(null);
  const navigate = useNavigate();

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

  const handleChallengeClick = (challengeId: number) => {
    navigate(`/challenge?id=${challengeId}`);
  };

  const handleBack = () => {
    navigate('/');
  };

  return (
    <Routes>
      <Route
        path="/"
        element={
          <ChallengeList
            auth={auth}
            onLogout={handleLogout}
            onAuthSuccess={handleAuthSuccess}
            onChallengeClick={handleChallengeClick}
          />
        }
      />
      <Route
        path="/challenge"
        element={
          <ChallengeDetail
            auth={auth}
            onBack={handleBack}
            onLogout={handleLogout}
            onSubmissionAccepted={addAcceptedChallenge}
            isAccepted={auth?.acceptedChallengeIds || []}
          />
        }
      />
    </Routes>
  );
}

export default App;
