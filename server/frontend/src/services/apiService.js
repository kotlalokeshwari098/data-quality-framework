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

    async getAgent(agentId, expandInteractions = false) {
        const token = localStorage.getItem('authToken')
        const url = expandInteractions
            ? `${API_BASE}/v1/agents/${agentId}?expand=interactions`
            : `${API_BASE}/v1/agents/${agentId}`
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        })
        if (!response.ok) {
            throw new Error(`Failed to fetch agent: ${response.status}`)
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

    async getAgentReports(agentId) {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/v1/agents/${agentId}/reports`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        })
        if (!response.ok) {
            throw new Error(`Failed to fetch agent reports: ${response.status}`)
        }
        return await response.json()
    }

    async getQualityChecks() {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/v1/quality-checks`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        })
        if (!response.ok) {
            throw new Error(`Failed to fetch quality checks: ${response.status}`)
        }
        return await response.json()
    }

    async getReports() {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/v1/reports`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        })
        if (!response.ok) {
            throw new Error(`Failed to fetch reports: ${response.status}`)
        }
        return await response.json()
    }

    async updateQualityCheck(hash, data) {
        const token = localStorage.getItem('authToken')
        const response = await fetch(`${API_BASE}/v1/quality-checks/${hash}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            body: JSON.stringify(data)
        })
        if (!response.ok) {
            throw new Error(`Failed to update quality check: ${response.status}`)
        }
        return await response.json()
    }
}

export const apiService = new ApiService()
