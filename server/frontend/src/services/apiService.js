const API_BASE = '/api'

class ApiService {
    async login(username, password) {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username,
                password
            })
        })
        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Invalid credentials')
            }
            throw new Error(`Login failed: ${response.status}`)
        }
        const data = await response.json()
        if (data.token) {
            localStorage.setItem('authToken', data.token)
        }
        return data
    }

    async getAgents() {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/agents`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        })
        if (!response.ok) {
            throw new Error(`Failed to fetch agents: ${response.status}`)
        }
        return await response.json()
    }

    async approveAgent(agentId) {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/agents/${agentId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            body: JSON.stringify({
                status: 'ACTIVE'
            })
        })
        if (!response.ok) {
            throw new Error(`Failed to approve agent: ${response.status}`)
        }
        return await response.json()
    }

    async declineAgent(agentId) {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/agents/${agentId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            body: JSON.stringify({
                status: 'INACTIVE'
            })
        })
        if (!response.ok) {
            throw new Error(`Failed to decline agent: ${response.status}`)
        }
        return await response.json()
    }

    async updateAgentName(agentId, data) {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/agents/${agentId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            body: JSON.stringify(data)
        })
        if (!response.ok) {
            throw new Error(`Failed to update agent: ${response.status}`)
        }
        return await response.json()
    }
}

export const apiService = new ApiService()
