import axios from 'axios';

// Dynamische Backend-URL basierend auf der aktuellen Host-Adresse
const getBackendUrl = () => {
  const hostname = window.location.hostname;
  // Wenn localhost, nutze localhost, sonst nutze die IP-Adresse
  return `http://${hostname}:8080/api`;
};

const api = axios.create({
  baseURL: getBackendUrl(),
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor für JWT Token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor für Error Handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired - redirect to login
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;