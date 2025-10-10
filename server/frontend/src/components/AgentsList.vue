<template>
  <div class="agents-container">
    <div class="card border-0 shadow-sm h-100">
      <div class="card-header bg-white border-bottom-0 py-2">
        <h6 class="mb-0 fw-bold text-dark">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" class="me-2 text-primary">
            <path d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 4L13.5 7H7V9H21ZM21 11H7V13H21V11ZM21 15H7V17H21V15ZM21 19H7V21H21V19Z" fill="currentColor"/>
          </svg>
          Agents
        </h6>
      </div>
      <div class="card-body p-2">
        <!-- Loading state -->
        <div v-if="loading" class="d-flex justify-content-center align-items-center" style="min-height: 80px;">
          <div class="spinner-border spinner-border-sm text-primary" role="status">
            <span class="visually-hidden">Loading agents...</span>
          </div>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="alert alert-danger mb-0 py-2" role="alert">
          <small>{{ error }}</small>
        </div>

        <!-- Agents list -->
        <div v-else-if="agents.length > 0" class="agents-list">
          <div
            v-for="agent in agents"
            :key="agent.id"
            class="agent-item py-1 px-2 mb-1 bg-light rounded"
            :class="{ 'pending-agent': agent.status === 'PENDING' }"
          >
            <div class="d-flex justify-content-between align-items-center">
              <div class="agent-info">
                <div class="agent-name fw-medium text-dark" v-if="editingAgent !== agent.id">
                  <span @click="startEditing(agent)" class="editable-name" :title="'Click to edit name'">
                    {{ agent.name || 'Unknown' }}
                  </span>
                </div>
                <div class="agent-name-edit d-flex align-items-center" v-else>
                  <input
                    ref="nameInput"
                    v-model="editingName"
                    @blur="saveAgentName(agent)"
                    @keyup.enter="saveAgentName(agent)"
                    @keyup.escape="cancelEditing"
                    :placeholder="agent.name || 'Unknown'"
                    class="form-control form-control-sm name-input"
                    :disabled="processingAgent === agent.id"
                  />
                  <div class="edit-saving ms-2" v-if="processingAgent === agent.id">
                    <div class="spinner-border spinner-border-sm text-primary" style="width: 16px; height: 16px;" role="status">
                      <span class="visually-hidden">Saving...</span>
                    </div>
                  </div>
                </div>
                <div class="agent-id text-muted small">{{ agent.id }}</div>
              </div>
              <div class="agent-actions d-flex align-items-center gap-2">
                <!-- Pending status with action buttons -->
                <div v-if="agent.status === 'PENDING'" class="d-flex flex-column align-items-end gap-1">
                  <span class="badge bg-warning text-dark">
                    {{ agent.status }}
                  </span>
                  <div class="d-flex gap-1">
                    <button
                      class="btn btn-success btn-sm px-2 py-1 d-flex align-items-center justify-content-center"
                      @click="approveAgent(agent)"
                      :disabled="processingAgent === agent.id"
                      style="font-size: 0.7rem;"
                    >
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none" class="me-1">
                        <path d="M9 16.17L4.83 12L3.41 13.41L9 19L21 7L19.59 5.59L9 16.17Z" fill="currentColor"/>
                      </svg>
                      Approve
                    </button>
                    <button
                      class="btn btn-danger btn-sm px-2 py-1 d-flex align-items-center justify-content-center"
                      @click="declineAgent(agent)"
                      :disabled="processingAgent === agent.id"
                      style="font-size: 0.7rem;"
                    >
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none" class="me-1">
                        <path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="currentColor"/>
                      </svg>
                      Decline
                    </button>
                  </div>
                </div>
                <!-- Other statuses -->
                <span
                  v-else
                  :class="getStatusClass(agent.status)"
                  class="badge"
                >
                  {{ agent.status }}
                </span>
              </div>
            </div>
            <!-- Pending message -->
            <div v-if="agent.status === 'PENDING'" class="pending-message mt-2 pt-1 border-top">
              <small class="text-muted">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" class="me-1 text-warning">
                  <path d="M1 21H23L12 2L1 21ZM13 18H11V16H13V18ZM13 14H11V10H13V14Z" fill="currentColor"/>
                </svg>
                This agent is awaiting approval to join the network
              </small>
            </div>
          </div>
        </div>

        <!-- No agents state -->
        <div v-else class="text-center text-muted py-3">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" class="mb-1 opacity-50">
            <path d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 4L13.5 7H7V9H21ZM21 11H7V13H21V11ZM21 15H7V17H21V15ZM21 19H7V21H21V19Z" fill="currentColor"/>
          </svg>
          <p class="mb-0 small">No agents available</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { apiService } from '../services/apiService.js'

const loading = ref(true)
const error = ref(null)
const agents = ref([])
const processingAgent = ref(null)
const editingAgent = ref(null)
const editingName = ref('')

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

const getStatusClass = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-success'
    case 'INACTIVE':
      return 'bg-secondary'
    case 'ERROR':
      return 'bg-danger'
    default:
      return 'bg-secondary'
  }
}

const approveAgent = async (agent) => {
  try {
    processingAgent.value = agent.id
    await apiService.approveAgent(agent.id)
    // Update the agent status locally
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
    // Update the agent status locally
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

const startEditing = (agent) => {
  editingAgent.value = agent.id
  editingName.value = agent.name
}

const saveAgentName = async (agent) => {
  if (!editingName.value.trim()) {
    cancelEditing()
    return
  }

  try {
    processingAgent.value = agent.id
    await apiService.updateAgentName(agent.id, { name: editingName.value.trim() })
    // Update the agent name locally
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

onMounted(() => {
  fetchAgents()
})
</script>

<style scoped>
.agents-container {
  min-height: 200px;
}

.agent-item {
  border: 1px solid #e9ecef;
  transition: all 0.2s ease;
}

.agent-item:hover {
  background-color: #f8f9fa !important;
  border-color: #dee2e6;
}

.agent-name {
  font-size: 0.9rem;
  line-height: 1.3;
}

.agent-id {
  font-size: 0.75rem;
  line-height: 1.2;
  font-family: 'Courier New', monospace;
}

.badge {
  font-size: 0.7rem;
  padding: 0.25rem 0.5rem;
}

.pending-agent {
  border-color: #ffc107;
}

.pending-message {
  border-top: 1px solid #ffc107;
}

.editable-name {
  cursor: pointer;
  text-decoration: underline;
}

.agent-name-edit {
  display: inline-block;
  width: 120px;
}

.name-input {
  font-size: 0.9rem;
  width: 120px;
}

.edit-actions {
  display: flex;
  align-items: center;
}

.edit-saving {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .agent-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .badge {
    align-self: flex-end;
  }
}
</style>
