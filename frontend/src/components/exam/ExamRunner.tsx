import { useState, useEffect, useCallback } from 'react';
import { Clock, ChevronLeft, ChevronRight, AlertCircle, Check, X } from 'lucide-react';
import { QuestionDisplay } from '../learning/QuestionDisplay';
import api from '../../services/api';

interface ExamQuestion {
  id: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  correctAnswer: string;
  category: string;
  difficulty: number;
}

interface ExamConfig {
  id: string;
  title: string;
  duration: number;
  questions: number;
  categories?: string[];
}

interface ExamAnswer {
  questionId: number;
  selectedAnswer: string;
  isCorrect?: boolean;
}

interface ExamRunnerProps {
  exam: ExamConfig;
  onComplete: (result: ExamResult) => void;
  onCancel: () => void;
}

interface ExamResult {
  totalQuestions: number;
  correctAnswers: number;
  wrongAnswers: number;
  score: number;
  timeSpent: number;
  answers: ExamAnswer[];
}

export const ExamRunner: React.FC<ExamRunnerProps> = ({ exam, onComplete, onCancel }) => {
  const [questions, setQuestions] = useState<ExamQuestion[]>([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<Map<number, ExamAnswer>>(new Map());
  const [timeRemaining, setTimeRemaining] = useState(exam.duration * 60); // in seconds
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [showWarning, setShowWarning] = useState(false);

  // Lade Prüfungsfragen
  useEffect(() => {
    loadExamQuestions();
  }, []);

  // Timer
  useEffect(() => {
    if (timeRemaining <= 0) {
      handleSubmit();
      return;
    }

    if (timeRemaining === 300) { // 5 Minuten Warnung
      setShowWarning(true);
      setTimeout(() => setShowWarning(false), 5000);
    }

    const timer = setInterval(() => {
      setTimeRemaining(prev => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [timeRemaining]);

  const loadExamQuestions = async () => {
    try {
      setLoading(true);
      const response = await api.get('/exam/questions', {
        params: {
          count: exam.questions,
          categories: exam.categories?.join(',')
        }
      });
      setQuestions(response.data);
    } catch (error) {
      console.error('Failed to load exam questions:', error);
      // Fallback: Lade normale Fragen
      try {
        const response = await api.get('/questions/random', {
          params: { count: exam.questions }
        });
        setQuestions(response.data);
      } catch (fallbackError) {
        console.error('Fallback also failed:', fallbackError);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleAnswerSelect = (answer: string) => {
    const currentQuestion = questions[currentQuestionIndex];
    const examAnswer: ExamAnswer = {
      questionId: currentQuestion.id,
      selectedAnswer: answer
    };
    
    const newAnswers = new Map(answers);
    newAnswers.set(currentQuestion.id, examAnswer);
    setAnswers(newAnswers);
  };

  const handleNextQuestion = () => {
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(prev => prev + 1);
    }
  };

  const handlePreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(prev => prev - 1);
    }
  };

  const handleQuestionNavigate = (index: number) => {
    setCurrentQuestionIndex(index);
  };

  const handleSubmit = async () => {
    if (answers.size < questions.length) {
      const confirmSubmit = window.confirm(
        `Du hast nur ${answers.size} von ${questions.length} Fragen beantwortet. Möchtest du wirklich abgeben?`
      );
      if (!confirmSubmit) return;
    }

    setSubmitting(true);

    // Berechne Ergebnisse
    const answersArray = Array.from(answers.values());
    let correctCount = 0;

    // Prüfe Antworten
    answersArray.forEach(answer => {
      const question = questions.find(q => q.id === answer.questionId);
      if (question) {
        answer.isCorrect = answer.selectedAnswer === question.correctAnswer;
        if (answer.isCorrect) correctCount++;
      }
    });

    const result: ExamResult = {
      totalQuestions: questions.length,
      correctAnswers: correctCount,
      wrongAnswers: questions.length - correctCount,
      score: Math.round((correctCount / questions.length) * 100),
      timeSpent: exam.duration * 60 - timeRemaining,
      answers: answersArray
    };

    // Sende Ergebnis an Backend
    try {
      await api.post('/exam/submit', {
        examId: exam.id,
        answers: answersArray,
        timeSpent: result.timeSpent
      });
    } catch (error) {
      console.error('Failed to submit exam:', error);
    }

    onComplete(result);
  };

  const formatTime = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-gray-600">Prüfung wird geladen...</p>
        </div>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="card text-center">
        <AlertCircle className="w-16 h-16 text-danger mx-auto mb-4" />
        <h2 className="text-xl font-bold text-gray-900 mb-2">
          Fehler beim Laden der Prüfung
        </h2>
        <p className="text-gray-600 mb-4">
          Die Prüfungsfragen konnten nicht geladen werden.
        </p>
        <button onClick={onCancel} className="btn-secondary">
          Zurück
        </button>
      </div>
    );
  }

  const currentQuestion = questions[currentQuestionIndex];
  const currentAnswer = answers.get(currentQuestion.id);

  return (
    <div className="max-w-6xl mx-auto">
      {/* Header mit Timer */}
      <div className="bg-white rounded-lg shadow-md p-4 mb-6">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{exam.title}</h2>
            <p className="text-gray-600">
              Frage {currentQuestionIndex + 1} von {questions.length}
            </p>
          </div>
          
          <div className={`flex items-center space-x-2 ${timeRemaining < 300 ? 'text-danger' : 'text-gray-700'}`}>
            <Clock className="w-6 h-6" />
            <span className="text-2xl font-mono font-bold">
              {formatTime(timeRemaining)}
            </span>
          </div>
        </div>

        {showWarning && (
          <div className="mt-4 bg-yellow-50 border border-yellow-200 rounded-lg p-3">
            <p className="text-yellow-800 font-medium">
              ⚠️ Nur noch 5 Minuten verbleibend!
            </p>
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Hauptbereich mit Frage */}
        <div className="lg:col-span-3">
          <div className="card">
            <div className="mb-4">
              <span className="inline-block px-3 py-1 bg-primary-100 text-primary-700 rounded-full text-sm">
                {currentQuestion.category}
              </span>
            </div>

            <h3 className="text-xl font-semibold text-gray-900 mb-6">
              {currentQuestion.questionText}
            </h3>

            <div className="space-y-3">
              {['A', 'B', 'C', 'D'].map((option) => {
                const optionKey = `option${option}` as keyof ExamQuestion;
                const optionText = currentQuestion[optionKey] as string;
                const isSelected = currentAnswer?.selectedAnswer === option;

                return (
                  <button
                    key={option}
                    onClick={() => handleAnswerSelect(option)}
                    className={`w-full text-left p-4 rounded-lg border-2 transition-all ${
                      isSelected
                        ? 'border-primary bg-primary-50'
                        : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50'
                    }`}
                  >
                    <div className="flex items-start">
                      <span className={`font-semibold mr-3 ${
                        isSelected ? 'text-primary' : 'text-gray-600'
                      }`}>
                        {option}.
                      </span>
                      <span className={isSelected ? 'text-primary-700' : 'text-gray-700'}>
                        {optionText}
                      </span>
                    </div>
                  </button>
                );
              })}
            </div>

            {/* Navigation */}
            <div className="flex justify-between items-center mt-8">
              <button
                onClick={handlePreviousQuestion}
                disabled={currentQuestionIndex === 0}
                className="btn-secondary disabled:opacity-50"
              >
                <ChevronLeft className="w-5 h-5 mr-1" />
                Vorherige
              </button>

              {currentQuestionIndex === questions.length - 1 ? (
                <button
                  onClick={handleSubmit}
                  disabled={submitting}
                  className="btn-primary"
                >
                  {submitting ? 'Wird abgegeben...' : 'Prüfung abgeben'}
                </button>
              ) : (
                <button
                  onClick={handleNextQuestion}
                  className="btn-primary"
                >
                  Nächste
                  <ChevronRight className="w-5 h-5 ml-1" />
                </button>
              )}
            </div>
          </div>
        </div>

        {/* Seitenleiste mit Fragenübersicht */}
        <div className="lg:col-span-1">
          <div className="card">
            <h3 className="font-semibold text-gray-900 mb-4">Fragenübersicht</h3>
            
            <div className="grid grid-cols-5 gap-2 mb-4">
              {questions.map((_, index) => {
                const question = questions[index];
                const answer = answers.get(question.id);
                const isAnswered = !!answer;
                const isCurrent = index === currentQuestionIndex;

                return (
                  <button
                    key={index}
                    onClick={() => handleQuestionNavigate(index)}
                    className={`
                      aspect-square rounded-lg font-medium text-sm
                      ${isCurrent ? 'ring-2 ring-primary ring-offset-2' : ''}
                      ${isAnswered 
                        ? 'bg-primary text-white' 
                        : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                      }
                    `}
                  >
                    {index + 1}
                  </button>
                );
              })}
            </div>

            <div className="space-y-2 text-sm">
              <div className="flex items-center justify-between">
                <span className="text-gray-600">Beantwortet:</span>
                <span className="font-semibold">
                  {answers.size} / {questions.length}
                </span>
              </div>
              
              <div className="flex items-center justify-between">
                <span className="text-gray-600">Fortschritt:</span>
                <span className="font-semibold">
                  {Math.round((answers.size / questions.length) * 100)}%
                </span>
              </div>
            </div>

            <div className="mt-6 pt-6 border-t">
              <button
                onClick={onCancel}
                className="btn-secondary w-full text-sm"
              >
                Prüfung abbrechen
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};