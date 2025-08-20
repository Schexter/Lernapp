export interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  roles: string[];
  experiencePoints?: number;
  level?: number;
  totalQuestionsAnswered?: number;
  correctAnswers?: number;
  currentStreak?: number;
  bestStreak?: number;
  lastLogin?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

export interface AuthResponse {
  token: string;
  userId: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  roles: string[];
  experiencePoints?: number;
  level?: number;
  totalQuestionsAnswered?: number;
  correctAnswers?: number;
  currentStreak?: number;
  bestStreak?: number;
  lastLogin?: string;
  message?: string;
}

export interface JwtPayload {
  sub: string;
  exp: number;
  iat: number;
  roles: string[];
}