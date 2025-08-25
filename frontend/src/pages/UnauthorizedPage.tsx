import { Link } from 'react-router-dom';
import { Shield, Home, ArrowLeft } from 'lucide-react';

export const UnauthorizedPage = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="max-w-md mx-auto text-center">
        <div className="mb-8">
          <Shield className="w-24 h-24 text-danger mx-auto mb-6" />
          <h1 className="text-6xl font-bold text-danger mb-4">403</h1>
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">
            Zugriff verweigert
          </h2>
          <p className="text-gray-600">
            Du hast keine Berechtigung, auf diese Seite zuzugreifen. 
            Wende dich an einen Administrator, falls du glaubst, dass dies ein Fehler ist.
          </p>
        </div>
        
        <div className="space-y-4">
          <Link 
            to="/dashboard" 
            className="btn-primary inline-flex items-center"
          >
            <Home className="w-4 h-4 mr-2" />
            Zum Dashboard
          </Link>
          
          <div>
            <button 
              onClick={() => window.history.back()}
              className="btn-secondary inline-flex items-center"
            >
              <ArrowLeft className="w-4 h-4 mr-2" />
              Zurück
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};