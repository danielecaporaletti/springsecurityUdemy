import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

// Interceptor to include Authorization header in each request if available
api.interceptors.request.use((config) => {
    const auth = sessionStorage.getItem('Authorization');
    if (auth) {
        config.headers.Authorization = auth;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;
