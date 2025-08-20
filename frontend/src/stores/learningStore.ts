import { create } from 'zustand';
import type { Question } from '../types/question';

interface LearningState {
  currentQuestion: Question | null;
  questionIndex: number;
  totalQuestions: number;
  userAnswers: Record<number, string>;
  showExplanation: boolean;
  setCurrentQuestion: (question: Question, index: number, total: number) => void;
  setUserAnswer: (questionId: number, answer: string) => void;
  toggleExplanation: () => void;
  reset: () => void;
}

export const useLearningStore = create<LearningState>((set) => ({
  currentQuestion: null,
  questionIndex: 0,
  totalQuestions: 0,
  userAnswers: {},
  showExplanation: false,
  
  setCurrentQuestion: (question, index, total) => 
    set({ currentQuestion: question, questionIndex: index, totalQuestions: total }),
  
  setUserAnswer: (questionId, answer) => 
    set((state) => ({ 
      userAnswers: { ...state.userAnswers, [questionId]: answer } 
    })),
  
  toggleExplanation: () => 
    set((state) => ({ showExplanation: !state.showExplanation })),
  
  reset: () => 
    set({ userAnswers: {}, questionIndex: 0, currentQuestion: null, showExplanation: false }),
}));