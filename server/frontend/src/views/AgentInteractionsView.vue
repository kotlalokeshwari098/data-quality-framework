<template>
  <div class="container-fluid py-3 py-md-4">
    <!-- Header with Back Button -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex align-items-center gap-3 mb-3">
          <button class="btn btn-outline-secondary btn-sm" @click="goBack">
            <i class="bi bi-arrow-left me-2"></i>Back to Reports
          </button>
        </div>
        <PageHeader
          :title="`Agent Interactions - ${agentName}`"
          :subtitle="`Agent ID: ${agentId}`"
          icon="bi bi-clock-history"
        />
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading interactions...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger" role="alert">
      <i class="bi bi-exclamation-triangle me-2"></i>
      {{ error }}
    </div>

    <!-- Interactions Content -->
    <div v-else>
      <!-- Stats Cards -->
      <div class="row mb-4">
        <div class="col-12 col-md-6 mb-3">
          <StatsCard
            label="Latest Ping"
            :value="latestPingTime || 'No pings recorded'"
            icon="bi bi-heart-pulse"
            :iconColor="latestPingColor"
            :iconBgColor="latestPingBgColor"
            trendText="Health check"
            trendType="neutral"
          />
        </div>
        <div class="col-12 col-md-6 mb-3">
          <StatsCard
            label="First Registration"
            :value="firstRegistrationTime || 'Not registered'"
            icon="bi bi-person-plus"
            iconColor="#0d6efd"
            iconBgColor="#cfe2ff"
            trendText="Agent joined"
            trendType="neutral"
          />
        </div>
      </div>

      <!-- Interactions Table -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-white border-bottom py-3">
              <div class="d-flex justify-content-between align-items-center">
                <h5 class="mb-0 fw-semibold">
                  <i class="bi bi-clock-history text-primary me-2"></i>Agent Activity Log
                </h5>
                <span class="badge bg-secondary">{{ totalInteractions }} interactions</span>
              </div>
            </div>
            <div class="card-body p-0">
              <div class="table-responsive">
                <table class="table table-hover mb-0 align-middle">
                  <thead class="table-light">
                    <tr>
                      <th class="ps-4">Timestamp</th>
                      <th>Type</th>
                      <th class="ps-4">Interaction ID</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="interaction in paginatedInteractions"
                      :key="interaction.id"
                    >
                      <td class="ps-4">
                        <div class="d-flex flex-column">
                          <span class="fw-medium">{{ formatDateShort(interaction.timestamp) }}</span>
                          <small class="text-muted">{{ formatTime(interaction.timestamp) }}</small>
                        </div>
                      </td>
                      <td>
                        <span class="badge rounded-pill" :class="getInteractionTypeBadgeClass(interaction.type)">
                          <i :class="getInteractionTypeIcon(interaction.type)" class="me-1"></i>
                          {{ interaction.type }}
                        </span>
                      </td>
                      <td class="ps-4">
                        <span class="font-monospace small text-truncate">{{ interaction.id }}</span>
                      </td>
                    </tr>
                    <tr v-if="paginatedInteractions.length === 0">
                      <td colspan="3" class="text-center text-muted py-5">
                        <i class="bi bi-inbox fs-1 d-block mb-2 opacity-50"></i>
                        <p class="mb-0">No interactions recorded for this agent</p>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Pagination -->
            <div v-if="totalPages > 1" class="card-footer bg-white border-top py-3">
              <div class="d-flex justify-content-between align-items-center">
                <div class="text-muted small">
                  Showing {{ startIndex + 1 }} to {{ Math.min(endIndex, totalInteractions) }} of {{ totalInteractions }} interactions
                </div>
                <nav>
                  <ul class="pagination pagination-sm mb-0">
                    <li class="page-item" :class="{ disabled: currentPage === 1 }">
                      <button class="page-link" @click="previousPage" :disabled="currentPage === 1">
                        <i class="bi bi-chevron-left"></i>
                      </button>
                    </li>
                    <li v-for="page in visiblePages" :key="page" class="page-item" :class="{ active: page === currentPage }">
                      <button class="page-link" @click="goToPage(page)">{{ page }}</button>
                    </li>
                    <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                      <button class="page-link" @click="nextPage" :disabled="currentPage === totalPages">
                        <i class="bi bi-chevron-right"></i>
                      </button>
                    </li>
                  </ul>
                </nav>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../components/PageHeader.vue'
