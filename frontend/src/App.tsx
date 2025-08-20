import { RouterProvider } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { router } from './router';
import { useAuthStore } from './stores/authStore';
import { DebugAuth } from './components/DebugAuth';
import './styles/globals.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  const initializeAuth = useAuthStore((state) => state.initializeAuth);
  const [isReady, setIsReady] = useState(false);
  
  useEffect(() => {
    initializeAuth();
    setIsReady(true);
  }, [initializeAuth]);
  
  if (!isReady) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }
  
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
      <DebugAuth />
    </QueryClientProvider>
  );
}

export default App;
