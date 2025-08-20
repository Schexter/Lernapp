import { Link } from 'react-router-dom';
import { Home, ArrowLeft } from 'lucide-react';

export const NotFoundPage = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="max-w-md mx-auto text-center">
        <div className="mb-8">
          <h1 className="text-9xl font-bold text-primary mb-4">404</h1>
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">
            Seite nicht gefunden
          </h2>
          <p className="text-gray-600">
            Die angeforderte Seite konnte nicht gefunden werden. 
            Möglicherweise wurde sie verschoben oder gelöscht.
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