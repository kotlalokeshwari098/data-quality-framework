<template>
  <!-- Edit/Create Modal -->
  <div
    v-if="show"
    class="modal fade show d-block modal-backdrop-custom"
    tabindex="-1"
    @click.self="handleClose"
  >
    <div class="modal-dialog modal-lg modal-dialog-scrollable modal-dialog-centered">
      <div class="modal-content border-0 shadow-lg">
        <div class="modal-header bg-gradient-purple border-0 text-white py-3">
          <div class="d-flex align-items-center">
            <div class="icon-wrapper me-3">
              <i class="bi bi-check2-square fs-4"></i>
            </div>
            <div>
              <h5 class="modal-title mb-0">{{ isEditing ? 'Edit Quality Check' : 'Add Quality Check' }}</h5>
              <small class="opacity-90">{{ isEditing ? 'Update check configuration' : 'Create a new CQL quality check' }}</small>
            </div>
          </div>
          <button
            type="button"
            class="btn-close-custom"
            @click="handleClose"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body p-4">
          <div class="form-section mb-4">
            <h6 class="section-title mb-3">
              <i class="bi bi-info-circle me-2"></i>
              Basic Information
            </h6>
            <div class="mb-3">
              <label for="checkName" class="form-label fw-semibold">
                Name <span class="text-danger">*</span>
              </label>
              <input
                v-model="formData.name"
                class="form-control form-control-lg"
                id="checkName"
                placeholder="Enter check name"
                :class="{ 'is-invalid': errors.name }"
              />
              <div v-if="errors.name" class="invalid-feedback">
                {{ errors.name }}
              </div>
            </div>
            <div class="mb-3">
              <label for="checkDescription" class="form-label fw-semibold">Description</label>
              <textarea
                v-model="formData.description"
                class="form-control"
                id="checkDescription"
                rows="2"
                placeholder="Enter a brief description of what this check validates"
              ></textarea>
              <small class="form-text text-muted">Optional: Helps others understand the purpose of this check</small>
            </div>
          </div>

          <div class="form-section mb-4">
            <h6 class="section-title mb-3">
              <i class="bi bi-code-square me-2"></i>
              Query Configuration
            </h6>
            <div class="mb-3">
              <label for="checkQuery" class="form-label fw-semibold">
                CQL Query <span class="text-danger">*</span>
              </label>
              <textarea
                v-model="formData.query"
                class="form-control font-monospace query-input"
                id="checkQuery"
                rows="8"
                placeholder="Enter your CQL query here..."
                :class="{ 'is-invalid': errors.query }"
              ></textarea>
              <div v-if="errors.query" class="invalid-feedback">
                {{ errors.query }}
              </div>
              <small class="form-text text-muted">
                <i class="bi bi-lightbulb me-1"></i>
                Write a CQL query to validate data quality
              </small>
            </div>
          </div>

          <div class="form-section">
            <h6 class="section-title mb-3">
              <i class="bi bi-sliders me-2"></i>
              Thresholds & Budget
            </h6>
            <div class="row g-3">
              <div class="col-md-4">
                <label for="checkWarningThreshold" class="form-label fw-semibold">
                  <i class="bi bi-exclamation-triangle text-warning me-1"></i>
                  Warning Threshold
                </label>
                <input
                  v-model.number="formData.warningThreshold"
                  type="number"
                  class="form-control"
                  id="checkWarningThreshold"
                  placeholder="10"
                />
                <small class="form-text text-muted">Trigger warning at this value</small>
              </div>
              <div class="col-md-4">
                <label for="checkErrorThreshold" class="form-label fw-semibold">
                  <i class="bi bi-x-circle text-danger me-1"></i>
                  Error Threshold
                </label>
                <input
                  v-model.number="formData.errorThreshold"
                  type="number"
                  class="form-control"
                  id="checkErrorThreshold"
                  placeholder="30"
                />
                <small class="form-text text-muted">Trigger error at this value</small>
              </div>
              <div class="col-md-4">
                <label for="checkEpsilonBudget" class="form-label fw-semibold">
                  <i class="bi bi-shield-check text-info me-1"></i>
                  Epsilon Budget
                </label>
                <input
                  v-model.number="formData.epsilonBudget"
                  type="number"
                  step="0.1"
                  class="form-control"
                  id="checkEpsilonBudget"
                  placeholder="1.0"
                />
                <small class="form-text text-muted">Privacy budget allocation</small>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer bg-light border-0 py-3 d-flex justify-content-center">
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-secondary" @click="handleClose">
              <i class="bi bi-x-circle me-2"></i>
              Cancel
            </button>
            <button
              type="button"
              class="btn btn-primary-gradient"
              @click="handleSave"
              :disabled="saving"
            >
              <span v-if="saving" class="spinner-border spinner-border-sm me-2" role="status"></span>
              <i v-else class="bi bi-check-circle me-2"></i>
              Save Changes
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Delete Confirmation Modal -->
  <div
    v-if="showDeleteModal"
    class="modal fade show d-block modal-backdrop-custom"
    tabindex="-1"
    @click.self="cancelDelete"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content border-0 shadow-lg">
        <div class="modal-header bg-danger text-white border-0 py-3">
          <div class="d-flex align-items-center">
            <div class="icon-wrapper-small me-3">
              <i class="bi bi-exclamation-triangle fs-4"></i>
            </div>
            <h5 class="modal-title mb-0">Confirm Deletion</h5>
          </div>
          <button
            type="button"
            class="btn-close-custom"
            @click="cancelDelete"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body p-4">
          <p class="mb-2 fw-semibold">Are you sure you want to delete this quality check?</p>
          <p class="mb-0 text-muted small">This action cannot be undone and will permanently remove the check configuration.</p>
        </div>
        <div class="modal-footer bg-light border-0 py-3">
          <button type="button" class="btn btn-secondary" @click="cancelDelete">
            <i class="bi bi-x-circle me-2"></i>
            Cancel
          </button>
          <button
            type="button"
            class="btn btn-danger"
            @click="confirmDelete"
            :disabled="deleting"
          >
            <span v-if="deleting" class="spinner-border spinner-border-sm me-2" role="status"></span>
            <i v-else class="bi bi-trash me-2"></i>
            Delete Permanently
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, inject } from 'vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  check: {
    type: Object,
    default: null
  },
  saving: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close', 'save', 'delete']);

