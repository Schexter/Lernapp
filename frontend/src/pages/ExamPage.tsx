import axios from 'axios';
import { Layout } from '../components/layout/Layout';
import { Clock, FileQuestion, Trophy, AlertCircle } from 'lucide-react';
import { useState, useEffect } from 'react';
import { ExamRunner } from '../components/exam/ExamRunner';
import { ExamResult } from '../components/exam/ExamResult';

interface ExamResultData {
  totalQuestions: number;
  correctAnswers: number;
  wrongAnswers: number;
  score: number;
  timeSpent: number;
  answers: any[];
}

export const ExamPage = () => {
  // Exam Stats from Backend
  const [examStats, setExamStats] = useState({
    bestScore: 0,
    averageTime: 0,
    examsThisMonth: 0
  });

  useEffect(() => {
    fetchExamStats();
  }, []);

  const fetchExamStats = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('/api/exam/stats', {
        headers: token ? { 'Authorization': `Bearer ${token}` } : {}
      });
      setExamStats(response.data);
    } catch (error) {
      console.error('Failed to fetch exam stats:', error);
    }
  };

  const [selectedExam, setSelectedExam] = useState<string | null>(null);
  const [examStarted, setExamStarted] = useState(false);
  const [examResult, setExamResult] = useState<ExamResultData | null>(null);

  const exams = [
    {
      id: 'ap1-mock',
      title: 'AP1 Probeklausur',
      description: 'Vollständige Simulation der Abschlussprüfung Teil 1',
      duration: 90,
      questions: 40,
      difficulty: 'Schwer',
      color: 'text-danger'
    },
    {
      id: 'netzwerk-exam',
      title: 'Netzwerktechnik Prüfung',
      description: 'Spezialisierte Prüfung zu Netzwerktechnologien',
      duration: 45,
      questions: 25,
      difficulty: 'Mittel',
      color: 'text-primary'
    },
    {
      id: 'database-exam',
      title: 'Datenbank Prüfung',
      description: 'SQL und Datenbankdesign im Fokus',
      duration: 60,
      questions: 30,
      difficulty: 'Mittel',
      color: 'text-accent'
    },
    {
      id: 'programming-exam',
      title: 'Programmierung Prüfung',
      description: 'Java-Grundlagen und Algorithmen',
      duration: 75,
      questions: 35,
      difficulty: 'Schwer',
      color: 'text-warning'
    }
  ];

  const handleExamComplete = (result: ExamResultData) => {
    setExamResult(result);
    setExamStarted(false);
    // Stats nach Prüfung neu laden
    fetchExamStats();
  };

  const handleExamCancel = () => {
    setExamStarted(false);
    setSelectedExam(null);
  };

  const handleRetryExam = () => {
    setExamResult(null);
    setExamStarted(true);
  };

  const handleGoHome = () => {
    setExamResult(null);
    setExamStarted(false);
    setSelectedExam(null);
  };

  // Zeige Prüfungsergebnis
  if (examResult && selectedExam) {
    const exam = exams.find(e => e.id === selectedExam);
    return (
      <Layout>
        <div className="p-6">
          <ExamResult
            result={examResult}
            examTitle={exam?.title || 'Prüfung'}
            onRetry={handleRetryExam}
            onGoHome={handleGoHome}
          />
        </div>
      </Layout>
    );
  }

  // Zeige laufende Prüfung
  if (examStarted && selectedExam) {
    const exam = exams.find(e => e.id === selectedExam);
    if (!exam) return null;

    return (
      <Layout>
        <div className="p-6">
          <ExamRunner
            exam={exam}
            onComplete={handleExamComplete}
            onCancel={handleExamCancel}
          />
        </div>
      </Layout>
    );
  }

  // Zeige Prüfungsvorschau
  if (selectedExam) {
    const exam = exams.find(e => e.id === selectedExam);
    return (
      <Layout>
        <div className="p-6 max-w-4xl mx-auto">
          <div className="card text-center">
            <AlertCircle className="w-16 h-16 text-warning mx-auto mb-4" />
            <h1 className="text-2xl font-bold text-gray-900 mb-2">
              Prüfung: {exam?.title}
            </h1>
            <p className="text-gray-600 mb-6">
              Du startest gleich eine {exam?.duration}-minütige Prüfung mit {exam?.questions} Fragen.
            </p>

            <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
              <h3 className="font-semibold text-yellow-800 mb-2">Wichtige Hinweise:</h3>
              <ul className="text-sm text-yellow-700 text-left space-y-1">
                <li>• Die Prüfung kann nicht pausiert werden</li>
                <li>• Alle Fragen müssen beantwortet werden</li>
                <li>• Das Zeitlimit ist strikt einzuhalten</li>
                <li>• Das Ergebnis wird sofort angezeigt</li>
              </ul>
            </div>

            <div className="flex justify-center space-x-4">
              <button
                onClick={() => setSelectedExam(null)}
                className="btn-secondary"
              >
                Zurück
              </button>
              <button
                onClick={() => setExamStarted(true)}
                className="btn-primary"
              >
                <FileQuestion className="w-5 h-5 mr-2" />
                Prüfung starten
              </button>
            </div>
          </div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="p-6 max-w-6xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Prüfungsmodus
          </h1>
          <p className="text-gray-600">
            Teste dein Wissen unter Prüfungsbedingungen mit Timer und vollständiger Bewertung.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          {exams.map((exam) => (
            <div key={exam.id} className="card-interactive">
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h3 className="text-xl font-semibold text-gray-900 mb-2">
                    {exam.title}
                  </h3>
                  <p className="text-gray-600 text-sm">
                    {exam.description}
                  </p>
                </div>
                <FileQuestion className={`w-8 h-8 ${exam.color}`} />
              </div>

              <div className="grid grid-cols-3 gap-4 mb-6">
                <div className="text-center">
                  <Clock className="w-5 h-5 text-gray-400 mx-auto mb-1" />
                  <div className="text-sm font-medium text-gray-900">{exam.duration} Min</div>
                  <div className="text-xs text-gray-500">Dauer</div>
                </div>
                <div className="text-center">
                  <FileQuestion className="w-5 h-5 text-gray-400 mx-auto mb-1" />
                  <div className="text-sm font-medium text-gray-900">{exam.questions}</div>
                  <div className="text-xs text-gray-500">Fragen</div>
                </div>
                <div className="text-center">
                  <Trophy className="w-5 h-5 text-gray-400 mx-auto mb-1" />
                  <div className="text-sm font-medium text-gray-900">{exam.difficulty}</div>
                  <div className="text-xs text-gray-500">Level</div>
                </div>
              </div>

              <button
                onClick={() => setSelectedExam(exam.id)}
                className="btn-primary w-full"
              >
                Prüfung auswählen
              </button>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="card text-center">
            <Trophy className="w-12 h-12 text-accent mx-auto mb-4" />
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              Beste Leistung
            </h3>
            <div className="text-3xl font-bold text-accent mb-1">{examStats.bestScore}%</div>
            <div className="text-sm text-gray-600">AP1 Probeklausur</div>
          </div>

          <div className="card text-center">
            <Clock className="w-12 h-12 text-primary mx-auto mb-4" />
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              Durchschnittliche Zeit
            </h3>
            <div className="text-3xl font-bold text-primary mb-1">{examStats.averageTime}</div>
            <div className="text-sm text-gray-600">Minuten pro Prüfung</div>
          </div>

          <div className="card text-center">
            <FileQuestion className="w-12 h-12 text-warning mx-auto mb-4" />
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              Absolvierte Prüfungen
            </h3>
            <div className="text-3xl font-bold text-warning mb-1">{examStats.examsThisMonth}</div>
            <div className="text-sm text-gray-600">Diesen Monat</div>
          </div>
        </div>
      </div>
    </Layout>
  );
};
