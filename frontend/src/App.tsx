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
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const email = localStorage.getItem('email');
    const acceptedChallengeIdsStr = localStorage.getItem('acceptedChallengeIds');

    if (token && username && email) {
      validateToken(token, username, email, acceptedChallengeIdsStr)
        .catch(() => {
          localStorage.removeItem('token');
          localStorage.removeItem('username');
          localStorage.removeItem('email');
          localStorage.removeItem('acceptedChallengeIds');
          setAuth(null);
        });
    }
  }, []);

  const validateToken = async (
    token: string,
    username: string,
    email: string,
    acceptedChallengeIdsStr: string | null
  ) => {
    try {
      const response = await fetch('http://localhost:8080/api/challenges?page=0&size=1', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.status === 401 || response.status === 403) {
        throw new Error('Token expired');
      }

      setAuth({
        username,
        email,
        acceptedChallengeIds: acceptedChallengeIdsStr ? JSON.parse(acceptedChallengeIdsStr) : [],
      });
    } catch (error) {
      throw error;
    }
  };

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
