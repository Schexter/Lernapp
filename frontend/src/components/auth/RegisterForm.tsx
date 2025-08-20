import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import { useAuthStore } from '../../stores/authStore';
import { useState } from 'react';
import { AlertCircle } from 'lucide-react';

const registerSchema = z.object({
  username: z.string()
    .min(3, 'Benutzername muss mindestens 3 Zeichen lang sein')
    .max(20, 'Benutzername darf maximal 20 Zeichen lang sein'),
  email: z.string().email('Ungültige E-Mail-Adresse'),
  password: z.string()
    .min(6, 'Passwort muss mindestens 6 Zeichen lang sein')
    .regex(/[A-Z]/, 'Passwort muss mindestens einen Großbuchstaben enthalten')
    .regex(/[0-9]/, 'Passwort muss mindestens eine Zahl enthalten'),
  confirmPassword: z.string(),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  agbAccepted: z.boolean().refine(val => val === true, {
    message: 'Sie müssen den AGBs zustimmen'
  })
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwörter stimmen nicht überein",
  path: ["confirmPassword"],
});

type RegisterFormData = z.infer<typeof registerSchema>;

export const RegisterForm: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const [error, setError] = useState<string | null>(null);
  
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema)
  });
  
  const onSubmit = async (data: RegisterFormData) => {
    try {
      setError(null);
      // Send all data including confirmPassword and agbAccepted to backend
      const response = await authService.register(data);
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
        totalQuestionsAnswered: 0,
        correctAnswers: 0,
        currentStreak: 0,
        bestStreak: 0
      };
      login(response.token, user);
      navigate('/dashboard');
    } catch (error: any) {
      setError(error.response?.data?.message || 'Registrierung fehlgeschlagen. Bitte versuchen Sie es erneut.');
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
      
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-1">
            Vorname (optional)
          </label>
          <input
            {...register('firstName')}
            type="text"
            id="firstName"
            placeholder="Max"
            className="input"
          />
        </div>
        
        <div>
          <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-1">
            Nachname (optional)
          </label>
          <input
            {...register('lastName')}
            type="text"
            id="lastName"
            placeholder="Mustermann"
            className="input"
          />
        </div>
      </div>
      
      <div>
        <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-1">
          Benutzername
        </label>
        <input
          {...register('username')}
          type="text"
          id="username"
          placeholder="maxmuster"
          className="input"
          autoComplete="username"
        />
        {errors.username && (
          <p className="text-red-600 text-sm mt-1">{errors.username.message}</p>
        )}
      </div>
      
      <div>
        <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
          E-Mail-Adresse
        </label>
        <input
          {...register('email')}
          type="email"
          id="email"
          placeholder="max@example.com"
          className="input"
          autoComplete="email"
        />
        {errors.email && (
          <p className="text-red-600 text-sm mt-1">{errors.email.message}</p>
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
          placeholder="Mindestens 6 Zeichen"
          className="input"
          autoComplete="new-password"
        />
        {errors.password && (
          <p className="text-red-600 text-sm mt-1">{errors.password.message}</p>
        )}
      </div>
      
      <div>
        <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">
          Passwort bestätigen
        </label>
        <input
          {...register('confirmPassword')}
          type="password"
          id="confirmPassword"
          placeholder="Passwort wiederholen"
          className="input"
          autoComplete="new-password"
        />
        {errors.confirmPassword && (
          <p className="text-red-600 text-sm mt-1">{errors.confirmPassword.message}</p>
        )}
      </div>
      
      <div className="flex items-start">
        <input
          {...register('agbAccepted')}
          type="checkbox"
          id="agbAccepted"
          className="mt-1 h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <label htmlFor="agbAccepted" className="ml-2 block text-sm text-gray-700">
          Ich akzeptiere die{' '}
          <a href="#" className="text-primary-600 hover:text-primary-700 underline">
            Allgemeinen Geschäftsbedingungen
          </a>{' '}
          und die{' '}
          <a href="#" className="text-primary-600 hover:text-primary-700 underline">
            Datenschutzerklärung
          </a>
        </label>
      </div>
      {errors.agbAccepted && (
        <p className="text-red-600 text-sm mt-1">{errors.agbAccepted.message}</p>
      )}
      
      <button
        type="submit"
        disabled={isSubmitting}
        className="btn-primary w-full"
      >
        {isSubmitting ? 'Registrieren...' : 'Registrieren'}
      </button>
    </form>
  );
};