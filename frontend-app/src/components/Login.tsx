import React, { useState } from 'react';
import axios from 'axios';

const Login: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        username,
        password
      });
      
      // Token und User-Info speichern
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
      
      // Weiterleitung zum Dashboard
      window.location.href = '/dashboard';
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login fehlgeschlagen');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <div className="max-w-md w-full">
        <div className="bg-surface rounded-2xl shadow-xl overflow-hidden">
          {/* Header mit Gradient */}
          <div className="bg-gradient-to-r from-primary to-primary-dark p-8 text-white text-center">
            <h1 className="text-3xl font-bold mb-2">Willkommen zurück!</h1>
            <p className="text-white/90">Melde dich an, um weiterzulernen</p>
          </div>
          
          {/* Form Container */}
          <div className="p-8">
            {error && (
              <div className="mb-6 p-4 bg-incorrect-bg border border-danger/20 rounded-lg text-danger">
                {error}
              </div>
            )}
            
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label htmlFor="username" className="block text-gray-700 font-semibold mb-2">
                  Benutzername
                </label>
                <input
                  type="text"
                  id="username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-primary focus:outline-none transition-colors"
                  required
                  autoFocus
                />
              </div>
              
              <div>
                <label htmlFor="password" className="block text-gray-700 font-semibold mb-2">
                  Passwort
                </label>
                <input
                  type="password"
                  id="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-primary focus:outline-none transition-colors"
                  required
                />
              </div>
              
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-primary to-primary-dark text-white font-semibold py-3 px-4 rounded-lg hover:shadow-lg transform hover:-translate-y-0.5 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? 'Anmeldung läuft...' : 'Anmelden'}
              </button>
            </form>
            
            <div className="mt-6 text-center">
              <p className="text-gray-500">
                Noch kein Konto?{' '}
                <a href="/register" className="text-primary font-semibold hover:underline">
                  Jetzt registrieren
                </a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;