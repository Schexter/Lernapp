import { Link } from 'react-router-dom';
import { LoginForm } from '../components/auth/LoginForm';
import { BookOpen } from 'lucide-react';

export const LoginPage = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-white flex items-center justify-center px-4">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <BookOpen className="w-12 h-12 text-primary" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Willkommen zurück!
          </h1>
          <p className="text-gray-600">
            Melde dich an, um mit dem Lernen fortzufahren
          </p>
        </div>
        
        <div className="card">
          <LoginForm />
          
          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Noch kein Konto?{' '}
              <Link to="/register" className="text-primary hover:text-primary-600 font-medium">
                Jetzt registrieren
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};