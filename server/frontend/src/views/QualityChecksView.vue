<template>
  <div class="container-fluid px-2 px-md-3 py-3 py-md-4">
    <div class="row">
      <div class="col-12">
        <!-- Page Header -->
        <PageHeader
          title="Quality Checks"
          mobile-title="Checks"
          subtitle="View and manage data quality check definitions"
          icon="bi bi-clipboard-check-fill"
        >
          <template #actions>
            <button
              @click="refreshChecks"
              class="btn btn-outline-primary btn-sm"
              :disabled="loading"
            >
              <i class="bi bi-arrow-clockwise"></i>
              <span class="d-none d-md-inline ms-1">Refresh</span>
            </button>
          </template>
        </PageHeader>

        <!-- Stats Cards -->
        <div class="stats-grid mb-3 mb-md-4">
          <div class="stat-card">
            <div class="stat-number text-dark">{{ qualityChecks.length }}</div>
            <div class="stat-label">Total Checks</div>
          </div>
          <div class="stat-card">
            <div class="stat-number text-primary">{{ checksWithWarning }}</div>
            <div class="stat-label">Warning Threshold</div>
          </div>
          <div class="stat-card">
            <div class="stat-number text-danger">{{ checksWithError }}</div>
            <div class="stat-label">Error Threshold</div>
          </div>
        </div>

        <!-- Filters -->
        <div class="filters-card mb-3 mb-md-4">
          <div class="filters-content">
            <div class="search-filter">
              <input
                v-model="searchQuery"
                type="text"
                class="form-control"
                placeholder="Search quality checks..."
              >
            </div>
            <div class="results-count">
              <span class="text-muted small">{{ filteredChecks.length }} checks</span>
            </div>
          </div>
        </div>

        <!-- Loading state -->
        <div v-if="loading" class="loading-state">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading quality checks...</span>
          </div>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="alert alert-danger" role="alert">
          <h6 class="alert-heading">Error Loading Quality Checks</h6>
          <p class="mb-0">{{ error }}</p>
        </div>

        <!-- Empty state -->
        <div v-else-if="filteredChecks.length === 0" class="empty-state">
          <div class="empty-state-icon">
            <i class="bi bi-clipboard-check"></i>
          </div>
          <h5 class="empty-state-title">No Quality Checks Found</h5>
          <p class="empty-state-text">
            {{ searchQuery ? 'Try adjusting your search criteria' : 'No quality checks are configured yet' }}
          </p>
        </div>

        <!-- Quality Checks Table -->
        <div v-else class="card border-0 shadow-sm">
          <div class="card-header bg-white border-bottom py-3">
            <div class="d-flex justify-content-between align-items-center">
              <h5 class="mb-0 fw-semibold">Quality Check Definitions</h5>
              <span class="badge bg-secondary">{{ filteredChecks.length }} checks</span>
            </div>
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                  <tr>
                    <th class="ps-4 d-none d-lg-table-cell">Hash</th>
                    <th>Name</th>
                    <th class="d-none d-md-table-cell">Description</th>
                    <th class="text-center d-none d-lg-table-cell">Warning Threshold</th>
                    <th class="text-center d-none d-lg-table-cell">Error Threshold</th>
                    <th class="d-none d-xl-table-cell">Registered At</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="check in filteredChecks" :key="check.hash || check.name" class="table-row-hover">
                    <td class="ps-4 d-none d-lg-table-cell">
                      <code class="font-monospace small text-muted hash-code">{{ check.hash }}</code>
                    </td>
                    <td>
                      <div
                        v-if="editingCheck !== check.hash"
                        class="editable-field"
                        @click="startEdit(check)"
                        title="Click to edit"
                      >
                        {{ check.name }}
                        <i class="bi bi-pencil-fill edit-icon"></i>
                      </div>
                      <div v-else>
                        <input
                          v-model="editForm.name"
                          type="text"
                          class="form-control form-control-sm"
                          @keyup.enter="saveCheck(check)"
                          ref="nameInput"
                        >
                        <small class="text-muted d-block mt-1">Press Enter to save</small>
                      </div>
                      <div class="d-md-none small text-muted mt-1">{{ check.description }}</div>
                    </td>
                    <td class="d-none d-md-table-cell">
                      <div
                        v-if="editingCheck !== check.hash"
                        class="editable-field text-muted small"
                        @click="startEdit(check)"
                        title="Click to edit"
                      >
                        {{ check.description || 'No description' }}
                        <i class="bi bi-pencil-fill edit-icon"></i>
                      </div>
                      <div v-else>
                        <textarea
                          v-model="editForm.description"
                          class="form-control form-control-sm"
                          rows="2"
                          @keyup.enter="saveCheck(check)"
                        ></textarea>
                        <small class="text-muted d-block mt-1">Press Enter to save</small>
                      </div>
                    </td>
                    <td class="text-center d-none d-lg-table-cell">
                      <div
                        v-if="editingCheck !== check.hash"
                        class="editable-field"
                        @click="startEdit(check)"
                        title="Click to edit"
                      >
                        <span class="badge bg-warning-subtle text-warning-emphasis">
                          {{ formatThreshold(check.warningThreshold) }}
                        </span>
                        <i class="bi bi-pencil-fill edit-icon"></i>
                      </div>
                      <div v-else>
                        <input
                          v-model.number="editForm.warningThreshold"
                          type="number"
                          step="0.01"
                          min="0"
                          max="1"
                          class="form-control form-control-sm"
                          @keyup.enter="saveCheck(check)"
                        >
                        <small class="text-muted d-block mt-1">Press Enter to save</small>
                      </div>
                    </td>
                    <td class="text-center d-none d-lg-table-cell">
                      <div
                        v-if="editingCheck !== check.hash"
                        class="editable-field"
                        @click="startEdit(check)"
                        title="Click to edit"
                      >
                        <span class="badge bg-danger-subtle text-danger-emphasis">
                          {{ formatThreshold(check.errorThreshold) }}
                        </span>
                        <i class="bi bi-pencil-fill edit-icon"></i>
                      </div>
                      <div v-else>
                        <input
                          v-model.number="editForm.errorThreshold"
                          type="number"
                          step="0.01"
                          min="0"
                          max="1"
                          class="form-control form-control-sm"
                          @keyup.enter="saveCheck(check)"
                        >
                        <small class="text-muted d-block mt-1">Press Enter to save</small>
                      </div>
                    </td>
                    <td class="d-none d-xl-table-cell">
                      <span class="text-muted small">{{ formatDate(check.registeredAt) }}</span>
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { apiService } from '../services/apiService.js'
import PageHeader from '../components/PageHeader.vue'

