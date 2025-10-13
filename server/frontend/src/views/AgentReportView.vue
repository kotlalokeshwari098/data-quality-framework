<template>
  <div class="container-fluid py-3 py-md-4">
    <!-- Agent Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex align-items-center mb-3">
          <button @click="goBack" class="btn btn-outline-secondary btn-sm me-3">
            <i class="bi bi-arrow-left me-1"></i>
            Back
          </button>
          <div>
            <h2 class="mb-1">{{ agentName }}</h2>
            <p class="text-muted mb-0">Agent ID: {{ agentId }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading agent report...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger" role="alert">
      <i class="bi bi-exclamation-triangle me-2"></i>
      {{ error }}
    </div>

    <!-- Agent Report Content -->
    <div v-else>
      <!-- Stats Cards -->
      <div class="row mb-4">
        <div class="col-12 col-sm-6 col-md-3 mb-3">
          <StatsCard
            label="Total Reports"
            :value="reportStats.total"
            icon="bi bi-file-text"
            iconColor="#0d6efd"
            iconBgColor="#cfe2ff"
            trendText="All time"
            trendType="neutral"
          />
        </div>
        <div class="col-12 col-sm-6 col-md-3 mb-3">
          <StatsCard
            label="Failed Checks"
            :value="reportStats.failed"
            icon="bi bi-x-circle"
            iconColor="#dc3545"
            iconBgColor="#f8d7da"
            trendText="Needs attention"
            trendType="negative"
          />
        </div>
        <div class="col-12 col-sm-6 col-md-3 mb-3">
          <StatsCard
            label="Warning Checks"
            :value="reportStats.warnings"
            icon="bi bi-exclamation-triangle"
            iconColor="#ffc107"
            iconBgColor="#fff3cd"
            trendText="Review recommended"
            trendType="neutral"
          />
        </div>
        <div class="col-12 col-sm-6 col-md-3 mb-3">
          <StatsCard
            label="Last Report"
            :value="reportStats.lastReportTime"
            icon="bi bi-clock"
            iconColor="#0dcaf0"
            iconBgColor="#cff4fc"
            trendText="Timestamp"
            trendType="neutral"
          />
        </div>
      </div>

      <!-- Recent Reports Table -->
      <div class="row">
        <div class="col-12">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-white border-bottom py-3">
              <h5 class="mb-0 fw-semibold">Recent Reports</h5>
            </div>
            <div class="card-body p-0">
              <div class="table-responsive">
                <table class="table table-hover mb-0">
                  <thead class="table-light">
                    <tr>
                      <th>Report ID</th>
                      <th>Date</th>
                      <th>Checks Passed</th>
                      <th>Checks Failed</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="report in reports" :key="report.id">
                      <td class="font-monospace small">{{ report.id }}</td>
                      <td>{{ formatDate(report.timestamp) }}</td>
                      <td>
                        <span class="text-success">
                          <i class="bi bi-check-circle me-1"></i>{{ getPassedChecks(report) }}
                        </span>
                      </td>
                      <td>
                        <span class="text-danger">
                          <i class="bi bi-x-circle me-1"></i>{{ getFailedChecks(report) }}
                        </span>
                      </td>
                      <td>
                        <button class="btn btn-sm btn-outline-primary">
                          <i class="bi bi-eye me-1"></i>View
                        </button>
                      </td>
                    </tr>
                    <tr v-if="reports.length === 0">
                      <td colspan="5" class="text-center text-muted py-4">
                        No reports available for this agent
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StatsCard from '../components/StatsCard.vue'
import { apiService } from '../services/apiService.js'

const route = useRoute()
const router = useRouter()

const agentId = ref(route.params.uuid)
const loading = ref(true)
const error = ref(null)
const agent = ref(null)
const reports = ref([])
const qualityChecks = ref([])

const agentName = computed(() => {
  return agent.value?.name || 'Unknown Agent'
})

// Create a map of hash -> quality check for quick lookup
const qualityCheckMap = computed(() => {
  const map = new Map()
  qualityChecks.value.forEach(check => {
    map.set(check.hash, check)
  })
  return map
})

const reportStats = computed(() => {
  const total = reports.value.length

  // Get the latest report for stats calculation
  const sortedReports = [...reports.value].sort((a, b) =>
    new Date(b.timestamp) - new Date(a.timestamp)
  )
  const latestReport = sortedReports[0]

  // Calculate stats from the latest report only
  let passedChecks = 0
  let failedChecks = 0
  let warningChecks = 0

  if (latestReport?.results && Array.isArray(latestReport.results)) {
    latestReport.results.forEach(result => {
      const check = qualityCheckMap.value.get(result.hash)
      const errorThreshold = check?.errorThreshold || 0.5
      const warningThreshold = check?.warningThreshold || 0.7

      // Failed: result < errorThreshold
      if (result.result < errorThreshold) {
        failedChecks++
      }
      // Warning: result >= errorThreshold but < warningThreshold
      else if (result.result < warningThreshold) {
        warningChecks++
      }
      // Passed: result >= warningThreshold
      else {
        passedChecks++
      }
    })
  }

  const lastReportTime = latestReport ? formatTime(latestReport.timestamp) : 'N/A'

  return {
    total,
    failed: failedChecks,
    passed: passedChecks,
    warnings: warningChecks,
    lastReportTime
  }
})

const fetchAgentDetails = async () => {
  try {
    loading.value = true
    error.value = null

    // Fetch quality checks first
    const qualityChecksResponse = await apiService.getQualityChecks()
    qualityChecks.value = qualityChecksResponse._embedded?.qualityChecks || []

    // Fetch agent details
    const agentsResponse = await apiService.getAgents()
    const agents = agentsResponse._embedded?.agents || []
    agent.value = agents.find(a => a.id === agentId.value)

    if (!agent.value) {
      error.value = 'Agent not found'
      return
    }

    // Fetch real reports from the API
    const reportsResponse = await apiService.getAgentReports(agentId.value)
    const reportsList = reportsResponse._embedded?.reports || reportsResponse.reports || []

    // Sort reports by timestamp (newest first)
    reports.value = reportsList.sort((a, b) =>
      new Date(b.timestamp) - new Date(a.timestamp)
    )

  } catch (err) {
    error.value = err.message || 'Failed to load agent report'
    console.error('Error fetching agent details:', err)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
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

  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'SUCCESS':
      return 'bg-success'
    case 'FAILED':
      return 'bg-danger'
    case 'WARNING':
      return 'bg-warning text-dark'
    default:
      return 'bg-secondary'
  }
}

const getPassedChecks = (report) => {
  if (!report.results || !Array.isArray(report.results)) {
    return 0
  }
  return report.results.filter(result => {
    const check = qualityCheckMap.value.get(result.hash)
    const threshold = check?.errorThreshold || 0.5
    return result.result >= threshold
  }).length
}

const getFailedChecks = (report) => {
  if (!report.results || !Array.isArray(report.results)) {
    return 0
  }
  return report.results.filter(result => {
    const check = qualityCheckMap.value.get(result.hash)
    const threshold = check?.errorThreshold || 0.5
    return result.result < threshold
  }).length
}

const getReportStatus = (report) => {
  if (!report.results || report.results.length === 0) {
    return 'NO DATA'
  }

  // Calculate status based on individual check thresholds
  let hasError = false
  let hasWarning = false

  report.results.forEach(result => {
    const check = qualityCheckMap.value.get(result.hash)
    if (check) {
      if (result.result < check.errorThreshold) {
        hasError = true
      } else if (result.result < check.warningThreshold) {
        hasWarning = true
      }
    }
  })

  if (hasError) return 'FAILED'
  if (hasWarning) return 'WARNING'
  return 'SUCCESS'
}

const getReportStatusBadgeClass = (report) => {
  const status = getReportStatus(report)
  return getStatusBadgeClass(status)
}

const goBack = () => {
  router.push('/dashboard')
}

onMounted(() => {
  fetchAgentDetails()
})
</script>

<style scoped>
.table th {
  font-weight: 600;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #6c757d;
}

.table td {
  vertical-align: middle;
}

.font-monospace {
  font-family: 'Courier New', monospace;
}

@media (max-width: 768px) {
  .table {
    font-size: 0.875rem;
  }

  .table th,
  .table td {
    padding: 0.5rem;
  }
}
</style>
