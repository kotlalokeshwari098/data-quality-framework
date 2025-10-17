<template>
  <div class="settings-page">
    <PageHeader
      title="Settings"
      mobileTitle="Settings"
      subtitle="Manage your application preferences and configurations"
      icon="bi bi-gear-fill"
    />

    <div class="page-content">
      <HealthStatusBanner/>

      <div class="settings-card">
        <div class="settings-section">
          <div class="section-header">
            <div>
              <h2 class="section-title">
                <i class="bi bi-database"></i>
                FHIR Server
              </h2>
              <p class="section-description">
                Configure your FHIR server connection to access the data
              </p>
            </div>
          </div>

          <form @submit.prevent="saveFhirSettings" class="settings-form">
            <div class="form-group">
              <label for="fhirUrl" class="form-label">
                Server URL
              </label>
              <input
                type="url"
                class="form-control"
                id="fhirUrl"
                v-model="fhirSettings.url"
                placeholder="https://fhir-server.example.com/fhir"
                required
              >
              <small class="form-help">
                The base URL of your FHIR server endpoint
              </small>
            </div>

            <div class="form-group">
              <label for="fhirUsername" class="form-label">
                Username
              </label>
              <input
                type="text"
                class="form-control"
                id="fhirUsername"
                v-model="fhirSettings.username"
                placeholder="Enter username"
                required
                autocomplete="username"
              >
              <small class="form-help">
                Username for authenticating with the FHIR server
              </small>
            </div>

            <div class="form-group">
              <label for="fhirPassword" class="form-label">
                Password
              </label>
              <input
                type="password"
                class="form-control"
                id="fhirPassword"
                v-model="fhirSettings.password"
                placeholder="Enter password"
                required
                autocomplete="current-password"
              >
              <small class="form-help">
                Password for authenticating with the FHIR server
              </small>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="isSaving">
                <i class="bi bi-check-circle me-2"></i>
                {{ isSaving ? 'Saving...' : 'Save Changes' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue';
import settingsStore from '../stores/settingsStore.js';
import PageHeader from '../components/PageHeader.vue';
import HealthStatusBanner from '../components/HealthStatusBanner.vue';
import healthStore from '../stores/healthStore.js';

const isSaving = ref(false);
const notify = inject('notify');

const fhirSettings = reactive({
  url: '',
  username: '',
  password: ''
});

function decodeBase64(encoded) {
  try {
    return atob(encoded);
  } catch (e) {
    console.error('Error decoding Base64:', e);
    return '';
  }
}

function encodeBase64(text) {
  try {
    return btoa(text);
  } catch (e) {
    console.error('Error encoding Base64:', e);
    return '';
  }
}

async function saveFhirSettings() {
  isSaving.value = true;

  try {
    const payload = {
      fhirUrl: fhirSettings.url,
      fhirUsername: fhirSettings.username,
      fhirPassword: encodeBase64(fhirSettings.password)
    };

    await settingsStore.updateSettings(payload);
    notify.success('Settings Saved', 'Your FHIR server settings have been updated successfully');
  } catch (error) {
    console.error('Error saving FHIR settings:', error);
    notify.error('Save Failed', 'Unable to save settings. Please try again.');
  } finally {
    isSaving.value = false;
  }
}

async function loadSettings() {
  try {
    const data = await settingsStore.fetchSettings();
    if (data) {
      fhirSettings.url = data.fhirUrl || '';
      fhirSettings.username = data.fhirUsername || '';
      fhirSettings.password = data.fhirPassword ? decodeBase64(data.fhirPassword) : '';
    }
  } catch (error) {
    console.error('Error loading settings:', error);
  }
}

onMounted(() => {
  loadSettings();
});
</script>

<style scoped>
.settings-page {
  min-height: 100%;
  padding: 2rem;
}

.page-content {
  max-width: 1400px;
  margin: 0 auto;
}

/* Settings Card */
.settings-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  max-width: 900px;
  margin: 0 auto;
}

.settings-section {
  padding: 2.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 0.5rem 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.section-title i {
  color: #667eea;
}

.section-description {
  font-size: 0.95rem;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

/* Form Styles */
.settings-form {
  max-width: 600px;
}

.form-group {
  margin-bottom: 1.75rem;
}

.form-label {
  display: block;
  font-size: 0.95rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.form-control {
  width: 100%;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  transition: all 0.2s ease;
  background: white;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-help {
  display: block;
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: #6b7280;
  line-height: 1.4;
}

/* Form Actions */
.form-actions {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 2px solid #f3f4f6;
}

.btn {
  padding: 0.875rem 2rem;
  font-size: 1rem;
  font-weight: 600;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* Responsive Design */
@media (max-width: 768px) {
  .settings-page {
    padding: 1rem;
  }

  .settings-section {
    padding: 1.5rem;
  }

  .section-title {
    font-size: 1.35rem;
  }

  .settings-form {
    max-width: 100%;
  }
}

@media (max-width: 576px) {
  .settings-page {
    padding: 0.75rem;
  }

  .settings-section {
    padding: 1.25rem;
  }

  .section-header {
    margin-bottom: 1.5rem;
  }

  .section-title {
    font-size: 1.2rem;
    gap: 0.5rem;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
