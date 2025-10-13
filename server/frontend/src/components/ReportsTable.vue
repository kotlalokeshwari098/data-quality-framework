<template>
  <div class="card border-0 shadow-sm">
    <div class="card-header bg-white border-bottom py-3">
      <div class="d-flex justify-content-between align-items-center">
        <h5 class="mb-0 fw-semibold">Recent Reports</h5>
        <span class="badge bg-secondary">{{ reports.length }} reports</span>
      </div>
    </div>
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-hover mb-0 align-middle">
          <thead class="table-light">
            <tr>
              <th class="ps-4">Report ID</th>
              <th>Timestamp</th>
              <th class="text-center">Status</th>
              <th class="text-center">Total Checks</th>
              <th class="text-center">Warnings</th>
              <th class="text-center">Errors</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="report in reports"
              :key="report.id"
              class="cursor-pointer table-row-hover"
              @click="$emit('report-selected', report)"
            >
              <td class="ps-4">
                <div class="d-flex align-items-center">
                  <i class="bi bi-file-earmark-text text-primary me-2"></i>
                  <span class="font-monospace small text-truncate report-id">{{ report.id }}</span>
                </div>
              </td>
              <td>
                <div class="d-flex flex-column">
                  <span class="fw-medium">{{ formatDateShort(report.timestamp) }}</span>
                  <small class="text-muted">{{ formatTime(report.timestamp) }}</small>
                </div>
              </td>
              <td class="text-center">
                <span class="badge rounded-pill" :class="getReportStatusBadgeClass(report)">
                  {{ getReportStatusText(report) }}
                </span>
              </td>
              <td class="text-center">
                <span class="text-muted">{{ getCheckCounts(report).total }}</span>
              </td>
              <td class="text-center">
                <span :class="getCheckCounts(report).warnings > 0 ? 'text-warning fw-semibold' : 'text-muted'">
                  {{ getCheckCounts(report).warnings }}
                </span>
              </td>
              <td class="text-center">
                <span :class="getCheckCounts(report).failed > 0 ? 'text-danger fw-semibold' : 'text-muted'">
                  {{ getCheckCounts(report).failed }}
                </span>
              </td>
            </tr>
            <tr v-if="reports.length === 0">
              <td colspan="6" class="text-center text-muted py-5">
                <i class="bi bi-inbox fs-1 d-block mb-2 opacity-50"></i>
                <p class="mb-0">No reports available for this agent</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { countChecksByStatus, getReportStatus, CheckStatus } from '../utils/qualityCheckUtils.js'

const props = defineProps({
  reports: {
    type: Array,
    required: true,
    default: () => []
  },
  qualityCheckMap: {
    type: Map,
    required: true
  }
})

defineEmits(['report-selected'])

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
  return date.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getCheckCounts = (report) => {
  return countChecksByStatus(report, props.qualityCheckMap)
}

const getReportStatusText = (report) => {
  const status = getReportStatus(report, props.qualityCheckMap)
  return status
}

const getReportStatusBadgeClass = (report) => {
  const status = getReportStatus(report, props.qualityCheckMap)

  switch (status) {
    case CheckStatus.PASSED:
      return 'bg-success'
    case CheckStatus.WARNING:
      return 'bg-warning text-dark'
    case CheckStatus.FAILED:
      return 'bg-danger'
    default:
      return 'bg-secondary'
  }
}
</script>

<style scoped>
.table th {
  font-weight: 600;
  font-size: 0.813rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #6c757d;
  padding: 1rem 0.75rem;
  border-bottom: 2px solid #dee2e6;
  white-space: nowrap;
}

.table td {
  vertical-align: middle;
  padding: 1rem 0.75rem;
  border-bottom: 1px solid #f0f0f0;
}

.table-responsive {
  overflow-x: visible;
}

.report-id {
  max-width: 150px;
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-row-hover {
  transition: all 0.2s ease-in-out;
}

.table-row-hover:hover {
  background-color: #f8f9fa;
  transform: translateX(2px);
  box-shadow: inset 3px 0 0 #0d6efd;
}

.font-monospace {
  font-family: 'SF Mono', 'Monaco', 'Courier New', monospace;
  font-size: 0.875rem;
}

.cursor-pointer {
  cursor: pointer;
}

.badge {
  font-weight: 500;
  padding: 0.35rem 0.65rem;
  font-size: 0.75rem;
  white-space: nowrap;
}

.badge.rounded-pill {
  padding: 0.35rem 0.85rem;
}

/* Status indicator animation */
.badge.bg-danger,
.badge.bg-warning {
  animation: pulse-subtle 2s ease-in-out infinite;
}

@keyframes pulse-subtle {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.85;
  }
}

/* Responsive adjustments */
@media (max-width: 992px) {
  .table th,
  .table td {
    padding: 0.75rem 0.5rem;
  }

  .report-id {
    max-width: 120px;
  }
}

@media (max-width: 768px) {
  .table {
    font-size: 0.75rem;
  }

  .table th,
  .table td {
    padding: 0.5rem 0.35rem;
  }

  .report-id {
    max-width: 100px;
  }

  .badge {
    font-size: 0.65rem;
    padding: 0.25rem 0.45rem;
  }

  .badge i {
    display: none;
  }
}

@media (max-width: 576px) {
  .table-responsive {
    overflow-x: auto;
  }

  .table th:nth-child(4),
  .table td:nth-child(4) {
    display: none;
  }
}
</style>
