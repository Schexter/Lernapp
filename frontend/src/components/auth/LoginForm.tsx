import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import { useAuthStore } from '../../stores/authStore';
import { useState } from 'react';
import { AlertCircle, CheckCircle } from 'lucide-react';

const loginSchema = z.object({
  username: z.string().min(1, 'Benutzername ist erforderlich'),
  password: z.string().min(1, 'Passwort ist erforderlich'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export const LoginForm: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema)
  });
  
  const onSubmit = async (data: LoginFormData) => {
    try {
      setError(null);
      setSuccess(null);
      const response = await authService.login(data);
      // Create user object from response
      const user = {
        id: response.userId,
        username: response.username,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        roles: response.roles,
        experiencePoints: response.experiencePoints || 0,
        level: response.level || 1,
        totalQuestionsAnswered: response.totalQuestionsAnswered || 0,
        correctAnswers: response.correctAnswers || 0,
        currentStreak: response.currentStreak || 0,
        bestStreak: response.bestStreak || 0,
        lastLogin: response.lastLogin
      };
      login(response.token, user);
      setSuccess('Login erfolgreich! Sie werden weitergeleitet...');
      setTimeout(() => {
        navigate('/dashboard');
      }, 1500);
    } catch (error: any) {
      const status = error.response?.status;
      if (status === 401) {
        setError('Ungültiger Benutzername oder Passwort. Bitte überprüfen Sie Ihre Eingaben.');
      } else if (status === 404) {
        setError('Benutzername nicht gefunden. Haben Sie sich bereits registriert?');
      } else if (!error.response) {
        setError('Keine Verbindung zum Server. Bitte prüfen Sie Ihre Internetverbindung.');
      } else {
        setError(error.response?.data || 'Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.');
      }
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-start space-x-2">
          <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
          <p className="text-red-800 text-sm">{error}</p>
        </div>
      )}
      
      {success && (
        <div className="bg-green-50 border border-green-200 rounded-lg p-4 flex items-start space-x-2">
          <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" />
          <p className="text-green-800 text-sm">{success}</p>
        </div>
      )}
      
      <div>
        <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-1">
          Benutzername
        </label>
        <input
          {...register('username')}
          type="text"
          id="username"
          placeholder="Ihr Benutzername"
          className="input"
          autoComplete="username"
        />
        {errors.username && (
          <p className="text-red-600 text-sm mt-1">{errors.username.message}</p>
        )}
      </div>
      
      <div>
        <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
          Passwort
        </label>
        <input
          {...register('password')}
          type="password"
          id="password"
          placeholder="Ihr Passwort"
          className="input"
          autoComplete="current-password"
        />
        {errors.password && (
          <p className="text-red-600 text-sm mt-1">{errors.password.message}</p>
        )}
      </div>
      
      <button
        type="submit"
        disabled={isSubmitting}
        className="btn-primary w-full"
      >
        {isSubmitting ? 'Anmelden...' : 'Anmelden'}
      </button>
    </form>
  );
};