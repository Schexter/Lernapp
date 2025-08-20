import { Layout } from '../components/layout/Layout';
import { BookOpen, Play, Clock, Target } from 'lucide-react';

export const LearningPage = () => {
  return (
    <Layout>
      <div className="p-6 max-w-4xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Lernmodus
          </h1>
          <p className="text-gray-600">
            Wähle ein Themengebiet und starte dein Lernabenteuer!
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div className="card-interactive">
            <div className="flex items-center mb-4">
              <BookOpen className="w-8 h-8 text-primary mr-3" />
              <h3 className="text-xl font-semibold">Netzwerktechnik</h3>
            </div>
            <p className="text-gray-600 mb-4">
              Grundlagen der Netzwerktechnik, Protokolle und Topologien
            </p>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-500">127 Fragen</span>
              <button className="btn-primary">
                <Play className="w-4 h-4 mr-2" />
                Starten
              </button>
            </div>
          </div>

          <div className="card-interactive">
            <div className="flex items-center mb-4">
              <BookOpen className="w-8 h-8 text-accent mr-3" />
              <h3 className="text-xl font-semibold">Datenbanken</h3>
            </div>
            <p className="text-gray-600 mb-4">
              SQL, Datenbankdesign und Normalisierung
            </p>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-500">89 Fragen</span>
              <button className="btn-primary">
                <Play className="w-4 h-4 mr-2" />
                Starten
              </button>
            </div>
          </div>

          <div className="card-interactive">
            <div className="flex items-center mb-4">
              <BookOpen className="w-8 h-8 text-warning mr-3" />
              <h3 className="text-xl font-semibold">Programmierung</h3>
            </div>
            <p className="text-gray-600 mb-4">
              Java, Algorithmen und Datenstrukturen
            </p>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-500">156 Fragen</span>
              <button className="btn-primary">
                <Play className="w-4 h-4 mr-2" />
                Starten
              </button>
            </div>
          </div>

          <div className="card-interactive">
            <div className="flex items-center mb-4">
              <BookOpen className="w-8 h-8 text-danger mr-3" />
              <h3 className="text-xl font-semibold">IT-Sicherheit</h3>
            </div>
            <p className="text-gray-600 mb-4">
              Verschlüsselung, Firewalls und Sicherheitskonzepte
            </p>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-500">78 Fragen</span>
              <button className="btn-primary">
                <Play className="w-4 h-4 mr-2" />
                Starten
              </button>
            </div>
          </div>

          <div className="card-interactive">
            <div className="flex items-center mb-4">
              <BookOpen className="w-8 h-8 text-primary mr-3" />
              <h3 className="text-xl font-semibold">Betriebssysteme</h3>
            </div>
            <p className="text-gray-600 mb-4">
              Windows, Linux und Systemadministration
            </p>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-500">94 Fragen</span>
              <button className="btn-primary">
                <Play className="w-4 h-4 mr-2" />
                Starten
              </button>
            </div>
          </div>

          <div className="card-interactive">
            <div className="flex items-center mb-4">
              <BookOpen className="w-8 h-8 text-accent mr-3" />
              <h3 className="text-xl font-semibold">Projektmanagement</h3>
            </div>
            <p className="text-gray-600 mb-4">
              Agile Methoden, Scrum und Projektplanung
            </p>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-500">45 Fragen</span>
              <button className="btn-primary">
                <Play className="w-4 h-4 mr-2" />
                Starten
              </button>
            </div>
          </div>
        </div>

        <div className="mt-8 card">
          <h2 className="text-xl font-semibold mb-4">Lernstatistiken</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center">
              <Clock className="w-8 h-8 text-primary mx-auto mb-2" />
              <div className="text-2xl font-bold text-gray-900">2h 15m</div>
              <div className="text-sm text-gray-600">Heute gelernt</div>
            </div>
            <div className="text-center">
              <Target className="w-8 h-8 text-accent mx-auto mb-2" />
              <div className="text-2xl font-bold text-gray-900">85%</div>
              <div className="text-sm text-gray-600">Genauigkeit</div>
            </div>
            <div className="text-center">
              <BookOpen className="w-8 h-8 text-warning mx-auto mb-2" />
              <div className="text-2xl font-bold text-gray-900">245</div>
              <div className="text-sm text-gray-600">Fragen beantwortet</div>
            </div>
            <div className="text-center">
              <Play className="w-8 h-8 text-danger mx-auto mb-2" />
              <div className="text-2xl font-bold text-gray-900">7</div>
              <div className="text-sm text-gray-600">Tage Streak</div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};