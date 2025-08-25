import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User } from '../types/auth';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (token: string, user: User) => void;
  logout: () => void;
  updateUser: (user: User) => void;
  initializeAuth: () => void;
}

export const useAuthStore = create<AuthState>()((
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      
      login: (token: string, user: User) => {
        localStorage.setItem('authToken', token);
        set({ token, user, isAuthenticated: true });
      },
      
      logout: () => {
        localStorage.removeItem('authToken');
        set({ token: null, user: null, isAuthenticated: false });
      },
      
      updateUser: (user: User) => set({ user }),
      
      initializeAuth: () => {
        const token = localStorage.getItem('authToken');
        const storedState = localStorage.getItem('auth-storage');
        if (storedState) {
          try {
            const parsed = JSON.parse(storedState);
            if (parsed.state && parsed.state.token && parsed.state.user) {
              set({ 
                token: parsed.state.token, 
                user: parsed.state.user, 
                isAuthenticated: true 
              });
            }
          } catch (e) {
            console.error('Failed to parse stored auth state:', e);
          }
        } else if (token) {
          set({ token, isAuthenticated: true });
        }
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ 
        token: state.token,
        user: state.user,
        isAuthenticated: state.isAuthenticated 
      }),
    }
  )
));