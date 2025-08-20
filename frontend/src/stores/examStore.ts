import { create } from 'zustand';
import type { Question } from '../types/question';

interface ExamState {
  examQuestions: Question[];
  currentQuestionIndex: number;
  userAnswers: Record<number, string>;
  timeRemaining: number;
  examStartTime: Date | null;
  examEndTime: Date | null;
  isExamActive: boolean;
  startExam: (questions: Question[]) => void;
  submitAnswer: (questionId: number, answer: string) => void;
  nextQuestion: () => void;
  previousQuestion: () => void;
  setTimeRemaining: (time: number) => void;
  endExam: () => void;
  reset: () => void;
}

export const useExamStore = create<ExamState>((set) => ({
  examQuestions: [],
  currentQuestionIndex: 0,
  userAnswers: {},
  timeRemaining: 0,
  examStartTime: null,
  examEndTime: null,
  isExamActive: false,

  startExam: (questions) => 
    set({ 
      examQuestions: questions, 
      isExamActive: true, 
      examStartTime: new Date(),
      currentQuestionIndex: 0,
      userAnswers: {},
      timeRemaining: questions.length * 90 // 90 seconds per question
    }),

  submitAnswer: (questionId, answer) => 
    set((state) => ({ 
      userAnswers: { ...state.userAnswers, [questionId]: answer } 
    })),

  nextQuestion: () => 
    set((state) => ({ 
      currentQuestionIndex: Math.min(state.currentQuestionIndex + 1, state.examQuestions.length - 1) 
    })),

  previousQuestion: () => 
    set((state) => ({ 
      currentQuestionIndex: Math.max(state.currentQuestionIndex - 1, 0) 
    })),

  setTimeRemaining: (time) => 
    set({ timeRemaining: time }),

  endExam: () => 
    set({ 
      isExamActive: false, 
      examEndTime: new Date() 
    }),

  reset: () => 
    set({ 
      examQuestions: [],
      currentQuestionIndex: 0,
      userAnswers: {},
      timeRemaining: 0,
      examStartTime: null,
      examEndTime: null,
      isExamActive: false
    })
}));