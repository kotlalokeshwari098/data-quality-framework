<template>
  <div class="container-fluid py-3 py-md-4">
    <!-- Dashboard Header -->
    <PageHeader
      title="Site Performance Overview"
      subtitle="Review Data Quality metrics from all connected locations"
      icon="bi bi-grid-3x3-gap-fill"
      :hide-subtitle-on-mobile="false"
    />

    <!-- Stats Cards Row -->
    <div class="stats-row mb-4">
      <StatsCard
        label="Sites Monitored"
        :value="`${activeAgentsCount}`"
        icon="bi bi-geo-alt-fill"
        iconColor="#0d6efd"
        iconBgColor="#cfe2ff"
        :trendText="`${activeAgentsCount} agents added this month`"
        trendType="positive"
      />
      <StatsCard
        label="Total Reports"
        :value="`${reports.length}`"
        icon="bi bi-file-earmark-text-fill"
        iconColor="#6f42c1"
        iconBgColor="#e2d9f3"
        :trendText="reportsChange"
        :trendType="reportsTrendType"
      />
      <StatsCard
        label="Errors This Week"
        :value="`${errorsThisWeek}`"
        icon="bi bi-exclamation-triangle-fill"
        iconColor="#dc3545"
        iconBgColor="#f8d7da"
        :trendText="errorsChange"
        :trendType="errorsTrendType"
      />
      <StatsCard
        label="Warnings This Week"
        :value="`${warningsThisWeek}`"
        icon="bi bi-exclamation-circle-fill"
        iconColor="#ffc107"
        iconBgColor="#fff3cd"
        :trendText="warningsChange"
        :trendType="warningsTrendType"
      />
    </div>

    <!-- Privacy Note -->
    <AppCallout type="info" icon="bi-shield-lock" class="mb-3">
      <small>
        Results on this dashboard are obfuscated using differential privacy to protect sensitive information.
        <a
          href="https://bbmri-cz.github.io/data-quality-framework/user/privacy.html"
          target="_blank"
          rel="noopener noreferrer"
          class="fw-semibold"
        >Learn more</a>.
      </small>
    </AppCallout>

    <!-- Main Content Grid -->
    <div class="content-grid">
      <!-- Agents Section -->
      <div class="agents-wrapper">
        <AgentsList @agentsLoaded="handleAgentsLoaded" />
      </div>

      <!-- Quality Checks Grid -->
      <div class="quality-checks-grid">
        <QualityCheckCard
          v-for="check in qualityChecks"
          :key="check.hash"
          :quality-check="check"
          :reports="reports"
          :agents="agents"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AgentsList from '../components/AgentsList.vue'
import StatsCard from '../components/StatsCard.vue'
import PageHeader from '../components/PageHeader.vue'
import QualityCheckCard from '../components/QualityCheckCard.vue'
import AppCallout from '../components/AppCallout.vue'
import { apiService } from '../services/apiService.js'
import { getReportStatus, CheckStatus } from '../utils/qualityCheckUtils.js'

const activeAgentsCount = ref(0)
const reports = ref([])
const qualityCheckMap = ref(new Map())
const agents = ref([])

// Get reports from this week (last 7 days)
const reportsThisWeek = computed(() => {
  const oneWeekAgo = new Date()
  oneWeekAgo.setDate(oneWeekAgo.getDate() - 7)

  return reports.value.filter(report => {
    const reportDate = new Date(report.timestamp)
    return reportDate >= oneWeekAgo
  })
})

// Get reports from the previous week (8-14 days ago)
const reportsLastWeek = computed(() => {
  const twoWeeksAgo = new Date()
  twoWeeksAgo.setDate(twoWeeksAgo.getDate() - 14)
  const oneWeekAgo = new Date()
  oneWeekAgo.setDate(oneWeekAgo.getDate() - 7)

  return reports.value.filter(report => {
    const reportDate = new Date(report.timestamp)
    return reportDate >= twoWeeksAgo && reportDate < oneWeekAgo
  })
})

// Count errors from this week
const errorsThisWeek = computed(() => {
  return reportsThisWeek.value.filter(report => {
    const status = getReportStatus(report, qualityCheckMap.value)
    return status === CheckStatus.FAILED
  }).length
})

// Count errors from last week
const errorsLastWeek = computed(() => {
  return reportsLastWeek.value.filter(report => {
    const status = getReportStatus(report, qualityCheckMap.value)
    return status === CheckStatus.FAILED
  }).length
})

