import { Link } from 'react-router-dom';
import { BookOpen, Trophy, Users, ChevronRight } from 'lucide-react';

export const LandingPage = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-white">
      <header className="container mx-auto px-4 py-6">
        <nav className="flex justify-between items-center">
          <div className="flex items-center space-x-2">
            <BookOpen className="w-8 h-8 text-primary" />
            <span className="text-2xl font-bold text-gray-900">FI-Lernapp</span>
          </div>
          <div className="space-x-4">
            <Link to="/login" className="btn-secondary">
              Anmelden
            </Link>
            <Link to="/register" className="btn-primary">
              Registrieren
            </Link>
          </div>
        </nav>
      </header>

      <main className="container mx-auto px-4 py-16">
        <section className="text-center mb-16">
          <h1 className="text-5xl font-bold text-gray-900 mb-6">
            Dein Weg zur erfolgreichen AP1 & AP2
          </h1>
          <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
            Brandneue Lernplattform mit über 1000 aktuellen Prüfungsfragen. 
            Mit der bewährten 60%-Erfolgsstrategie effizient zum Ziel!
          </p>
          <Link to="/register" className="btn-primary text-lg px-8 py-4">
            Jetzt kostenlos durchstarten
            <ChevronRight className="ml-2 w-5 h-5 inline" />
          </Link>
        </section>

        <section className="grid md:grid-cols-3 gap-8 mb-16">
          <div className="card text-center">
            <div className="flex justify-center mb-4">
              <BookOpen className="w-12 h-12 text-primary" />
            </div>
            <h3 className="text-xl font-semibold mb-2">Strukturiertes Lernen</h3>
            <p className="text-gray-600">
              Alle Themengebiete der AP1 und AP2 systematisch aufbereitet
            </p>
          </div>
          <div className="card text-center">
            <div className="flex justify-center mb-4">
              <Trophy className="w-12 h-12 text-accent" />
            </div>
            <h3 className="text-xl font-semibold mb-2">Prüfungssimulation</h3>
            <p className="text-gray-600">
              Realistische Prüfungsbedingungen mit Timer und Bewertung
            </p>
          </div>
          <div className="card text-center">
            <div className="flex justify-center mb-4">
              <Users className="w-12 h-12 text-warning" />
            </div>
            <h3 className="text-xl font-semibold mb-2">Fortschrittsverfolgung</h3>
            <p className="text-gray-600">
              Behalte deinen Lernfortschritt immer im Blick
            </p>
          </div>
        </section>

        <section className="bg-white rounded-2xl shadow-xl p-12 text-center">
          <h2 className="text-3xl font-bold mb-6">Starte jetzt deine Lernreise!</h2>
          <p className="text-lg text-gray-600 mb-8">
            Eine neue Lernplattform mit über 1000 Fragen wartet auf dich. 
            Sei einer der Ersten und bereite dich optimal auf deine Prüfung vor!
          </p>
          <div className="flex justify-center space-x-4">
            <Link to="/register" className="btn-primary text-lg px-6 py-3">
              Jetzt kostenlos starten
            </Link>
            <Link to="/login" className="btn-secondary">
              Bereits registriert? Anmelden
            </Link>
          </div>
        </section>
      </main>
    </div>
  );
};