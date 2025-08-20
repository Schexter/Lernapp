import { Trophy, X, Check, Clock, Target, TrendingUp } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface ExamAnswer {
  questionId: number;
  selectedAnswer: string;
  isCorrect?: boolean;
}

interface ExamResult {
  totalQuestions: number;
  correctAnswers: number;
  wrongAnswers: number;
  score: number;
  timeSpent: number;
  answers: ExamAnswer[];
}

interface ExamResultProps {
  result: ExamResult;
  examTitle: string;
  onRetry: () => void;
  onGoHome: () => void;
}

export const ExamResult: React.FC<ExamResultProps> = ({ 
  result, 
  examTitle, 
  onRetry, 
  onGoHome 
}) => {
  const navigate = useNavigate();
  
  const formatTime = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const getGrade = (score: number) => {
    if (score >= 90) return { grade: 'Sehr gut', color: 'text-green-600' };
    if (score >= 80) return { grade: 'Gut', color: 'text-green-500' };
    if (score >= 70) return { grade: 'Befriedigend', color: 'text-blue-600' };
    if (score >= 60) return { grade: 'Ausreichend', color: 'text-yellow-600' };
    return { grade: 'Nicht bestanden', color: 'text-red-600' };
  };

  const { grade, color } = getGrade(result.score);
  const passed = result.score >= 60;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="card">
        {/* Header */}
        <div className="text-center mb-8">
          <div className={`inline-flex items-center justify-center w-24 h-24 rounded-full mb-4 ${
            passed ? 'bg-green-100' : 'bg-red-100'
          }`}>
            {passed ? (
              <Trophy className={`w-12 h-12 ${passed ? 'text-green-600' : 'text-red-600'}`} />
            ) : (
              <X className="w-12 h-12 text-red-600" />
            )}
          </div>
          
          <h2 className="text-3xl font-bold text-gray-900 mb-2">
            {examTitle}
          </h2>
          
          <div className={`text-5xl font-bold ${color} mb-2`}>
            {result.score}%
          </div>
          
          <p className={`text-xl font-semibold ${color}`}>
            {grade}
          </p>
        </div>

        {/* Statistiken */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-gray-50 rounded-lg p-4 text-center">
            <Check className="w-8 h-8 text-green-500 mx-auto mb-2" />
            <div className="text-2xl font-bold text-gray-900">
              {result.correctAnswers}
            </div>
            <div className="text-sm text-gray-600">Richtig</div>
          </div>
          
          <div className="bg-gray-50 rounded-lg p-4 text-center">
            <X className="w-8 h-8 text-red-500 mx-auto mb-2" />
            <div className="text-2xl font-bold text-gray-900">
              {result.wrongAnswers}
            </div>
            <div className="text-sm text-gray-600">Falsch</div>
          </div>
          
          <div className="bg-gray-50 rounded-lg p-4 text-center">
            <Clock className="w-8 h-8 text-blue-500 mx-auto mb-2" />
            <div className="text-2xl font-bold text-gray-900">
              {formatTime(result.timeSpent)}
            </div>
            <div className="text-sm text-gray-600">Zeit</div>
          </div>
          
          <div className="bg-gray-50 rounded-lg p-4 text-center">
            <Target className="w-8 h-8 text-purple-500 mx-auto mb-2" />
            <div className="text-2xl font-bold text-gray-900">
              {result.totalQuestions}
            </div>
            <div className="text-sm text-gray-600">Fragen</div>
          </div>
        </div>

        {/* Fortschrittsbalken */}
        <div className="mb-8">
          <div className="flex justify-between text-sm text-gray-600 mb-2">
            <span>Dein Ergebnis</span>
            <span>{result.correctAnswers} von {result.totalQuestions} richtig</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-4 overflow-hidden">
            <div 
              className={`h-full rounded-full transition-all duration-1000 ${
                passed ? 'bg-green-500' : 'bg-red-500'
              }`}
              style={{ width: `${result.score}%` }}
            />
          </div>
        </div>

        {/* Feedback */}
        <div className={`p-4 rounded-lg mb-8 ${
          passed ? 'bg-green-50 border border-green-200' : 'bg-yellow-50 border border-yellow-200'
        }`}>
          <h3 className={`font-semibold mb-2 ${
            passed ? 'text-green-800' : 'text-yellow-800'
          }`}>
            {passed ? '🎉 Glückwunsch!' : '💪 Nicht aufgeben!'}
          </h3>
          <p className={passed ? 'text-green-700' : 'text-yellow-700'}>
            {passed 
              ? `Du hast die Prüfung erfolgreich bestanden! Mit ${result.score}% hast du gezeigt, dass du den Stoff gut beherrschst.`
              : `Mit ${result.score}% hast du die Prüfung leider nicht bestanden. Die Mindestanforderung liegt bei 60%. Nutze die Lernmodule, um dich gezielt zu verbessern!`
            }
          </p>
        </div>

        {/* Empfehlungen */}
        {!passed && (
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-8">
            <h3 className="font-semibold text-blue-800 mb-2">
              <TrendingUp className="w-5 h-5 inline mr-2" />
              Empfohlene nächste Schritte
            </h3>
            <ul className="text-sm text-blue-700 space-y-1">
              <li>• Wiederhole die Themen, bei denen du Fehler gemacht hast</li>
              <li>• Nutze den Lernmodus für gezieltes Training</li>
              <li>• Aktiviere die 60%-Strategie für optimales Lernen</li>
              <li>• Versuche es in ein paar Tagen erneut</li>
            </ul>
          </div>
        )}

        {/* Aktionen */}
        <div className="flex flex-col sm:flex-row gap-4">
          <button
            onClick={() => navigate('/learning')}
            className="btn-secondary flex-1"
          >
            Zum Lernmodus
          </button>
          
          <button
            onClick={onRetry}
            className="btn-primary flex-1"
          >
            Prüfung wiederholen
          </button>
          
          <button
            onClick={onGoHome}
            className="btn-secondary flex-1"
          >
            Zur Übersicht
          </button>
        </div>
      </div>
    </div>
  );
};