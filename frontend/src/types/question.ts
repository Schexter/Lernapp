export interface Question {
  id: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  correctAnswer: 'A' | 'B' | 'C' | 'D';
  explanation?: string;
  category: string;
  subcategory?: string;
  difficulty?: 'EASY' | 'MEDIUM' | 'HARD';
  tags?: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface QuestionFilters {
  category?: string;
  subcategory?: string;
  difficulty?: string;
  tags?: string[];
  search?: string;
}

export interface QuestionStats {
  totalQuestions: number;
  answeredQuestions: number;
  correctAnswers: number;
  incorrectAnswers: number;
  averageScore: number;
}