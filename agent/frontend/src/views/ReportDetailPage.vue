<template>
  <div class="report-detail-page">
    <PageHeader
      :title="`Report ${report?.id || ''}`"
      :mobileTitle="`Report ${report?.id || ''}`"
      subtitle="Detailed view of data quality report"
      icon="bi bi-file-earmark-text-fill"
    />

    <div class="page-content">
      <!-- Back button -->
      <div class="mb-3">
        <button class="btn btn-outline-secondary btn-sm" @click="goBack">
          <i class="bi bi-arrow-left me-2"></i>Back to Reports
        </button>
      </div>

      <!-- Loading state -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="text-muted mt-3">Loading report details...</p>
      </div>

      <!-- Error state -->
      <div v-else-if="error" class="alert alert-danger">
        <i class="bi bi-exclamation-triangle me-2"></i>
        {{ error }}
      </div>

      <!-- Report details -->
      <template v-else-if="report">
        <!-- Stats Cards -->
        <div class="stats-grid mb-4">
          <StatCard
            :number="report.numberOfEntities?.toLocaleString() || 'N/A'"
            label="Total Entities"
            number-class="text-primary"
          />
          <StatCard
            :number="report.epsilonBudget?.toFixed(2) || 'N/A'"
            label="Epsilon Budget"
            number-class="text-info"
          />
          <StatCard
            :number="calculateEpsilonUsed().toFixed(2)"
            label="Epsilon Used"
            :number-class="isOverBudget() ? 'text-danger' : 'text-success'"
          />
          <StatCard
            :number="report.results?.length || 0"
            label="Quality Checks"
            number-class="text-secondary"
          />
        </div>

        <!-- Epsilon Warning Alert -->
        <div v-if="isOverBudget()" class="alert alert-warning mb-4">
          <i class="bi bi-exclamation-triangle me-2"></i>
          <strong>Warning:</strong> Epsilon budget exceeded! Total epsilon used ({{ calculateEpsilonUsed().toFixed(2) }}) exceeds budget ({{ report.epsilonBudget.toFixed(2) }})
        </div>

        <!-- Results Section -->
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white border-bottom">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
              <div>
                <h5 class="mb-0">Quality Check Results</h5>
              </div>
              <div class="d-flex align-items-center gap-3">
                <span class="badge rounded-pill" :class="getStatusBadgeClass(report)">
                  {{ report.status }}
                </span>
                <button
                  class="btn btn-sm btn-outline-secondary"
                  @click="toggleValues"
                  :title="showValues ? 'Hide values' : 'Show values'"
                >
                  <i :class="['bi', showValues ? 'bi-eye-slash' : 'bi-eye', 'me-1']"></i>
                  {{ showValues ? 'Hide' : 'Show' }} Values
                </button>
              </div>
            </div>
          </div>
          <div class="card-body">
            <div v-if="!report.results || report.results.length === 0" class="text-center py-4 text-muted">
              <i class="bi bi-inbox fs-1 d-block mb-2 opacity-50"></i>
              <p class="mb-0">No results available</p>
            </div>
            <div v-else class="results-container">
              <div
                v-for="result in report.results"
                :key="getCheckIdKey(result)"
                :class="['result-card', 'card', 'mb-3', getResultClass(result)]"
              >
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start">
                    <div class="flex-grow-1">
                      <h6 class="card-title mb-2">{{ result.checkName }}</h6>
                      <div class="result-details">
                        <div class="detail-row">
                          <span class="detail-label">Epsilon Used:</span>
                          <span class="detail-value">{{ result.epsilon }}</span>
                        </div>
                        <div v-if="showValues" class="detail-row">
                          <span class="detail-label">Raw Value:</span>
                          <span class="detail-value">{{ result.rawValue }}</span>
                        </div>
                        <div class="detail-row">
                          <span class="detail-label">Occurrence Rate:</span>
                          <span class="detail-value">{{ calculatePercentage(result.obfuscatedValue) }}%</span>
                        </div>
                        <div v-if="result.error" class="detail-row">
                          <span class="detail-label text-danger">Error:</span>
                          <span class="detail-value text-danger">{{ result.error }}</span>
                        </div>
                      </div>
                    </div>
                    <button
                      v-if="Array.isArray(result.patients) && result.patients.length > 0"
                      class="btn btn-sm btn-outline-secondary ms-3"
                      @click="toggleIds(getCheckIdKey(result))"
                      :title="openIds[getCheckIdKey(result)] ? 'Hide Patient IDs' : 'Show Patient IDs'"
                    >
                      <i class="bi bi-person-lines-fill"></i>
                    </button>
                  </div>

                  <!-- Patient IDs Section -->
                  <div v-if="Array.isArray(result.patients) && result.patients.length > 0 && openIds[getCheckIdKey(result)]" class="patient-ids-section mt-3">
                    <hr class="my-3">
                    <h6 class="mb-2">
                      <i class="bi bi-people-fill me-2"></i>Patient Identifiers ({{ result.patients.length }})
                    </h6>
                    <div class="table-responsive">
                      <table class="table table-sm table-hover table-borderless text-center mb-0">
                        <tbody>
                          <tr v-for="(row, rowIndex) in patientTableRows(result)" :key="rowIndex">
                            <td v-for="(patient, colIndex) in row" :key="colIndex" class="patient-cell">
                              <a
                                v-if="patient"
                                href="#"
                                @click.prevent="showPatientDetail(patient)"
                                class="patient-link"
                              >
                                {{ patient }}
                              </a>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <nav
                      v-if="result.patients.length > pageSize"
                      class="d-flex justify-content-center mt-3"
                    >
                      <Pagination
                        :current-page="idPage[getCheckIdKey(result)] || 1"
                        :page-size="pageSize"
                        :total-pages="Math.ceil((result.patients?.length || 0) / pageSize)"
                        :max-visible-buttons="5"
                        @page-changed="page => changePage(getCheckIdKey(result), page)"
                      />
                    </nav>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <PatientModal ref="patientModalRef" :patient-id="modalPatientId" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '@/components/PageHeader.vue'
