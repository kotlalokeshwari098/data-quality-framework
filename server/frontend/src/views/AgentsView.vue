<template>
  <div class="container-fluid py-3 py-md-4">
    <div class="row">
      <div class="col-12">
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h2 class="h3 fw-bold text-dark mb-1">
              <i class="bi bi-database-fill-gear me-2 text-primary"></i>
              Agents Management
            </h2>
            <p class="text-muted mb-0">Manage and monitor all connected agents in the network</p>
          </div>
          <div class="d-flex gap-2">
            <button
              @click="refreshAgents"
              class="btn btn-outline-primary btn-sm"
              :disabled="loading"
            >
              <i class="bi bi-arrow-clockwise me-1"></i>
              Refresh
            </button>
          </div>
        </div>

        <!-- Stats Cards -->
        <div class="row mb-4">
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm">
              <div class="card-body text-center py-3">
                <div class="text-success fw-bold h4 mb-1">{{ agentStats.active }}</div>
                <div class="text-muted small">Active</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm">
              <div class="card-body text-center py-3">
                <div class="text-warning fw-bold h4 mb-1">{{ agentStats.pending }}</div>
                <div class="text-muted small">Pending</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm">
              <div class="card-body text-center py-3">
                <div class="text-secondary fw-bold h4 mb-1">{{ agentStats.inactive }}</div>
                <div class="text-muted small">Inactive</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm">
              <div class="card-body text-center py-3">
                <div class="text-dark fw-bold h4 mb-1">{{ agentStats.total }}</div>
                <div class="text-muted small">Total</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Filters -->
        <div class="card border-0 shadow-sm mb-4">
          <div class="card-body py-3">
            <div class="row align-items-center">
              <div class="col-md-4">
                <input
                  v-model="searchQuery"
                  type="text"
                  class="form-control"
                  placeholder="Search agents by name or ID..."
                >
              </div>
              <div class="col-md-3">
                <select v-model="statusFilter" class="form-select">
                  <option value="">All Statuses</option>
                  <option value="ACTIVE">Active</option>
                  <option value="PENDING">Pending</option>
                  <option value="INACTIVE">Inactive</option>
                  <option value="ERROR">Error</option>
                </select>
              </div>
              <div class="col-md-2">
                <select v-model="sortBy" class="form-select">
                  <option value="name">Sort by Name</option>
                  <option value="status">Sort by Status</option>
                  <option value="id">Sort by ID</option>
                </select>
              </div>
              <div class="col-md-3 text-end">
                <span class="text-muted small">{{ filteredAgents.length }} agents</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Loading state -->
        <div v-if="loading" class="d-flex justify-content-center align-items-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading agents...</span>
          </div>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="alert alert-danger" role="alert">
          <h6 class="alert-heading">Error Loading Agents</h6>
          <p class="mb-0">{{ error }}</p>
        </div>

        <!-- Agents List -->
        <div v-else-if="filteredAgents.length > 0" class="agents-grid">
          <div
            v-for="agent in filteredAgents"
            :key="agent.id"
            class="agent-card card border-0 shadow-sm mb-3"
            :class="{ 'border-warning': agent.status === 'PENDING' }"
          >
            <div class="card-body p-3">
              <div class="row align-items-center">
                <!-- Agent Info -->
                <div class="col-md-6">
                  <div class="d-flex align-items-center">
                    <div class="agent-avatar me-3">
                      <div class="avatar-circle" :class="getAvatarClass(agent.status)">
                        <i class="bi bi-database-fill-gear"></i>
                      </div>
                    </div>
                    <div class="agent-details">
                      <div class="agent-name fw-medium text-dark mb-1" v-if="editingAgent !== agent.id">
                        <span @click="startEditing(agent)" class="editable-name" :title="'Click to edit name'">
                          {{ agent.name || 'Unknown' }}
                        </span>
                      </div>
                      <div class="agent-name-edit mb-1" v-else>
                        <input
                          ref="nameInput"
                          v-model="editingName"
                          @blur="saveAgentName(agent)"
                          @keyup.enter="saveAgentName(agent)"
                          @keyup.escape="cancelEditing"
                          :placeholder="agent.name || 'Unknown'"
                          class="form-control form-control-sm"
                          :disabled="processingAgent === agent.id"
                          style="width: 200px;"
                        />
                      </div>
                      <div class="agent-id text-muted small">
                        <code>{{ agent.id }}</code>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Status and Actions -->
                <div class="col-md-6">
                  <div class="d-flex justify-content-between align-items-center">
                    <div class="agent-status">
                      <span :class="getStatusClass(agent.status)" class="badge">
                        {{ agent.status }}
                      </span>
                    </div>

                    <!-- Actions -->
                    <div class="agent-actions d-flex gap-2">
                      <!-- Processing indicator -->
                      <div v-if="processingAgent === agent.id" class="d-flex align-items-center">
                        <div class="spinner-border spinner-border-sm text-primary me-2" role="status">
                          <span class="visually-hidden">Processing...</span>
                        </div>
                        <small class="text-muted">Processing...</small>
                      </div>

                      <!-- Pending actions -->
                      <div v-else-if="agent.status === 'PENDING'" class="d-flex gap-2">
                        <button
                          class="btn btn-success btn-sm"
                          @click="approveAgent(agent)"
                          title="Approve Agent"
                        >
                          <i class="bi bi-check-lg me-1"></i>
                          Approve
                        </button>
                        <button
                          class="btn btn-outline-danger btn-sm"
                          @click="declineAgent(agent)"
                          title="Decline Agent"
                        >
                          <i class="bi bi-x-lg me-1"></i>
                          Decline
                        </button>
                      </div>

                      <!-- Active/Inactive toggle -->
                      <div v-else-if="agent.status === 'ACTIVE' || agent.status === 'INACTIVE'" class="d-flex align-items-center">
                        <div class="form-check form-switch">
                          <input
                            class="form-check-input"
                            type="checkbox"
                            :id="`toggle-${agent.id}`"
                            :checked="agent.status === 'ACTIVE'"
                            @change="toggleAgentStatus(agent)"
                            :disabled="processingAgent === agent.id"
                          >
                          <label class="form-check-label small text-muted ms-1" :for="`toggle-${agent.id}`">
                            {{ agent.status === 'ACTIVE' ? 'Active' : 'Inactive' }}
                          </label>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Pending message -->
              <div v-if="agent.status === 'PENDING'" class="row mt-3">
                <div class="col-12">
                  <div class="alert alert-warning py-2 mb-0">
                    <small class="d-flex align-items-center">
                      <i class="bi bi-exclamation-triangle me-2 text-warning"></i>
                      This agent is awaiting approval to join the network
                    </small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- No agents state -->
        <div v-else class="text-center py-5">
          <i class="bi bi-database-fill-gear mb-3 opacity-50" style="font-size: 4rem;"></i>
          <h4 class="text-muted">No agents found</h4>
          <p class="text-muted">{{ searchQuery || statusFilter ? 'Try adjusting your filters' : 'No agents are currently registered in the system' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { apiService } from '../services/apiService.js'

const loading = ref(true)
const error = ref(null)
const agents = ref([])
const processingAgent = ref(null)
const editingAgent = ref(null)
const editingName = ref('')

// Filters
const searchQuery = ref('')
const statusFilter = ref('')
const sortBy = ref('name')

const fetchAgents = async () => {
  try {
    loading.value = true
    error.value = null
    const response = await apiService.getAgents()
    agents.value = response._embedded?.agents || []
  } catch (err) {
    error.value = err.message || 'Failed to load agents'
    console.error('Error fetching agents:', err)
  } finally {
    loading.value = false
  }
}

const refreshAgents = () => {
  fetchAgents()
}

// Computed properties
const agentStats = computed(() => {
  const stats = {
    total: agents.value.length,
    active: 0,
    pending: 0,
    inactive: 0,
    error: 0
  }

  agents.value.forEach(agent => {
    switch (agent.status) {
      case 'ACTIVE':
        stats.active++
        break
      case 'PENDING':
        stats.pending++
        break
      case 'INACTIVE':
        stats.inactive++
        break
      case 'ERROR':
        stats.error++
        break
    }
  })

  return stats
})

const filteredAgents = computed(() => {
  let filtered = agents.value

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(agent =>
      (agent.name || 'unknown').toLowerCase().includes(query) ||
      agent.id.toLowerCase().includes(query)
    )
  }

  // Filter by status
  if (statusFilter.value) {
    filtered = filtered.filter(agent => agent.status === statusFilter.value)
  }

  // Sort
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return (a.name || 'Unknown').localeCompare(b.name || 'Unknown')
      case 'status':
        return a.status.localeCompare(b.status)
      case 'id':
        return a.id.localeCompare(b.id)
      default:
        return 0
    }
  })

  return filtered
})

