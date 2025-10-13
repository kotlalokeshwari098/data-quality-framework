<template>
  <div
    v-if="report"
    class="modal fade show d-block"
    tabindex="-1"
    style="background-color: rgba(0,0,0,0.5);"
  >
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header bg-primary bg-gradient text-white">
          <h5 class="modal-title">
            <i class="bi bi-file-earmark-text me-2"></i>
            Report Details: {{ report.id }}
          </h5>
          <button
            type="button"
            class="btn-close btn-close-white"
            @click="$emit('close')"
          ></button>
        </div>
        <div class="modal-body p-0">
          <!-- Report Info -->
          <div class="p-3 bg-light border-bottom">
            <div class="row">
              <div class="col-md-6">
                <small class="text-muted d-block">Report ID</small>
                <span class="font-monospace">{{ report.id }}</span>
              </div>
              <div class="col-md-6">
                <small class="text-muted d-block">Timestamp</small>
                <span>{{ formatDate(report.timestamp) }}</span>
              </div>
            </div>
          </div>

          <!-- Checks List -->
          <div class="list-group list-group-flush">
            <div
              v-if="!report.results || report.results.length === 0"
              class="p-4 text-center text-muted"
            >
              <i class="bi bi-inbox mb-2" style="font-size: 2rem;"></i>
              <p class="mb-0">No check results available for this report</p>
            </div>
            <div
              v-for="result in report.results"
              :key="result.hash"
              class="list-group-item list-group-item-action"
            >
              <div class="d-flex justify-content-between align-items-start">
                <div class="flex-grow-1">
                  <h6 class="mb-1">{{ getCheckName(result.hash) }}</h6>
                  <p class="mb-1 small text-muted">{{ getCheckDescription(result.hash) }}</p>
                  <div class="mt-2">
                    <span class="badge me-2" :class="getCheckBadgeClass(result)">
                      {{ getCheckStatusText(result) }}
                    </span>
                    <span class="text-muted small">Score: {{ formatScore(result.result) }}</span>
                  </div>
                </div>
                <div class="ms-3">
                  <div class="circular-progress" :class="getCheckProgressColor(result)">
                    <span class="progress-value">{{ formatScoreRounded(result.result) }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="$emit('close')">
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  getCheckStatus,
  getStatusBadgeClass,
  getProgressColorClass,
  formatScore,
  formatScoreRounded
} from '../utils/qualityCheckUtils.js'

const props = defineProps({
  report: {
    type: Object,
    default: null
  },
  qualityCheckMap: {
    type: Map,
    required: true
  }
})

defineEmits(['close'])

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

const getCheckName = (hash) => {
  const check = props.qualityCheckMap.get(hash)
  return check?.name || hash
}

const getCheckDescription = (hash) => {
  const check = props.qualityCheckMap.get(hash)
  return check?.description || 'No description available'
}

const getCheckStatusText = (result) => {
  const check = props.qualityCheckMap.get(result.hash)
  return getCheckStatus(result, check)
}

const getCheckBadgeClass = (result) => {
  const status = getCheckStatusText(result)
  return getStatusBadgeClass(status)
}

const getCheckProgressColor = (result) => {
  const check = props.qualityCheckMap.get(result.hash)
  return getProgressColorClass(result, check)
}
</script>

<style scoped>
.font-monospace {
  font-family: 'Courier New', monospace;
}

.circular-progress {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 4px solid transparent;
  overflow: hidden;
}

.circular-progress .progress-value {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 0.75rem;
  font-weight: 500;
  color: #fff;
}
</style>

