import { useState, useEffect } from 'react';
import { Layout } from '../components/layout/Layout';
import { QuestionDisplay } from '../components/learning/QuestionDisplay';
import { learningService } from '../services/learningService';
import { BookOpen, Target, TrendingUp, Award, CheckCircle, XCircle } from 'lucide-react';
import type { Question, AnswerResponse } from '../types/learning';
import { useAuthStore } from '../stores/authStore';

export const LearningPage = () => {
  const { user, updateUser } = useAuthStore();
  const [currentQuestion, setCurrentQuestion] = useState<Question | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedDifficulty, setSelectedDifficulty] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [feedback, setFeedback] = useState<AnswerResponse | null>(null);
  const [categories, setCategories] = useState<string[]>([]);
  const [sessionStats, setSessionStats] = useState({
    questionsAnswered: 0,
    correctAnswers: 0,
    currentStreak: 0,
    pointsEarned: 0
  });

  useEffect(() => {
    loadCategories();
    loadNextQuestion();
  }, []);

  useEffect(() => {
    if (!feedback) {
      loadNextQuestion();
    }
  }, [selectedCategory, selectedDifficulty]);

  const loadCategories = async () => {
    try {
      const cats = await learningService.getCategories();
      setCategories(cats);
    } catch (error) {
      console.error('Error loading categories:', error);
    }
  };

  const loadNextQuestion = async () => {
    setLoading(true);
    setFeedback(null);
    try {
      const question = await learningService.getNextQuestion(
        selectedCategory !== 'all' ? selectedCategory : undefined,
        selectedDifficulty || undefined
      );
      setCurrentQuestion(question);
    } catch (error) {
      console.error('Error loading question:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAnswer = async (answer: string) => {
    if (!currentQuestion) return;

    try {
      const response = await learningService.submitAnswer(currentQuestion.id, answer);
      setFeedback(response);
      
      // Update session stats
      setSessionStats(prev => ({
        questionsAnswered: prev.questionsAnswered + 1,
        correctAnswers: prev.correctAnswers + (response.correct ? 1 : 0),
        currentStreak: response.correct ? prev.currentStreak + 1 : 0,
        pointsEarned: prev.pointsEarned + response.pointsEarned
      }));
      
      // Update user stats in store
      if (user) {
        const updatedUser = {
          ...user,
          totalQuestionsAnswered: (user.totalQuestionsAnswered || 0) + 1,
          correctAnswers: (user.correctAnswers || 0) + (response.correct ? 1 : 0),
          currentStreak: response.correct ? (user.currentStreak || 0) + 1 : 0,
          bestStreak: response.correct && ((user.currentStreak || 0) + 1 > (user.bestStreak || 0)) 
            ? (user.currentStreak || 0) + 1 
            : (user.bestStreak || 0),
          experiencePoints: (user.experiencePoints || 0) + response.pointsEarned,
          level: 1 + Math.floor(((user.experiencePoints || 0) + response.pointsEarned) / 100)
        };
        updateUser(updatedUser);
      }
    } catch (error) {
      console.error('Error submitting answer:', error);
    }
  };

  const handleNextQuestion = () => {
    loadNextQuestion();
  };

  return (
    <Layout>
      <div className="max-w-7xl mx-auto p-6">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Lernmodus</h1>
          <p className="text-gray-600">
            Beantworte Fragen und verbessere dein Wissen für die Prüfung
          </p>
        </div>

        {/* Session Stats Bar */}
        <div className="bg-gradient-to-r from-primary to-accent rounded-lg p-4 mb-6">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-white">
            <div className="text-center">
              <div className="text-2xl font-bold">{sessionStats.questionsAnswered}</div>
              <div className="text-sm opacity-90">Fragen beantwortet</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold">
                {sessionStats.questionsAnswered > 0 
                  ? Math.round((sessionStats.correctAnswers / sessionStats.questionsAnswered) * 100)
                  : 0}%
              </div>
              <div className="text-sm opacity-90">Genauigkeit</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold">{sessionStats.currentStreak}</div>
              <div className="text-sm opacity-90">Aktuelle Serie</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold">{sessionStats.pointsEarned}</div>
              <div className="text-sm opacity-90">Punkte verdient</div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Learning Area */}
          <div className="lg:col-span-2">
            {/* Category and Difficulty Selector */}
            <div className="card mb-6">
              <div className="flex flex-wrap gap-4">
                <div className="flex-1 min-w-[200px]">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Kategorie
                  </label>
                  <select
                    value={selectedCategory}
                    onChange={(e) => setSelectedCategory(e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                  >
                    <option value="all">Alle Kategorien</option>
                    {categories.map(cat => (
                      <option key={cat} value={cat}>{cat}</option>
                    ))}
                  </select>
                </div>
                <div className="flex-1 min-w-[200px]">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Schwierigkeit
                  </label>
                  <select
                    value={selectedDifficulty || ''}
                    onChange={(e) => setSelectedDifficulty(e.target.value ? Number(e.target.value) : null)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                  >
                    <option value="">Alle Schwierigkeiten</option>
                    <option value="1">Einfach</option>
                    <option value="2">Mittel</option>
                    <option value="3">Schwer</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Question Display */}
            {loading ? (
              <div className="card flex items-center justify-center h-96">
                <div className="text-center">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
                  <p className="text-gray-600">Lade nächste Frage...</p>
                </div>
              </div>
            ) : currentQuestion ? (
              <QuestionDisplay
                question={currentQuestion}
                onAnswer={handleAnswer}
                feedback={feedback}
                onNext={handleNextQuestion}
              />
            ) : (
              <div className="card flex items-center justify-center h-96">
                <div className="text-center">
                  <BookOpen className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600">Keine Fragen verfügbar</p>
                  <button
                    onClick={loadNextQuestion}
                    className="btn-primary mt-4"
                  >
                    Erneut versuchen
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Stats Sidebar */}
          <div className="space-y-6">
            {/* Quick Actions */}
            <div className="card">
              <h3 className="text-lg font-semibold mb-4">Schnellauswahl</h3>
              <div className="space-y-2">
                <button
                  onClick={() => {
                    setSelectedDifficulty(1);
                    setSelectedCategory('all');
                  }}
                  className="w-full btn-secondary text-left flex items-center justify-between"
                >
                  <span>Einfache Fragen</span>
                  <Target className="w-4 h-4" />
                </button>
                <button
                  onClick={() => {
                    setSelectedDifficulty(2);
                    setSelectedCategory('all');
                  }}
                  className="w-full btn-secondary text-left flex items-center justify-between"
                >
                  <span>Mittlere Fragen</span>
                  <TrendingUp className="w-4 h-4" />
                </button>
                <button
                  onClick={() => {
                    setSelectedDifficulty(3);
                    setSelectedCategory('all');
                  }}
                  className="w-full btn-secondary text-left flex items-center justify-between"
                >
                  <span>Schwere Fragen</span>
                  <Award className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Session Results */}
            {sessionStats.questionsAnswered > 0 && (
              <div className="card">
                <h3 className="text-lg font-semibold mb-4">Diese Sitzung</h3>
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600">Richtig:</span>
                    <div className="flex items-center">
                      <CheckCircle className="w-4 h-4 text-green-500 mr-1" />
                      <span className="font-semibold">{sessionStats.correctAnswers}</span>
                    </div>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-gray-600">Falsch:</span>
                    <div className="flex items-center">
                      <XCircle className="w-4 h-4 text-red-500 mr-1" />
                      <span className="font-semibold">
                        {sessionStats.questionsAnswered - sessionStats.correctAnswers}
                      </span>
                    </div>
                  </div>
                  <div className="pt-3 border-t">
                    <div className="flex items-center justify-between">
                      <span className="text-gray-600">Erfolgsrate:</span>
                      <span className="font-bold text-lg">
                        {Math.round((sessionStats.correctAnswers / sessionStats.questionsAnswered) * 100)}%
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Tips */}
            <div className="card bg-blue-50 border-blue-200">
              <h3 className="text-lg font-semibold mb-2 text-blue-900">💡 Tipp</h3>
              <p className="text-sm text-blue-800">
                Nimm dir Zeit für jede Frage. Es ist besser, gründlich zu verstehen, 
                als schnell zu antworten. Die Erklärungen helfen dir beim Lernen!
              </p>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};