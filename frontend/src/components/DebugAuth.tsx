import { useAuthStore } from '../stores/authStore';

export const DebugAuth = () => {
  const { isAuthenticated, user, token } = useAuthStore();
  
  return (
    <div className="fixed bottom-4 right-4 bg-black text-white p-2 rounded text-xs z-50">
      <div>Auth: {isAuthenticated ? 'YES' : 'NO'}</div>
      <div>User: {user?.username || 'none'}</div>
      <div>Token: {token ? 'present' : 'missing'}</div>
    </div>
  );
};