const qualityChecks = ref([])
const loading = ref(false)
const saving = ref(false)
const error = ref(null)
const searchQuery = ref('')
const editingCheck = ref(null)
const editForm = ref({
  name: '',
  description: '',
  warningThreshold: 0,
  errorThreshold: 0
})

const checksWithWarning = computed(() => {
  return qualityChecks.value.filter(check => check.warningThreshold > 0).length
})

const checksWithError = computed(() => {
  return qualityChecks.value.filter(check => check.errorThreshold > 0).length
})

const filteredChecks = computed(() => {
  if (!searchQuery.value) {
    return qualityChecks.value
  }

  const query = searchQuery.value.toLowerCase()
  return qualityChecks.value.filter(check =>
    check.name?.toLowerCase().includes(query) ||
    check.description?.toLowerCase().includes(query) ||
    check.hash?.toLowerCase().includes(query)
  )
})

const formatThreshold = (value) => {
  return value !== undefined && value !== null ? value : 'N/A'
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' }
  return new Date(dateString).toLocaleDateString(undefined, options)
}

const loadQualityChecks = async () => {
  loading.value = true
  error.value = null

  try {
    const data = await apiService.getQualityChecks()
    // Handle HAL format response
    if (data._embedded && data._embedded.qualityChecks) {
      qualityChecks.value = data._embedded.qualityChecks
    } else if (Array.isArray(data)) {
      qualityChecks.value = data
    } else {
      qualityChecks.value = []
    }
  } catch (err) {
    error.value = err.message || 'Failed to load quality checks'
    console.error('Error loading quality checks:', err)
  } finally {
    loading.value = false
  }
}

