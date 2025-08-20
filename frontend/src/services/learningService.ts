import api from './api';
import type { Question, AnswerResponse, LearningStats } from '../types/learning';

export const learningService = {
  async getNextQuestion(category?: string, difficulty?: number): Promise<Question> {
    const params = new URLSearchParams();
    if (category) params.append('category', category);
    if (difficulty) params.append('difficulty', difficulty.toString());
    
    const response = await api.get<Question>(`/learning/next-question?${params}`);
    return response.data;
  },

  async getQuestions(category?: string, difficulty?: number, limit: number = 10): Promise<Question[]> {
    const params = new URLSearchParams();
    if (category) params.append('category', category);
    if (difficulty) params.append('difficulty', difficulty.toString());
    params.append('limit', limit.toString());
    
    const response = await api.get<Question[]>(`/learning/questions?${params}`);
    return response.data;
  },

  async submitAnswer(questionId: number, answer: string): Promise<AnswerResponse> {
    const response = await api.post<AnswerResponse>('/learning/answer', {
      questionId,
      answer
    });
    return response.data;
  },

  async getCategories(): Promise<string[]> {
    const response = await api.get<string[]>('/learning/categories');
    return response.data;
  },

  async getStats(): Promise<LearningStats> {
    const response = await api.get<LearningStats>('/learning/stats');
    return response.data;
  }
};