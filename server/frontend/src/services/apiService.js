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
        const response = await fetch(`${API_BASE}/v1/agents`, {
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

    async updateAgent(agentId, data) {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/v1/agents/${agentId}`, {
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

    // Convenience methods using the consolidated updateAgent method
    async approveAgent(agentId) {
        return this.updateAgent(agentId, { status: 'ACTIVE' })
    }

    async declineAgent(agentId) {
        return this.updateAgent(agentId, { status: 'INACTIVE' })
    }

    async updateAgentStatus(agentId, status) {
        return this.updateAgent(agentId, { status })
    }

    async updateAgentName(agentId, name) {
        return this.updateAgent(agentId, { name })
    }
}

export const apiService = new ApiService()
