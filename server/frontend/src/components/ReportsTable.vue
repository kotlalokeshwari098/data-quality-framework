<template>
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
              <th>Total Checks</th>
              <th>Warnings</th>
              <th>Errors</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="report in reports"
              :key="report.id"
              class="cursor-pointer"
              @click="$emit('report-selected', report)"
            >
              <td class="font-monospace small">{{ report.id }}</td>
              <td>{{ formatDate(report.timestamp) }}</td>
              <td>
                <span class="text-muted">
                  <i class="bi bi-list-check me-1"></i>{{ getCheckCounts(report).total }}
                </span>
              </td>
              <td>
                <span class="text-warning">
                  <i class="bi bi-exclamation-triangle me-1"></i>{{ getCheckCounts(report).warnings }}
                </span>
              </td>
              <td>
                <span class="text-danger">
                  <i class="bi bi-x-circle me-1"></i>{{ getCheckCounts(report).failed }}
                </span>
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
</template>

<script setup>
import { countChecksByStatus } from '../utils/qualityCheckUtils.js'

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

const getCheckCounts = (report) => {
  return countChecksByStatus(report, props.qualityCheckMap)
}
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

.cursor-pointer {
  cursor: pointer;
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

