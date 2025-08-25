"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.useAuthStore = void 0;
var zustand_1 = require("zustand");
var middleware_1 = require("zustand/middleware");
exports.useAuthStore = (0, zustand_1.create)()(((0, middleware_1.persist)(function (set) { return ({
    user: null,
    token: null,
    isAuthenticated: false,
    login: function (token, user) {
        localStorage.setItem('authToken', token);
        set({ token: token, user: user, isAuthenticated: true });
    },
    logout: function () {
        localStorage.removeItem('authToken');
        set({ token: null, user: null, isAuthenticated: false });
    },
    updateUser: function (user) { return set({ user: user }); },
    initializeAuth: function () {
        var token = localStorage.getItem('authToken');
        var storedState = localStorage.getItem('auth-storage');
        if (storedState) {
            try {
                var parsed = JSON.parse(storedState);
                if (parsed.state && parsed.state.token && parsed.state.user) {
                    set({
                        token: parsed.state.token,
                        user: parsed.state.user,
                        isAuthenticated: true
                    });
                }
            }
            catch (e) {
                console.error('Failed to parse stored auth state:', e);
            }
        }
        else if (token) {
            set({ token: token, isAuthenticated: true });
        }
    },
}); }, {
    name: 'auth-storage',
    partialize: function (state) { return ({
        token: state.token,
        user: state.user,
        isAuthenticated: state.isAuthenticated
    }); },
})));