import PatientModal from '@/components/PatientModal.vue'
import Pagination from '@/components/Pagination.vue'
import StatCard from '@/components/StatCard.vue'
import reportStore from '@/stores/reportStore.js'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const error = ref(null)
const report = ref(null)
const showValues = ref(true)
const openIds = ref({})
const pageSize = 60
const idPage = ref({})
const patientModalRef = ref(null)
const modalPatientId = ref('')

const goBack = () => {
  router.push('/reports')
}

const toggleValues = () => {
  showValues.value = !showValues.value
}

function toggleIds(checkId) {
  openIds.value[checkId] = !openIds.value[checkId]
}

function changePage(checkId, page) {
  idPage.value[checkId] = page
}

function paginatedPatients(result) {
  const all = result.patients || []
  const currentPage = idPage.value[getCheckIdKey(result)] || 1
  const start = (currentPage - 1) * pageSize
  return all.slice(start, start + pageSize)
}

function patientTableRows(result) {
  const patients = paginatedPatients(result)
  const rows = []
  for (let i = 0; i < patients.length; i += 6) {
    rows.push(patients.slice(i, i + 6))
  }
  return rows
}

function showPatientDetail(patientId) {
  modalPatientId.value = patientId
  if (patientModalRef.value) {
    patientModalRef.value.open()
  }
}

function getCheckIdKey(result) {
  return result.checkId + '_' + (result.stratum || 'all')
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

const calculatePercentage = (value) => {
  const total = report.value?.numberOfEntities || 1
  return ((value / total) * 100).toFixed(2)
}

const calculateEpsilonUsed = () => {
  if (!report.value?.results) return 0
  return report.value.results.reduce((sum, result) => sum + result.epsilon, 0)
}

const isOverBudget = () => {
  return calculateEpsilonUsed() > report.value.epsilonBudget
}

const getResultClass = (result) => {
  const percentage = parseFloat(calculatePercentage(result.obfuscatedValue))
  if (percentage >= result.errorThreshold || result.error) {
    return 'bg-danger'
  } else if (percentage >= result.warningThreshold) {
    return 'bg-warning'
  }
  return 'bg-success'
}

const getStatusBadgeClass = (report) => {
  switch (report?.status) {
    case 'COMPLETED':
      return 'bg-success'
    case 'GENERATING':
      return 'bg-warning text-dark'
    case 'FAILED':
      return 'bg-danger'
    default:
      return 'bg-secondary'
  }
}

onMounted(async () => {
  try {
    loading.value = true
    const reportId = route.params.id
    report.value = await reportStore.fetchReportById(reportId)
  } catch (err) {
    error.value = `Failed to load report: ${err.message || 'Unknown error'}`
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.report-detail-page {
  min-height: 100%;
  padding: 2rem;
}

.page-content {
  width: 100%;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.results-container {
  display: flex;
  flex-direction: column;
}

.result-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid #dee2e6;
}

.result-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.result-card.bg-success {
  background-color: rgba(25, 135, 84, 0.1) !important;
  border-left: 4px solid #198754;
}

.result-card.bg-warning {
  background-color: rgba(255, 193, 7, 0.1) !important;
  border-left: 4px solid #ffc107;
}

.result-card.bg-danger {
  background-color: rgba(220, 53, 69, 0.1) !important;
  border-left: 4px solid #dc3545;
}

.result-details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.detail-row {
  display: flex;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.detail-label {
  font-weight: 600;
  color: #6c757d;
  min-width: 120px;
}

.detail-value {
  color: #212529;
}

.patient-ids-section {
  background-color: rgba(0, 0, 0, 0.02);
  padding: 1rem;
  border-radius: 0.375rem;
}

.patient-cell {
  padding: 0.5rem;
  width: 16.66%;
}

.patient-link {
  text-decoration: none;
  color: #0d6efd;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  transition: background-color 0.2s ease;
  display: inline-block;
}

.patient-link:hover {
  background-color: rgba(13, 110, 253, 0.1);
  text-decoration: underline;
}

.badge {
  font-weight: 500;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .report-detail-page {
    padding: 1rem;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .detail-label {
    min-width: 100px;
    font-size: 0.85rem;
  }

  .detail-value {
    font-size: 0.85rem;
  }
}

@media (max-width: 576px) {
  .report-detail-page {
    padding: 0.75rem;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .patient-cell {
    width: 33.33%;
  }
}
</style>