// Inject notification system
const notify = inject('notify')

const formData = ref({
  id: null,
  name: '',
  description: '',
  query: '',
  warningThreshold: 10,
  errorThreshold: 30,
  epsilonBudget: 1.0
});

const errors = ref({});
const showDeleteModal = ref(false);
const deleting = ref(false);

const isEditing = computed(() => !!formData.value.id);

// Watch for changes to the check prop and update form data
watch(() => props.check, (newCheck) => {
  if (newCheck) {
    formData.value = { ...newCheck };
  } else {
    // Reset form for new check
    formData.value = {
      id: null,
      name: '',
      description: '',
      query: '',
      warningThreshold: 10,
      errorThreshold: 30,
      epsilonBudget: 1.0
    };
  }
  errors.value = {};
}, { immediate: true });

const validateForm = () => {
  const validationErrors = {};

  if (!formData.value.name?.trim()) {
    validationErrors.name = 'Name is required';
  }

  if (!formData.value.query?.trim()) {
    validationErrors.query = 'CQL query is required';
  }

  errors.value = validationErrors;
  return Object.keys(validationErrors).length === 0;
};

const handleClose = () => {
  errors.value = {};
  emit('close');
};

const handleSave = () => {
  if (!validateForm()) {
    return;
  }
  emit('save', { ...formData.value });
};

const cancelDelete = () => {
  showDeleteModal.value = false;
};

const confirmDelete = () => {
  deleting.value = true;
  emit('delete', formData.value.id);
  showDeleteModal.value = false;
  // Reset deleting state after a short delay
  setTimeout(() => {
    deleting.value = false;
  }, 500);
};

// Emit events for save success and error instead of exposing methods
// Example usage: emit('save-success', { message: '...' }); emit('save-error', { errorMessage: '...' });
</script>

<style scoped>
/* Modal Backdrop */
.modal-backdrop-custom {
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(2px);
}

.modal-dialog-centered {
  display: flex;
  align-items: center;
  min-height: calc(100% - 3.5rem);
}

.modal-content {
  border-radius: 0.75rem;
  overflow: hidden;
}

/* Header Gradient */
.bg-gradient-purple {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* Custom Close Button */
.btn-close-custom {
  background: transparent;
  border: none;
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.375rem;
  transition: all 0.2s ease;
  position: relative;
  opacity: 1;
  cursor: pointer;
  margin-left: auto;
  flex-shrink: 0;
}

.btn-close-custom::before,
.btn-close-custom::after {
  content: '';
  position: absolute;
  width: 18px;
  height: 2px;
  background-color: white;
  border-radius: 1px;
  transition: all 0.2s ease;
}

.btn-close-custom::before {
  transform: rotate(45deg);
}

.btn-close-custom::after {
  transform: rotate(-45deg);
}

.btn-close-custom:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.btn-close-custom:hover::before,
.btn-close-custom:hover::after {
  background-color: white;
  width: 20px;
}

.btn-close-custom:active {
  transform: scale(0.95);
}

.btn-close-custom:focus {
  outline: 2px solid rgba(255, 255, 255, 0.5);
  outline-offset: 2px;
}

/* Icon Wrapper */
.icon-wrapper {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper-small {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Modal Body */
.modal-body {
  max-height: calc(100vh - 250px);
  overflow-y: auto;
  background: linear-gradient(to bottom, #f8f9fa 0%, #ffffff 100%);
}

/* Form Sections */
.form-section {
  background: white;
  padding: 1.5rem;
  border-radius: 0.5rem;
  border: 1px solid #e9ecef;
}

.section-title {
  color: #495057;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #e9ecef;
}

/* Form Controls */
.form-label {
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
  color: #495057;
}

.form-control {
  font-size: 0.875rem;
  border: 1px solid #dee2e6;
  transition: all 0.2s ease;
}

.form-control:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
}

.form-control-lg {
  font-size: 1rem;
  padding: 0.75rem 1rem;
}

.form-text {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.813rem;
}

.query-input {
  background-color: #f8f9fa;
  line-height: 1.6;
}

.font-monospace {
  font-family: 'SF Mono', 'Monaco', 'Courier New', monospace;
}

/* Buttons */
.btn-primary-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 0.375rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-primary-gradient:hover:not(:disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  color: white;
}

.btn-primary-gradient:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(102, 126, 234, 0.3);
}

.btn-primary-gradient:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #6c757d;
  border-color: #6c757d;
  color: white;
  padding: 0.5rem 1.25rem;
  border-radius: 0.375rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-secondary:hover {
  background: #5c636a;
  border-color: #565e64;
  transform: translateY(-1px);
}

.btn-danger {
  padding: 0.5rem 1.25rem;
  border-radius: 0.375rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-danger:hover {
  transform: translateY(-1px);
}

/* Scrollbar styling */
.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.modal-body::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 4px;
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .modal-dialog {
    margin: 0.5rem;
  }

  .form-section {
    padding: 1rem;
  }

  .icon-wrapper {
    width: 40px;
    height: 40px;
  }

  .modal-body {
    padding: 1rem !important;
  }
}
</style>
