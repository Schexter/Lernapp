import { useState } from 'react';
import { CheckCircle, XCircle, BookOpen } from 'lucide-react';
import type { Question, AnswerResponse } from '../../types/learning';

interface QuestionDisplayProps {
  question: Question;
  onAnswer: (answer: string) => void;
  feedback: AnswerResponse | null;
  onNext: () => void;
}

export const QuestionDisplay: React.FC<QuestionDisplayProps> = ({
  question,
  onAnswer,
  feedback,
  onNext,
}) => {
  const [selectedAnswer, setSelectedAnswer] = useState<string | null>(null);

  const handleSubmit = () => {
    if (selectedAnswer) {
      onAnswer(selectedAnswer);
    }
  };

  const handleNext = () => {
    setSelectedAnswer(null);
    onNext();
  };

  const options = [
    { key: 'A', text: question.optionA },
    { key: 'B', text: question.optionB },
    { key: 'C', text: question.optionC },
    { key: 'D', text: question.optionD },
  ];

  return (
    <div className="card">
      {/* Question Header */}
      <div className="mb-6">
        <div className="flex items-center justify-between mb-3">
          <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-primary/10 text-primary">
            {question.category}
          </span>
          <div className="flex items-center space-x-2">
            <span className="text-sm text-gray-500">Schwierigkeit:</span>
            <div className="flex space-x-1">
              {[1, 2, 3].map((level) => (
                <div
                  key={level}
                  className={`w-2 h-2 rounded-full ${
                    level <= question.difficulty
                      ? question.difficulty === 1
                        ? 'bg-green-500'
                        : question.difficulty === 2
                        ? 'bg-yellow-500'
                        : 'bg-red-500'
                      : 'bg-gray-300'
                  }`}
                />
              ))}
            </div>
            <span className="text-sm text-gray-500 ml-2">{question.points} Punkte</span>
          </div>
        </div>
        <p className="text-lg text-gray-800 font-medium">{question.questionText}</p>
      </div>

      {/* Answer Options */}
      <div className="space-y-3 mb-6">
        {options.map((option) => (
          <button
            key={option.key}
            onClick={() => !feedback && setSelectedAnswer(option.key)}
            disabled={!!feedback}
            className={`w-full text-left p-4 rounded-lg border-2 transition-all ${
              selectedAnswer === option.key
                ? feedback
                  ? feedback.correct && feedback.correctAnswer === option.key
                    ? 'border-green-500 bg-green-50'
                    : feedback.correctAnswer === option.key
                    ? 'border-green-500 bg-green-50'
                    : 'border-red-500 bg-red-50'
                  : 'border-primary bg-primary/5'
                : feedback && feedback.correctAnswer === option.key
                ? 'border-green-500 bg-green-50'
                : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50'
            } ${feedback ? 'cursor-not-allowed' : 'cursor-pointer'}`}
          >
            <div className="flex items-start">
              <span className="font-semibold mr-3 text-gray-700">{option.key}.</span>
              <span className="flex-1 text-gray-800">{option.text}</span>
              {feedback && (
                <span className="ml-2">
                  {feedback.correctAnswer === option.key ? (
                    <CheckCircle className="w-5 h-5 text-green-600" />
                  ) : selectedAnswer === option.key && !feedback.correct ? (
                    <XCircle className="w-5 h-5 text-red-600" />
                  ) : null}
                </span>
              )}
            </div>
          </button>
        ))}
      </div>

      {/* Feedback */}
      {feedback && (
        <div className={`p-4 rounded-lg mb-6 ${
          feedback.correct 
            ? 'bg-green-50 border border-green-200' 
            : 'bg-red-50 border border-red-200'
        }`}>
          <div className="flex items-start">
            {feedback.correct ? (
              <CheckCircle className="w-5 h-5 text-green-600 mr-2 flex-shrink-0 mt-0.5" />
            ) : (
              <XCircle className="w-5 h-5 text-red-600 mr-2 flex-shrink-0 mt-0.5" />
            )}
            <div className="flex-1">
              <p className={`font-semibold ${
                feedback.correct ? 'text-green-900' : 'text-red-900'
              }`}>
                {feedback.correct ? 'Richtig!' : 'Leider falsch!'}
              </p>
              {feedback.correct && (
                <p className="text-sm text-green-700 mt-1">
                  Du hast {feedback.pointsEarned} Punkte erhalten!
                </p>
              )}
              {!feedback.correct && (
                <p className="text-sm text-red-700 mt-1">
                  Die richtige Antwort ist: <strong>{feedback.correctAnswer}</strong>
                </p>
              )}
              {feedback.explanation && (
                <div className="mt-3 p-3 bg-white rounded-lg">
                  <div className="flex items-center mb-2">
                    <BookOpen className="w-4 h-4 text-gray-600 mr-2" />
                    <p className="text-sm font-semibold text-gray-700">Erklärung:</p>
                  </div>
                  <p className="text-sm text-gray-600">{feedback.explanation}</p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Action Buttons */}
      <div className="flex justify-end space-x-3">
        {!feedback ? (
          <button
            onClick={handleSubmit}
            disabled={!selectedAnswer}
            className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Antwort prüfen
          </button>
        ) : (
          <button onClick={handleNext} className="btn-primary">
            Nächste Frage
          </button>
        )}
      </div>
    </div>
  );
};