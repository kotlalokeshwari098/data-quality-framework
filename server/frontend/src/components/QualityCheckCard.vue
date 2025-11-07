<template>
  <div class="card border-0 shadow-sm h-100 compact-card">
    <div class="card-body p-3 position-relative">
      <!-- Icon in top right corner -->
      <div class="position-absolute top-0 end-0 p-3" style="pointer-events: none;">
        <i class="bi bi-clipboard-check-fill text-primary opacity-75" style="font-size: 1.5rem;"></i>
      </div>

      <div class="mb-3 pe-5">
        <div class="d-flex align-items-center gap-2">
          <p
            class="text-muted mb-0 fw-bold text-truncate flex-grow-1"
            style="font-size: 1rem; line-height: 1.3;"
            :title="qualityCheck.name || qualityCheck.cql || qualityCheck.hash"
          >
            {{ qualityCheck.name || qualityCheck.cql || qualityCheck.hash }}
          </p>
          <i
            v-if="qualityCheck.description"
            class="bi bi-question-circle text-muted flex-shrink-0"
            style="font-size: 0.9rem; cursor: help;"
            :title="qualityCheck.description"
            data-bs-toggle="tooltip"
            data-bs-placement="top"
          ></i>
        </div>
        <small
          class="text-muted font-monospace d-block text-truncate"
          style="font-size: 0.7rem; opacity: 0.6;"
          :title="qualityCheck.hash"
        >
          {{ qualityCheck.hash }}
        </small>
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
              <div
                class="text-dark text-truncate"
                style="font-size: 1.1rem; line-height: 1.3;"
                :title="agentResult.agentName"
              >
                {{ agentResult.agentName }}
              </div>
              <small
                class="text-muted d-block text-truncate"
                style="font-size: 0.85rem; line-height: 1.3;"
                :title="agentResult.agentId"
              >
                {{ agentResult.agentId }}
              </small>
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
        <div class="d-flex align-items-center">
          <div
            style="font-size: 1rem; cursor: help;"
            :title="getThresholdTooltip()"
            data-bs-toggle="tooltip"
            data-bs-placement="top"
            data-bs-html="true"
          >
            <span class="text-muted">Average across all agents:</span>
            <span :class="getResultColorClass(averageResult)" class="fw-bold ms-1">
              {{ formatResultAsPercentage(averageResult) }}%
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUpdated } from 'vue'
import { CheckStatus } from '../utils/qualityCheckUtils.js'
import { Tooltip } from 'bootstrap'

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

      // Normalize result to fraction (0-1). If backend already provides percentage (0-100), convert.
      const raw = result.result
      const fraction = typeof raw === 'number' ? (raw > 1 ? raw / 100 : raw) : 0

      // Keep the latest (highest) result for each agent
      if (!resultsMap.has(agentId) || resultsMap.get(agentId).result < fraction) {
        resultsMap.set(agentId, {
          agentId,
          agentName: agentMap.value.get(agentId) || agentId,
          result: fraction,
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

// Calculate the average result of the worst agents
const averageResult = computed(() => {
  if (worstAgents.value.length === 0) {
    return 0
  }
  const total = worstAgents.value.reduce((sum, agent) => sum + agent.result, 0)
  return total / worstAgents.value.length
})

// Get status based on thresholds (higher is worse)
// Accept result as fraction (0-1) or percentage (0-100)
const getStatus = (result) => {
  const percentage = result <= 1 ? result * 100 : result
  if (percentage > props.qualityCheck.errorThreshold) {
    return CheckStatus.FAILED
  } else if (percentage > props.qualityCheck.warningThreshold) {
    return CheckStatus.WARNING
  }
  return CheckStatus.PASSED
}

// Generate formatted tooltip for thresholds
const getThresholdTooltip = () => {
  return `
    <div style="text-align: left; padding: 4px;">
      <strong style="display: block; margin-bottom: 6px; font-size: 0.9rem;">Quality Check Thresholds</strong>
      <div style="margin-bottom: 4px; font-size: 0.85rem; opacity: 0.9;">
        Higher values indicate worse quality
      </div>
      <div style="margin-bottom: 4px;">
        <span style="color: #ffc107; font-weight: bold;">⚠</span>
        <strong>Warning:</strong> ≥ ${props.qualityCheck.warningThreshold}%
      </div>
      <div>
        <span style="color: #dc3545; font-weight: bold;">✖</span>
        <strong>Error:</strong> ≥ ${props.qualityCheck.errorThreshold}%
      </div>
    </div>
  `
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

// Format result as percentage from fraction
const formatResultAsPercentage = (fraction) => {
  return (fraction * 100).toFixed(1)
}

// Initialize Bootstrap tooltips
onMounted(() => {
  initTooltips()
})

onUpdated(() => {
  initTooltips()
})

const initTooltips = () => {
  const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
  tooltipTriggerList.forEach(tooltipTriggerEl => {
    // Dispose existing tooltip if any
    const existingTooltip = Tooltip.getInstance(tooltipTriggerEl)
    if (existingTooltip) {
      existingTooltip.dispose()
    }
    // Create new tooltip
    new Tooltip(tooltipTriggerEl)
  })
}
</script>

<style scoped>
.compact-card {
  font-size: 0.875rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  will-change: transform, box-shadow;
}

.compact-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 0.5rem 1.5rem rgba(0, 0, 0, 0.15) !important;
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
