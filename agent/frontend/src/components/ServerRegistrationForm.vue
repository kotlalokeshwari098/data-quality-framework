<template>
  <div class="server-registration-form">
    <div class="empty-state-header">
      <div class="empty-icon">
        <i class="bi bi-server"></i>
      </div>
      <h2 class="empty-title">No Server Registered</h2>
      <p class="empty-description">
        Register your first central server to start sending quality reports
      </p>
    </div>

    <form @submit.prevent="handleSubmit" class="server-form">
      <div class="form-group">
        <label for="serverName" class="form-label">
          <i class="bi bi-tag"></i>
          Server Name
        </label>
        <input
          type="text"
          class="form-control"
          id="serverName"
          v-model="formData.name"
          placeholder="e.g., Production Central Server"
          required
          maxlength="255"
        >
        <small class="form-help">
          A descriptive name for this server (max 255 characters)
        </small>
      </div>

      <div class="form-group">
        <label for="serverUrl" class="form-label">
          <i class="bi bi-link-45deg"></i>
          Server URL
        </label>
        <input
          type="url"
          class="form-control"
          id="serverUrl"
          v-model="formData.url"
          placeholder="https://central.example.com"
          required
          maxlength="500"
          pattern="^https?://[^\s/$.?#].[^\s]*$"
        >
        <small class="form-help">
          The base URL of the central server (max 500 characters)
        </small>
      </div>

      <div class="form-actions">
        <SaveButton
          type="submit"
          :loading="loading"
          :text="loading ? 'Registering...' : 'Register Server'"
        />
        <CancelButton
          type="button"
          @click="clearForm"
          text="Clear Form"
        />
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import SaveButton from './SaveButton.vue';
import CancelButton from './CancelButton.vue';

defineProps({
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['submit']);

const formData = reactive({
  name: '',
  url: ''
});

function handleSubmit() {
  emit('submit', {
    name: formData.name.trim(),
    url: formData.url.trim()
  });
}

function clearForm() {
  formData.name = '';
  formData.url = '';
}

defineExpose({ clearForm });
</script>

<style scoped>
.server-registration-form {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-2xl);
}

.empty-state-header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);
  padding-bottom: var(--spacing-2xl);
  border-bottom: 2px solid var(--color-gray-100);
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto var(--spacing-lg);
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon i {
  font-size: 2.5rem;
  color: white;
}

.empty-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-gray-800);
  margin: 0 0 var(--spacing-sm) 0;
}

.empty-description {
  font-size: 1rem;
  color: var(--color-gray-500);
  margin: 0;
  line-height: 1.6;
}

.server-form {
  max-width: 600px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 1.75rem;
}

.form-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--color-gray-700);
  margin-bottom: var(--spacing-sm);
}

.form-label i {
  color: var(--color-primary);
}

.form-control {
  width: 100%;
  padding: 0.875rem var(--spacing-md);
  font-size: 1rem;
  border: 2px solid var(--color-gray-200);
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
  background: var(--bg-card);
}

.form-control:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-help {
  display: block;
  margin-top: var(--spacing-sm);
  font-size: 0.875rem;
  color: var(--color-gray-500);
  line-height: 1.4;
}

.form-actions {
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 2px solid var(--color-gray-100);
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
}

@media (max-width: 768px) {
  .server-registration-form {
    padding: var(--spacing-lg);
  }

  .server-form {
    max-width: 100%;
  }

  .form-actions {
    flex-direction: column;
  }

  .empty-icon {
    width: 64px;
    height: 64px;
  }

  .empty-icon i {
    font-size: 2rem;
  }

  .empty-title {
    font-size: 1.5rem;
  }
}
</style>