const refreshChecks = () => {
  loadQualityChecks()
}

const startEdit = (check) => {
  editingCheck.value = check.hash
  editForm.value = {
    name: check.name,
    description: check.description,
    warningThreshold: check.warningThreshold,
    errorThreshold: check.errorThreshold
  }
}

const cancelEdit = () => {
  editingCheck.value = null
  editForm.value = {
    name: '',
    description: '',
    warningThreshold: 0,
    errorThreshold: 0
  }
}

const saveCheck = async (check) => {
  saving.value = true

  try {
    await apiService.updateQualityCheck(check.hash, editForm.value)

    // Update the local data
    const index = qualityChecks.value.findIndex(c => c.hash === check.hash)
    if (index !== -1) {
      qualityChecks.value[index] = {
        ...qualityChecks.value[index],
        ...editForm.value
      }
    }

    cancelEdit()
  } catch (err) {
    error.value = err.message || 'Failed to update quality check'
    console.error('Error updating quality check:', err)
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadQualityChecks()
})
</script>

<style scoped>
/* Page Header */
.page-header {
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f0f0;
}

.page-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.page-subtitle {
  color: #6c757d;
  font-size: 0.95rem;
  margin: 0;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.stat-card {
  background: white;
  padding: 1.25rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.875rem;
  color: #6c757d;
  font-weight: 500;
}

/* Filters */
.filters-card {
  background: white;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.filters-content {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
}

.search-filter {
  flex: 1;
  min-width: 200px;
}

.results-count {
  margin-left: auto;
}

/* Loading State */
.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 4rem 0;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-state-icon {
  font-size: 4rem;
  color: #e0e0e0;
  margin-bottom: 1rem;
}

.empty-state-title {
  color: #2c3e50;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.empty-state-text {
  color: #6c757d;
  margin-bottom: 0;
}

/* Table Styling */
.card {
  border-radius: 12px;
  overflow: hidden;
}

.table {
  font-size: 0.875rem;
}

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
  font-size: 0.875rem;
}

.table-responsive {
  overflow-x: visible;
}

.hash-code {
  max-width: 150px;
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-row-hover {
  transition: all 0.2s ease-in-out;
  cursor: pointer;
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

.badge {
  font-weight: 500;
  padding: 0.35rem 0.65rem;
  font-size: 0.75rem;
  white-space: nowrap;
}

/* Editable Fields */
.editable-field {
  position: relative;
  cursor: pointer;
  padding-right: 1.5rem;
  transition: background-color 0.2s ease;
  border-radius: 4px;
  display: inline-block;
  min-width: 100px;
}

.editable-field:hover {
  background-color: rgba(0, 123, 255, 0.05);
}

.editable-field:hover .edit-icon {
  opacity: 0.6;
}

.edit-icon {
  font-size: 0.7rem;
  color: #6c757d;
  opacity: 0.3;
  margin-left: 0.5rem;
  transition: opacity 0.2s ease;
  vertical-align: middle;
}

/* Responsive */
@media (max-width: 992px) {
  .table th,
  .table td {
    padding: 0.75rem 0.5rem;
  }

  .hash-code {
    max-width: 120px;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 1.5rem;
  }

  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .stat-number {
    font-size: 1.5rem;
  }

  .stat-label {
    font-size: 0.75rem;
  }

  .filters-content {
    flex-direction: column;
    align-items: stretch;
  }

  .results-count {
    margin-left: 0;
    text-align: center;
  }

  .table {
    font-size: 0.75rem;
  }

  .table th,
  .table td {
    padding: 0.5rem 0.35rem;
  }

  .hash-code {
    max-width: 100px;
  }

  .badge {
    font-size: 0.65rem;
    padding: 0.25rem 0.45rem;
  }
}

@media (max-width: 576px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 0.5rem;
  }

  .stat-card {
    padding: 0.875rem 0.5rem;
  }

  .container-fluid {
    padding-left: 0.75rem;
    padding-right: 0.75rem;
  }

  .table-responsive {
    overflow-x: auto;
  }
}
</style>
