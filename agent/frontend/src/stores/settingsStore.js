import { ref } from 'vue'
import { api } from '../js/api'

const settings = ref(null)
const loading = ref(false)
const error = ref(null)

async function fetchSettings() {
    loading.value = true
    error.value = null
    try {
        const response = await api.get('/api/settings')
        settings.value = response.data
        return response.data
    } catch (err) {
        console.error('Failed to fetch settings', err)
        error.value = err.response?.data?.message || 'Failed to fetch settings'
        throw err
    } finally {
        loading.value = false
    }
}

async function updateSettings(settingsData) {
    loading.value = true
    error.value = null
    try {
        const response = await api.put('/api/settings', settingsData)
        settings.value = response.data
        return response.data
    } catch (err) {
        console.error('Failed to update settings', err)
        error.value = err.response?.data?.message || 'Failed to update settings'
        throw err
    } finally {
        loading.value = false
    }
}

export default {
    settings,
    loading,
    error,
    fetchSettings,
    updateSettings,
}
