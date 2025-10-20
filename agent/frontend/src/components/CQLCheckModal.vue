<template>
  <BaseModal
    :show="show"
    :title="isEditing ? 'Edit Quality Check' : 'Add Quality Check'"
    :subtitle="isEditing ? 'Update check configuration' : 'Create a new CQL quality check'"
    icon="bi bi-check2-square fs-4"
    size="lg"
    variant="primary"
    :loading="saving"
    :save-disabled="!isFormValid"
    :save-button-props="{ text: 'Save Changes' }"
    @close="handleClose"
    @save="handleSave"
  >
    <div class="form-sections">
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
  </BaseModal>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import BaseModal from './BaseModal.vue';

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

const emit = defineEmits([
  'close',
  'save',
  'save-success',
  'save-error'
]);

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

const isEditing = computed(() => !!formData.value.id);

const isFormValid = computed(() => {
  return formData.value.name?.trim() && formData.value.query?.trim();
});

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
</script>

<style scoped>
.form-sections {
  background: linear-gradient(to bottom, #f8f9fa 0%, #ffffff 100%);
  min-height: 100%;
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

.invalid-feedback {
  display: block;
  width: 100%;
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: #dc3545;
}

.is-invalid {
  border-color: #dc3545;
  box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .form-section {
    padding: 1rem;
  }
}
</style>
