import { Layout } from '../components/layout/Layout';
import { ProgressRing } from '../components/ui/ProgressRing';
import { BookOpen, Trophy, Clock, Target, FileQuestion } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';

export const DashboardPage = () => {
  const { user } = useAuthStore();

  // Mock data - später durch echte API-Daten ersetzen
  const stats = {
    questionsAnswered: 245,
    correctAnswers: 198,
    totalQuestions: 1000,
    studyStreak: 7,
    averageScore: 81,
    lastActivity: 'vor 2 Stunden'
  };

  const progress = Math.round((stats.questionsAnswered / stats.totalQuestions) * 100);

  return (
    <Layout>
      <div className="p-6 max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Willkommen zurück, {user?.firstName || user?.username}!
          </h1>
          <p className="text-gray-600 mt-2">
            Letzte Aktivität: {stats.lastActivity}
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <BookOpen className="w-8 h-8 text-primary" />
              <span className="text-2xl font-bold text-gray-900">
                {stats.questionsAnswered}
              </span>
            </div>
            <h3 className="text-sm font-medium text-gray-600">Fragen beantwortet</h3>
            <p className="text-xs text-gray-500 mt-1">
              von {stats.totalQuestions} insgesamt
            </p>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <Trophy className="w-8 h-8 text-accent" />
              <span className="text-2xl font-bold text-gray-900">
                {stats.averageScore}%
              </span>
            </div>
            <h3 className="text-sm font-medium text-gray-600">Durchschnittsnote</h3>
            <p className="text-xs text-gray-500 mt-1">
              {stats.correctAnswers} richtige Antworten
            </p>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <Clock className="w-8 h-8 text-warning" />
              <span className="text-2xl font-bold text-gray-900">
                {stats.studyStreak}
              </span>
            </div>
            <h3 className="text-sm font-medium text-gray-600">Tage Lernsträhne</h3>
            <p className="text-xs text-gray-500 mt-1">
              Bleib dran!
            </p>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <Target className="w-8 h-8 text-danger" />
              <span className="text-2xl font-bold text-gray-900">
                AP1
              </span>
            </div>
            <h3 className="text-sm font-medium text-gray-600">Nächste Prüfung</h3>
            <p className="text-xs text-gray-500 mt-1">
              in 42 Tagen
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <div className="card">
              <h2 className="text-xl font-semibold mb-4">Schnellstart</h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Link to="/learning" className="btn-primary text-center">
                  <BookOpen className="w-5 h-5 mr-2 inline" />
                  Weiterlernen
                </Link>
                <Link to="/exam" className="btn-secondary text-center">
                  <FileQuestion className="w-5 h-5 mr-2 inline" />
                  Prüfung starten
                </Link>
              </div>
            </div>

            <div className="card">
              <h2 className="text-xl font-semibold mb-4">Letzte Aktivitäten</h2>
              <div className="space-y-3">
                <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">Netzwerktechnik</p>
                    <p className="text-sm text-gray-600">25 Fragen beantwortet</p>
                  </div>
                  <span className="text-sm text-gray-500">vor 2 Stunden</span>
                </div>
                <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">Datenbanken</p>
                    <p className="text-sm text-gray-600">15 Fragen beantwortet</p>
                  </div>
                  <span className="text-sm text-gray-500">gestern</span>
                </div>
                <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">Programmierung</p>
                    <p className="text-sm text-gray-600">30 Fragen beantwortet</p>
                  </div>
                  <span className="text-sm text-gray-500">vor 2 Tagen</span>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-6">
            <div className="card text-center">
              <h2 className="text-xl font-semibold mb-4">Gesamtfortschritt</h2>
              <ProgressRing progress={progress} size="lg" />
              <p className="text-sm text-gray-600 mt-4">
                {stats.questionsAnswered} von {stats.totalQuestions} Fragen
              </p>
            </div>

            <div className="card">
              <h2 className="text-xl font-semibold mb-4">Kategorien</h2>
              <div className="space-y-3">
                <div>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-700">Netzwerktechnik</span>
                    <span className="text-gray-600">75%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div className="bg-primary h-2 rounded-full" style={{ width: '75%' }}></div>
                  </div>
                </div>
                <div>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-700">Datenbanken</span>
                    <span className="text-gray-600">60%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div className="bg-accent h-2 rounded-full" style={{ width: '60%' }}></div>
                  </div>
                </div>
                <div>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-700">Programmierung</span>
                    <span className="text-gray-600">90%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div className="bg-warning h-2 rounded-full" style={{ width: '90%' }}></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};