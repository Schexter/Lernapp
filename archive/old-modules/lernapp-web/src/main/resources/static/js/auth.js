// SIMPLIFIED Auth Manager - Development Version
const AuthManager = {
    getToken: function() {
        return localStorage.getItem('jwt_token');
    },
    
    setTokens: function(token, refreshToken) {
        localStorage.setItem('jwt_token', token);
        localStorage.setItem('refresh_token', refreshToken);
        localStorage.setItem('is_authenticated', 'true');
    },
    
    clearTokens: function() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('user');
        localStorage.removeItem('is_authenticated');
    },
    
    getUser: function() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },
    
    setUser: function(user) {
        localStorage.setItem('user', JSON.stringify(user));
    },
    
    isAuthenticated: function() {
        // SIMPLIFIED - just check if auth flag is set
        return localStorage.getItem('is_authenticated') === 'true';
    },
    
    logout: function() {
        this.clearTokens();
        window.location.href = '/login';
    }
};

// DISABLE automatic auth checks - let each page handle it
console.log('Auth.js loaded - Simplified version');

// Only update UI, no redirects
function updateAuthUI() {
    const user = AuthManager.getUser();
    const isAuthenticated = AuthManager.isAuthenticated();
    
    console.log('Updating UI - Authenticated:', isAuthenticated);
    
    const usernameElements = document.querySelectorAll('[data-username]');
    usernameElements.forEach(el => {
        el.textContent = user ? user.username : 'Gast';
    });
    
    const authElements = document.querySelectorAll('[data-auth-required]');
    authElements.forEach(el => {
        el.style.display = isAuthenticated ? '' : 'none';
    });
    
    const guestElements = document.querySelectorAll('[data-guest-only]');
    guestElements.forEach(el => {
        el.style.display = isAuthenticated ? 'none' : '';
    });
}

function handleLogout() {
    AuthManager.logout();
}

// Make available globally
window.AuthManager = AuthManager;
window.apiClient = {
    get: (url) => fetch(url),
    post: (url, data) => fetch(url, {method: 'POST', body: JSON.stringify(data), headers: {'Content-Type': 'application/json'}})
};

// Update UI on load without redirects
document.addEventListener('DOMContentLoaded', function() {
    updateAuthUI();
});