// Count warnings from this week
const warningsThisWeek = computed(() => {
  return reportsThisWeek.value.filter(report => {
    const status = getReportStatus(report, qualityCheckMap.value)
    return status === CheckStatus.WARNING
  }).length
})

// Count warnings from last week
const warningsLastWeek = computed(() => {
  return reportsLastWeek.value.filter(report => {
    const status = getReportStatus(report, qualityCheckMap.value)
    return status === CheckStatus.WARNING
  }).length
})

// Calculate change in reports from last week
const reportsChange = computed(() => {
  const change = reportsThisWeek.value.length - reportsLastWeek.value.length
  if (change === 0) return 'No change from last week'
  const direction = change > 0 ? '+' : ''
  return `${direction}${change} from last week`
})

// Calculate change in errors from last week
const errorsChange = computed(() => {
  const change = errorsThisWeek.value - errorsLastWeek.value
  if (change === 0) return 'No change from last week'
  const direction = change > 0 ? '+' : ''
  return `${direction}${change} from last week`
})

// Calculate change in warnings from last week
const warningsChange = computed(() => {
  const change = warningsThisWeek.value - warningsLastWeek.value
  if (change === 0) return 'No change from last week'
  const direction = change > 0 ? '+' : ''
  return `${direction}${change} from last week`
})

// Determine trend type for reports
const reportsTrendType = computed(() => {
  const change = reportsThisWeek.value.length - reportsLastWeek.value.length
  if (change > 0) return 'positive'
  if (change < 0) return 'negative'
  return 'neutral'
})

// Determine trend type for errors (fewer is better)
const errorsTrendType = computed(() => {
  const change = errorsThisWeek.value - errorsLastWeek.value
  if (change < 0) return 'positive'  // fewer errors is positive
  if (change > 0) return 'negative'  // more errors is negative
  return 'neutral'
})

// Determine trend type for warnings (fewer is better)
const warningsTrendType = computed(() => {
  const change = warningsThisWeek.value - warningsLastWeek.value
  if (change < 0) return 'positive'  // fewer warnings is positive
  if (change > 0) return 'negative'  // more warnings is negative
  return 'neutral'
})

// Get array of quality checks for iteration
const qualityChecks = computed(() => {
  return Array.from(qualityCheckMap.value.values())
})

const handleAgentsLoaded = (count) => {
  activeAgentsCount.value = count
}

const loadReportsData = async () => {
  try {
    // Fetch quality checks, reports, and agents in parallel
    const [checksData, reportsData, agentsData] = await Promise.all([
      apiService.getQualityChecks(),
      apiService.getReports(),
      apiService.getAgents()
    ])

    // Handle HAL format response for quality checks
    const checks = checksData._embedded?.qualityChecks || (Array.isArray(checksData) ? checksData : [])

    // Handle HAL format response for reports
    const reportsArray = reportsData._embedded?.reports || (Array.isArray(reportsData) ? reportsData : [])

    // Handle HAL format response for agents
    agents.value = agentsData._embedded?.agents || (Array.isArray(agentsData) ? agentsData : [])

    // Convert quality checks array to Map for quick lookup
    qualityCheckMap.value = new Map(checks.map(check => [check.hash, check]))

    // Sort reports by timestamp (newest first)
    reports.value = reportsArray.sort((a, b) =>
      new Date(b.timestamp) - new Date(a.timestamp)
    )
  } catch (err) {
    console.error('Error fetching reports:', err)
  }
}

onMounted(() => {
  loadReportsData()
})
</script>

<style scoped>
/* Stats Row */
.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

/* Main Content Grid */
.content-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: 1fr;
}

.agents-wrapper {
  width: 100%;
}

.quality-checks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
  align-items: start;
}

.quality-checks-grid > * {
  min-height: 150px;
}

/* Desktop Layout */
@media (min-width: 992px) {
  .content-grid {
    grid-template-columns: 280px 1fr;
  }

  .agents-wrapper {
    width: 280px;
  }

  .quality-checks-grid {
    grid-template-columns: repeat(auto-fill, 480px);
  }

  .quality-checks-grid > * {
    height: 480px;
  }
}

/* Tablet Layout */
@media (min-width: 768px) and (max-width: 991px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* Mobile Layout */
@media (max-width: 767px) {
  .container-fluid {
    padding-left: 0.75rem;
    padding-right: 0.75rem;
  }

  .stats-row {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .content-grid {
    gap: 0.75rem;
  }

  .quality-checks-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .quality-checks-grid > * {
    min-height: auto;
  }
}
</style>