// Methods
const getStatusClass = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-success'
    case 'PENDING':
      return 'bg-warning text-dark'
    case 'INACTIVE':
      return 'bg-secondary'
    case 'ERROR':
      return 'bg-danger'
    default:
      return 'bg-secondary'
  }
}

const getAvatarClass = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-success text-white'
    case 'PENDING':
      return 'bg-warning text-dark'
    case 'INACTIVE':
      return 'bg-secondary text-white'
    case 'ERROR':
      return 'bg-danger text-white'
    default:
      return 'bg-secondary text-white'
  }
}

const approveAgent = async (agent) => {
  try {
    processingAgent.value = agent.id
    await apiService.approveAgent(agent.id)
    const agentIndex = agents.value.findIndex(a => a.id === agent.id)
    if (agentIndex !== -1) {
      agents.value[agentIndex].status = 'ACTIVE'
    }
  } catch (err) {
    console.error('Error approving agent:', err)
    error.value = `Failed to approve agent: ${err.message}`
  } finally {
    processingAgent.value = null
  }
}

const declineAgent = async (agent) => {
  try {
    processingAgent.value = agent.id
    await apiService.declineAgent(agent.id)
    const agentIndex = agents.value.findIndex(a => a.id === agent.id)
    if (agentIndex !== -1) {
      agents.value[agentIndex].status = 'INACTIVE'
    }
  } catch (err) {
    console.error('Error declining agent:', err)
    error.value = `Failed to decline agent: ${err.message}`
  } finally {
    processingAgent.value = null
  }
}

