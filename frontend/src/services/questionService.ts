import api from './api';
import type { Question, QuestionFilters } from '../types/question';

export const questionService = {
  async getQuestions(filters?: QuestionFilters): Promise<Question[]> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value) {
          if (Array.isArray(value)) {
            value.forEach(v => params.append(key, v));
          } else {
            params.append(key, value);
          }
        }
      });
    }
    const response = await api.get<Question[]>(`/questions?${params.toString()}`);
    return response.data;
  },

  async getQuestion(id: number): Promise<Question> {
    const response = await api.get<Question>(`/questions/${id}`);
    return response.data;
  },

  async createQuestion(question: Partial<Question>): Promise<Question> {
    const response = await api.post<Question>('/questions', question);
    return response.data;
  },

  async updateQuestion(id: number, question: Partial<Question>): Promise<Question> {
    const response = await api.put<Question>(`/questions/${id}`, question);
    return response.data;
  },

  async deleteQuestion(id: number): Promise<void> {
    await api.delete(`/questions/${id}`);
  }
};