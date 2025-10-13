<template>
  <div class="card border-0 shadow-sm h-100 compact-card">
    <div class="card-body p-3 position-relative">
      <!-- Icon in top right corner -->
      <div class="position-absolute top-0 end-0 p-3">
        <i class="bi bi-clipboard-data-fill text-primary opacity-75" style="font-size: 1.5rem;"></i>
      </div>

      <div class="mb-3">
        <p class="text-muted mb-1 fw-semibold" style="font-size: 1rem; line-height: 1.3;">{{ qualityCheck.cql || qualityCheck.hash }}</p>
        <small class="text-muted font-monospace d-block" style="font-size: 0.7rem; opacity: 0.6;">{{ qualityCheck.hash }}</small>
      </div>

      <!-- No data state -->
      <div v-if="worstAgents.length === 0" class="text-center py-3 text-muted flex-grow-1 d-flex flex-column justify-content-center">
        <i class="bi bi-check-circle d-block mb-2 opacity-50" style="font-size: 2.5rem;"></i>
        <p class="mb-0" style="font-size: 1rem;">No results</p>
      </div>

      <!-- Top 3 worst performing agents -->
      <div v-else class="agents-results flex-grow-1">
        <div
          v-for="agentResult in worstAgents"
          :key="agentResult.agentId"
          class="agent-result-item mb-2"
        >
          <div class="d-flex justify-content-between align-items-center">
            <div class="flex-grow-1 me-2" style="min-width: 0;">
              <div class="fw-semibold text-dark text-truncate" style="font-size: 1.1rem; line-height: 1.3;">{{ agentResult.agentName }}</div>
              <small class="text-muted d-block text-truncate" style="font-size: 0.85rem; line-height: 1.3;">{{ agentResult.agentId }}</small>
            </div>
            <div class="text-end flex-shrink-0">
              <div
                class="fw-bold mb-0"
                :class="getResultColorClass(agentResult.result)"
                style="font-size: 1.75rem; line-height: 1;"
              >
                {{ formatResultAsPercentage(agentResult.result) }}%
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Threshold info -->
      <div v-if="worstAgents.length > 0" class="mt-auto pt-2 border-top">
        <div class="d-flex justify-content-between align-items-center">
          <small class="text-muted" style="font-size: 0.75rem;">
            <i class="bi bi-exclamation-triangle-fill text-warning me-1"></i>
            Warn: {{ qualityCheck.warningThreshold }}
          </small>
          <small class="text-muted" style="font-size: 0.75rem;">
            <i class="bi bi-x-circle-fill text-danger me-1"></i>
            Error: {{ qualityCheck.errorThreshold }}
          </small>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { CheckStatus } from '../utils/qualityCheckUtils.js'

const props = defineProps({
  qualityCheck: {
    type: Object,
    required: true
  },
  reports: {
    type: Array,
    required: true,
    default: () => []
  },
  agents: {
    type: Array,
    required: true,
    default: () => []
  }
})

// Create a map of agent ID to agent name
const agentMap = computed(() => {
  const map = new Map()
  props.agents.forEach(agent => {
    map.set(agent.id, agent.name || 'Unknown Agent')
  })
  return map
})

// Aggregate results per agent for this specific check
const agentResults = computed(() => {
  const resultsMap = new Map()

  props.reports.forEach(report => {
    // Find the result for this quality check in the report
    const result = report.results?.find(r => r.hash === props.qualityCheck.hash)

    if (result) {
      const agentId = report.agentId || report.agent?.id || 'unknown'

      // Keep the latest (highest) result for each agent
      if (!resultsMap.has(agentId) || resultsMap.get(agentId).result < result.result) {
        resultsMap.set(agentId, {
          agentId,
          agentName: agentMap.value.get(agentId) || agentId,
          result: result.result,
          timestamp: report.timestamp
        })
      }
    }
  })

  return Array.from(resultsMap.values())
})

// Get the 3 worst performing agents (highest result values)
const worstAgents = computed(() => {
  return agentResults.value
    .sort((a, b) => b.result - a.result)
    .slice(0, 3)
})

// Get status based on thresholds
const getStatus = (result) => {
  if (result > props.qualityCheck.errorThreshold) {
    return CheckStatus.FAILED
  } else if (result > props.qualityCheck.warningThreshold) {
    return CheckStatus.WARNING
  } else {
    return CheckStatus.PASSED
  }
}

const getResultColorClass = (result) => {
  const status = getStatus(result)
  switch (status) {
    case CheckStatus.FAILED:
      return 'text-danger'
    case CheckStatus.WARNING:
      return 'text-warning'
    case CheckStatus.PASSED:
      return 'text-success'
    default:
      return 'text-muted'
  }
}

// Format result as percentage (multiply by 100 if between 0-1, otherwise show as is)
const formatResultAsPercentage = (result) => {
  // If result is between 0 and 1, treat it as a decimal percentage
  if (result >= 0 && result <= 1) {
    return (result * 100).toFixed(1)
  }
  // Otherwise, show the raw value
  return result.toFixed(1)
}
</script>

<style scoped>
.compact-card {
  font-size: 0.875rem;
}

.compact-card .card-body {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.font-monospace {
  font-size: 0.6rem;
}

.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
