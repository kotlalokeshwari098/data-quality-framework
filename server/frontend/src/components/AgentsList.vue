<template>
  <div class="agents-container">
    <div class="card border-0 shadow-md rounded-3 overflow-hidden h-100">
      <div class="card-header bg-primary bg-gradient px-4 py-3 border-bottom-0">
        <h6 class="mb-0 fw-semibold text-white">
          <i class="bi bi-database-fill-gear me-2"></i>
          Agents
        </h6>
      </div>
      <div class="card-body p-0">
        <!-- Loading state -->
        <div v-if="loading" class="d-flex justify-content-center align-items-center" style="min-height: 80px;">
          <div class="spinner-border spinner-border-sm text-primary" role="status">
            <span class="visually-hidden">Loading agents...</span>
          </div>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="alert alert-danger mb-0 mx-4 my-2" role="alert">
          <small>{{ error }}</small>
        </div>

        <!-- Agents list -->
        <div v-else-if="agents.length > 0" class="agents-list" style="max-height: 600px; overflow-y: auto;">
          <div
            v-for="agent in agents"
            :key="agent.id"
            class="agent-item border-bottom px-4 py-3 hover-bg-light cursor-pointer"
            :class="{ 'pending-agent': agent.status === 'PENDING' }"
          >
            <div class="d-flex align-items-center justify-content-between">
              <div class="d-flex align-items-center flex-grow-1">
                <div
                  class="status-indicator rounded-circle me-3"
                  :class="getStatusIndicatorClass(agent.status)"
                  style="min-width: 12px; width: 12px; height: 12px;"
                ></div>
                <div class="flex-grow-1">
                  <div class="agent-name fw-medium text-dark" v-if="editingAgent !== agent.id">
                    <span @click.stop="startEditing(agent)" class="editable-name" :title="'Click to edit name'">
                      {{ agent.name || 'Unknown' }}
                    </span>
                  </div>
                  <div class="agent-name-edit d-flex align-items-center" v-else>
                    <input
                      ref="nameInput"
                      v-model="editingName"
                      @blur="saveAgentName(agent)"
                      @keyup.enter="saveAgentName(agent)"
                      @keyup.esc="cancelEditing"
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
                  <p class="agent-id text-muted small mb-0">ID: {{ agent.id }}</p>
                </div>
              </div>
              <div class="agent-actions d-flex align-items-center gap-2 ms-2">
                <!-- Pending status with action buttons -->
                <div v-if="agent.status === 'PENDING'" class="d-flex flex-column align-items-end gap-1">
                  <span class="badge bg-warning text-dark">
                    {{ agent.status }}
                  </span>
                  <div class="d-flex gap-1">
                    <button
                      class="btn btn-success btn-sm px-2 py-1 d-flex align-items-center justify-content-center"
                      @click.stop="approveAgent(agent)"
                      :disabled="processingAgent === agent.id"
                      style="font-size: 0.7rem;"
                    >
                      <i class="bi bi-check-lg me-1"></i>
                      Approve
                    </button>
                    <button
                      class="btn btn-danger btn-sm px-2 py-1 d-flex align-items-center justify-content-center"
                      @click.stop="declineAgent(agent)"
                      :disabled="processingAgent === agent.id"
                      style="font-size: 0.7rem;"
                    >
                      <i class="bi bi-x-lg me-1"></i>
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
            <div v-if="agent.status === 'PENDING'" class="pending-message mt-2 pt-2 border-top">
              <small class="text-muted">
                <i class="bi bi-exclamation-triangle me-1 text-warning"></i>
                This agent is awaiting approval to join the network
              </small>
            </div>
          </div>
        </div>

        <!-- No agents state -->
        <div v-else class="text-center text-muted py-4 px-4">
          <i class="bi bi-database-fill-gear mb-2 opacity-50" style="font-size: 1.5rem;"></i>
          <p class="mb-0 small">No agents available</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { apiService } from '../services/apiService.js'

const emit = defineEmits(['agentsLoaded'])

const loading = ref(true)
const error = ref(null)
const agents = ref([])
const processingAgent = ref(null)
const editingAgent = ref(null)
const editingName = ref('')

const activeAgentsCount = computed(() => {
  return agents.value.filter(agent => agent.status === 'ACTIVE').length
})

// Emit active agents count whenever it changes
watch(activeAgentsCount, (count) => {
  emit('agentsLoaded', count)
}, { immediate: true })

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

const getStatusIndicatorClass = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-success'
    case 'INACTIVE':
      return 'bg-secondary'
    case 'ERROR':
      return 'bg-danger'
    case 'PENDING':
      return 'bg-warning'
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
    await apiService.updateAgentName(agent.id, editingName.value.trim())
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
  transition: background-color 0.2s ease;
  border-left: none !important;
  border-right: none !important;
  border-top: none !important;
}

.agent-item:last-child {
  border-bottom: none !important;
}

.hover-bg-light:hover {
  background-color: #f8f9fa !important;
}

.cursor-pointer {
  cursor: pointer;
}

.agent-name {
  font-size: 1rem;
  line-height: 1.3;
}

.agent-id {
  font-size: 0.875rem;
  line-height: 1.2;
}

.badge {
  font-size: 0.7rem;
  padding: 0.25rem 0.5rem;
}

.pending-agent {
  background-color: #fff3cd;
}

.pending-message {
  border-top-color: #ffc107 !important;
}

.editable-name {
  cursor: pointer;
  text-decoration: underline;
  text-decoration-style: dotted;
}

.editable-name:hover {
  text-decoration-style: solid;
}

.agent-name-edit {
  max-width: 200px;
}

.name-input {
  font-size: 0.9rem;
  min-width: 150px;
}

.edit-saving {
  display: flex;
  align-items: center;
}

.status-indicator {
  flex-shrink: 0;
}

/* Custom scrollbar for agents list */
.agents-list::-webkit-scrollbar {
  width: 6px;
}

.agents-list::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.agents-list::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.agents-list::-webkit-scrollbar-thumb:hover {
  background: #555;
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