import StatsCard from '../components/StatsCard.vue'
import { apiService } from '../services/apiService.js'

const route = useRoute()
const router = useRouter()

const agentId = ref(route.params.uuid)
const agentName = ref('Unknown Agent')
const loading = ref(true)
const error = ref(null)
const interactions = ref([])
const currentPage = ref(1)
const itemsPerPage = 10

const totalInteractions = computed(() => interactions.value.length)
const totalPages = computed(() => Math.ceil(totalInteractions.value / itemsPerPage))

const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage)
const endIndex = computed(() => startIndex.value + itemsPerPage)

const paginatedInteractions = computed(() => {
  return interactions.value.slice(startIndex.value, endIndex.value)
})

const visiblePages = computed(() => {
  const pages = []
  const maxPagesToShow = 5
  let startPage = Math.max(1, currentPage.value - Math.floor(maxPagesToShow / 2))
  let endPage = Math.min(totalPages.value, startPage + maxPagesToShow - 1)

  if (endPage - startPage + 1 < maxPagesToShow) {
    startPage = Math.max(1, endPage - maxPagesToShow + 1)
  }

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i)
  }

  return pages
})

const latestPingTime = computed(() => {
  const latestPing = interactions.value.find(i => i.type === 'PING')
  if (!latestPing) return null
  return formatTime(latestPing.timestamp)
})

const latestPingColor = computed(() => {
  const latestPing = interactions.value.find(i => i.type === 'PING')
  if (!latestPing) return '#198754' // default green

  const date = new Date(latestPing.timestamp)
  const now = new Date()
  const diffMs = now - date
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

  if (diffDays > 3) return '#dc3545' // red if more than 3 days old
  return '#198754' // green otherwise
})

const latestPingBgColor = computed(() => {
  const latestPing = interactions.value.find(i => i.type === 'PING')
  if (!latestPing) return '#d1e7dd' // default light green

  const date = new Date(latestPing.timestamp)
  const now = new Date()
  const diffMs = now - date
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

  if (diffDays > 3) return '#f8d7da' // light red if more than 3 days old
  return '#d1e7dd' // light green otherwise
})

const firstRegistrationTime = computed(() => {
  const registration = [...interactions.value].reverse().find(i => i.type === 'REGISTRATION')
  if (!registration) return null
  return formatTime(registration.timestamp)
})

const fetchAgentInteractions = async () => {
  try {
    loading.value = true
    error.value = null

    // Fetch agent details first to get the name
    const agentsResponse = await apiService.getAgents()
    const agents = agentsResponse._embedded?.agents || []
    const agent = agents.find(a => a.id === agentId.value)

    if (!agent) {
      error.value = 'Agent not found'
      return
    }

    agentName.value = agent.name || 'Unknown Agent'

    // Fetch agent with interactions expanded
    const agentResponse = await apiService.getAgent(agentId.value, true)
    interactions.value = agentResponse.interactions || []

    // Sort by timestamp (newest first)
    interactions.value.sort((a, b) =>
      new Date(b.timestamp) - new Date(a.timestamp)
    )
  } catch (err) {
    error.value = err.message || 'Failed to load agent interactions'
    console.error('Error fetching agent interactions:', err)
  } finally {
    loading.value = false
  }
}

const formatDateShort = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
}

const formatTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMins / 60)
  const diffDays = Math.floor(diffHours / 24)

  if (diffMins < 1) return 'Just now'
  if (diffMins < 60) return `${diffMins}m ago`
  if (diffHours < 24) return `${diffHours}h ago`
  if (diffDays < 7) return `${diffDays}d ago`

  return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
}

const getInteractionTypeBadgeClass = (type) => {
  switch (type) {
    case 'REPORT':
      return 'bg-info'
    case 'PING':
      return 'bg-success'
    case 'REGISTRATION':
      return 'bg-primary'
    default:
      return 'bg-secondary'
  }
}

const getInteractionTypeIcon = (type) => {
  switch (type) {
    case 'REPORT':
      return 'bi bi-file-earmark-text'
    case 'PING':
      return 'bi bi-heart-pulse'
    case 'REGISTRATION':
      return 'bi bi-person-plus'
    default:
      return 'bi bi-info-circle'
  }
}

const goToPage = (page) => {
  currentPage.value = page
}

const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchAgentInteractions()
})
</script>

<style scoped>
.table-row-hover:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}

.report-id {
  max-width: 200px;
}

.pagination-sm .page-link {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
</style>
