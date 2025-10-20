import axios from 'axios';

const state = {
    authToken: null,
};

export function clearAuth() {
    state.authToken = null;
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('defaultPasswordFlag');
    sessionStorage.removeItem('userId');
}

export function isAuthenticated() {
    return !!state.authToken;
}

export function getUsername() {
    return sessionStorage.getItem('username');
}

export function getUserId() {
    return sessionStorage.getItem('userId');
}

export function getDefaultPasswordFlag() {
    const stored = sessionStorage.getItem('defaultPasswordFlag');
    return stored === 'true';
}

export function getAuthToken() {
    return state.authToken || sessionStorage.getItem('authToken');
}

// Initialize token from sessionStorage on page load
function initializeAuth() {
    const storedToken = sessionStorage.getItem('authToken');
    if (storedToken) {
        state.authToken = storedToken;
    }
}

initializeAuth();

export const api = axios.create({
});

api.interceptors.request.use((config) => {
    config.headers = config.headers || {};
    const token = getAuthToken();
    if (token && !config.__skipAuth) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    (r) => r,
    (err) => {
        if (err.response?.status === 401) {
            clearAuth();
            const redirect = encodeURIComponent(window.location.pathname + window.location.search);
            if (window.location.pathname !== '/login') {
                window.location.assign(`/login?redirect=${redirect}`);
            }
        }
        return Promise.reject(err);
    }
);

export async function authenticate(username, password) {
    try {
        const res = await api.post('/api/auth/login', {
            username,
            password
        }, {
            headers: {
                'Content-Type': 'application/json',
            },
            validateStatus: () => true,
            __skipAuth: true,
        });

        if (res.status !== 200) {
            throw new Error('Invalid username or password');
        }

        const { token, user } = res.data;

        if (!token) {
            throw new Error('No token received from server');
        }

        state.authToken = token;
        sessionStorage.setItem('authToken', token);

        const serverUsername = user?.username && String(user.username).trim()
            ? user.username
            : username;

        sessionStorage.setItem('username', serverUsername);

        if (user && typeof user.defaultPassword === 'boolean') {
            sessionStorage.setItem('defaultPasswordFlag', user.defaultPassword.toString());
        }

        if (user && user.id) {
            sessionStorage.setItem('userId', user.id.toString());
        }

        return {
            username: serverUsername,
            defaultPassword: user?.defaultPassword || false,
            userId: user?.id || null
        };
    } catch (error) {
        if (error.response?.status === 401) {
            throw new Error('Invalid username or password');
        }
        throw error;
    }
}
