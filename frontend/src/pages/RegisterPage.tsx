import { Link } from 'react-router-dom';
import { RegisterForm } from '../components/auth/RegisterForm';
import { BookOpen } from 'lucide-react';

export const RegisterPage = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-white flex items-center justify-center px-4 py-8">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <BookOpen className="w-12 h-12 text-primary" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Erstelle dein Konto
          </h1>
          <p className="text-gray-600">
            Starte jetzt deine Prüfungsvorbereitung
          </p>
        </div>
        
        <div className="card">
          <RegisterForm />
          
          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Bereits registriert?{' '}
              <Link to="/login" className="text-primary hover:text-primary-600 font-medium">
                Zur Anmeldung
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};