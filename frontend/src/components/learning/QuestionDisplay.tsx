import { CheckCircle, XCircle } from 'lucide-react';
import type { Question } from '../../types/question';

interface QuestionDisplayProps {
  question: Question;
  userAnswer?: string;
  onAnswerSelect: (answer: string) => void;
  showFeedback: boolean;
  showExplanation: boolean;
}

export const QuestionDisplay: React.FC<QuestionDisplayProps> = ({
  question,
  userAnswer,
  onAnswerSelect,
  showFeedback,
  showExplanation
}) => {
  const getOptionClass = (option: string) => {
    let baseClass = "question-option";
    
    if (userAnswer === option) {
      baseClass += " selected";
    }
    
    if (showFeedback) {
      if (option === question.correctAnswer) {
        baseClass += " correct";
      } else if (userAnswer === option && option !== question.correctAnswer) {
        baseClass += " incorrect";
      }
    }
    
    return baseClass;
  };

  return (
    <div className="space-y-6">
      <div className="question-header">
        <h2 className="text-xl font-semibold text-gray-900">
          {question.questionText}
        </h2>
      </div>
      
      <div className="space-y-3">
        {['A', 'B', 'C', 'D'].map((option) => (
          <div
            key={option}
            className={getOptionClass(option)}
            onClick={() => !showFeedback && onAnswerSelect(option)}
          >
            <div className="flex items-center space-x-3">
              <span className="font-medium text-gray-600">{option})</span>
              <span>{question[`option${option}` as keyof Question]}</span>
            </div>
          </div>
        ))}
      </div>
      
      {showFeedback && (
        <div className="feedback-section">
          {userAnswer === question.correctAnswer ? (
            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <div className="flex items-center space-x-2">
                <CheckCircle className="w-5 h-5 text-green-600" />
                <span className="font-medium text-green-800">
                  Sehr gut! Das war richtig.
                </span>
              </div>
            </div>
          ) : (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4">
              <div className="flex items-center space-x-2">
                <XCircle className="w-5 h-5 text-red-600" />
                <span className="font-medium text-red-800">
                  Fast! Schau dir die Erklärung an.
                </span>
              </div>
            </div>
          )}
        </div>
      )}
      
      {showExplanation && question.explanation && (
        <div className="explanation bg-blue-50 border border-blue-200 rounded-lg p-4">
          <h4 className="font-medium text-blue-900 mb-2">Erklärung:</h4>
          <p className="text-blue-800">{question.explanation}</p>
        </div>
      )}
    </div>
  );
};