const toggleAgentStatus = async (agent) => {
  const newStatus = agent.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  try {
    processingAgent.value = agent.id
    await apiService.updateAgentStatus(agent.id, newStatus)
    const agentIndex = agents.value.findIndex(a => a.id === agent.id)
    if (agentIndex !== -1) {
      agents.value[agentIndex].status = newStatus
    }
  } catch (err) {
    console.error('Error updating agent status:', err)
    error.value = `Failed to update agent status: ${err.message}`
  } finally {
    processingAgent.value = null
  }
}

const startEditing = (agent) => {
  editingAgent.value = agent.id
  editingName.value = agent.name || ''
  nextTick(() => {
    const input = document.querySelector('input[ref="nameInput"]')
    if (input) input.focus()
  })
}

const saveAgentName = async (agent) => {
  if (!editingName.value.trim()) {
    cancelEditing()
    return
  }

  try {
    processingAgent.value = agent.id
    await apiService.updateAgentName(agent.id, { name: editingName.value.trim() })
    const agentIndex = agents.value.findIndex(a => a.id === agent.id)
    if (agentIndex !== -1) {
      agents.value[agentIndex].name = editingName.value.trim()
    }
    editingAgent.value = null
  } catch (err) {
    console.error('Error updating agent name:', err)
    error.value = `Failed to update agent name: ${err.message}`
  } finally {
    processingAgent.value = null
  }
}

const cancelEditing = () => {
  editingAgent.value = null
  editingName.value = ''
}

const viewAgentDetails = (agent) => {
  // Placeholder for agent details modal or navigation
  console.log('View details for agent:', agent.id)
}

onMounted(() => {
  fetchAgents()
})
</script>

<style scoped>
.agent-card {
  transition: all 0.2s ease;
}

.agent-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.editable-name {
  cursor: pointer;
  text-decoration: underline;
}

.editable-name:hover {
  color: #0d6efd;
}

.agent-id code {
  font-size: 0.75rem;
  color: #6c757d;
  background: none;
  padding: 0;
}

@media (max-width: 768px) {
  .col-md-6 {
    margin-bottom: 1rem;
  }

  .d-flex.justify-content-between {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}
</style>
