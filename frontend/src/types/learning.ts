export interface Question {
  id: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  category: string;
  difficulty: number;
  points: number;
}

export interface AnswerResponse {
  correct: boolean;
  correctAnswer: string;
  explanation: string;
  pointsEarned: number;
}

export interface LearningStats {
  totalQuestionsAnswered: number;
  correctAnswers: number;
  currentStreak: number;
  bestStreak: number;
  experiencePoints: number;
  level: number;
  accuracy: number;
  progressToNextLevel: number;
  xpNeededForNextLevel: number;
  categoryBreakdown: Record<string, number>;
}