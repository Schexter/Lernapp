import { Layout } from '../components/layout/Layout';
import { ProgressRing } from '../components/ui/ProgressRing';
import { Trophy, Target, TrendingUp, Calendar } from 'lucide-react';

export const ProgressPage = () => {
  const categoryProgress = [
    { name: 'Netzwerktechnik', progress: 75, total: 127, correct: 95 },
    { name: 'Datenbanken', progress: 60, total: 89, correct: 53 },
    { name: 'Programmierung', progress: 90, total: 156, correct: 140 },
    { name: 'IT-Sicherheit', progress: 45, total: 78, correct: 35 },
    { name: 'Betriebssysteme', progress: 80, total: 94, correct: 75 },
    { name: 'Projektmanagement', progress: 30, total: 45, correct: 14 }
  ];

  const weeklyProgress = [
    { day: 'Mo', questions: 15, correct: 12 },
    { day: 'Di', questions: 22, correct: 18 },
    { day: 'Mi', questions: 8, correct: 6 },
    { day: 'Do', questions: 30, correct: 25 },
    { day: 'Fr', questions: 18, correct: 16 },
    { day: 'Sa', questions: 25, correct: 22 },
    { day: 'So', questions: 12, correct: 10 }
  ];

  const overallProgress = Math.round(
    categoryProgress.reduce((acc, cat) => acc + cat.progress, 0) / categoryProgress.length
  );

  return (
    <Layout>
      <div className="p-6 max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Lernfortschritt
          </h1>
          <p className="text-gray-600">
            Verfolge deinen Lernfortschritt und identifiziere Verbesserungsbereiche.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="card text-center">
            <ProgressRing progress={overallProgress} size="lg" className="mx-auto mb-4" />
            <h3 className="text-lg font-semibold text-gray-900 mb-1">
              Gesamtfortschritt
            </h3>
            <p className="text-sm text-gray-600">
              Alle Kategorien kombiniert
            </p>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <Trophy className="w-8 h-8 text-accent" />
              <span className="text-2xl font-bold text-gray-900">85%</span>
            </div>
            <h3 className="text-sm font-medium text-gray-600">Durchschnittsnote</h3>
            <p className="text-xs text-gray-500 mt-1">
              538 von 632 Fragen richtig
            </p>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <Target className="w-8 h-8 text-primary" />
              <span className="text-2xl font-bold text-gray-900">632</span>
            </div>
            <h3 className="text-sm font-medium text-gray-600">Fragen beantwortet</h3>
            <p className="text-xs text-gray-500 mt-1">
              von 589 total verfügbaren
            </p>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <TrendingUp className="w-8 h-8 text-warning" />
              <span className="text-2xl font-bold text-gray-900">14</span>
            </div>
            <h3 className="text-sm font-medium text-gray-600">Tage Lernsträhne</h3>
            <p className="text-xs text-gray-500 mt-1">
              Weiter so!
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <div className="card">
            <h2 className="text-xl font-semibold mb-6">Fortschritt nach Kategorie</h2>
            <div className="space-y-4">
              {categoryProgress.map((category) => (
                <div key={category.name}>
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-sm font-medium text-gray-700">
                      {category.name}
                    </span>
                    <span className="text-sm text-gray-600">
                      {category.correct}/{category.total} ({category.progress}%)
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3">
                    <div 
                      className="bg-primary h-3 rounded-full transition-all duration-300"
                      style={{ width: `${category.progress}%` }}
                    ></div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="card">
            <h2 className="text-xl font-semibold mb-6">Wochenübersicht</h2>
            <div className="space-y-4">
              {weeklyProgress.map((day) => (
                <div key={day.day} className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="w-8 h-8 bg-primary-50 rounded-full flex items-center justify-center">
                      <span className="text-sm font-medium text-primary">
                        {day.day}
                      </span>
                    </div>
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {day.questions} Fragen
                      </div>
                      <div className="text-xs text-gray-500">
                        {day.correct} richtig
                      </div>
                    </div>
                  </div>
                  <div className="text-sm font-medium text-gray-600">
                    {Math.round((day.correct / day.questions) * 100)}%
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold">Empfehlungen</h2>
            <Calendar className="w-6 h-6 text-gray-400" />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-red-50 border border-red-200 rounded-lg p-4">
              <h3 className="font-semibold text-red-800 mb-2">Schwachstelle</h3>
              <p className="text-sm text-red-700">
                <strong>Projektmanagement</strong> benötigt mehr Aufmerksamkeit. 
                Nur 30% der Fragen wurden beantwortet.
              </p>
            </div>
            
            <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
              <h3 className="font-semibold text-yellow-800 mb-2">Wiederholung</h3>
              <p className="text-sm text-yellow-700">
                <strong>IT-Sicherheit</strong> Fragen aus den letzten 2 Wochen 
                sollten wiederholt werden.
              </p>
            </div>
            
            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <h3 className="font-semibold text-green-800 mb-2">Stärke</h3>
              <p className="text-sm text-green-700">
                <strong>Programmierung</strong> läuft sehr gut! 
                90% Fortschritt mit hoher Genauigkeit.
              </p>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};