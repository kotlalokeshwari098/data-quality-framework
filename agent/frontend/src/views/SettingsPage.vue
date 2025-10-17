<template>
  <div class="settings-page">
    <PageHeader
      title="Settings"
      mobileTitle="Settings"
      subtitle="Configure your FHIR server connection"
      icon="bi bi-gear-fill"
    />

    <div class="page-content">
      <div class="settings-container">
        <div class="card shadow-sm">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="card-title mb-0 fw-semibold">
              <i class="bi bi-database me-2"></i>
              FHIR Server
            </h5>
            <button
                v-if="!isEditingFhir"
                class="btn btn-outline-primary btn-sm"
                @click="startEditingFhir"
            >
              <i class="bi bi-pencil me-1"></i>
              Edit
            </button>
          </div>
          <div class="card-body">
            <div v-if="!isEditingFhir">
              <p class="mb-2"><strong>URL:</strong> {{ fhirSettings.url || 'Not configured' }}</p>
              <p class="mb-2"><strong>FHIR Username:</strong> {{ fhirSettings.username || 'Not configured' }}</p>
              <p class="mb-0"><strong>FHIR Password:</strong> {{ fhirSettings.password ? '••••••••' : 'Not configured' }}</p>
            </div>

            <div v-else>
              <form @submit.prevent="saveFhirSettings">
                <div class="mb-3">
                  <label for="fhirUrl" class="form-label fw-semibold">FHIR Server URL</label>
                  <input
                      type="url"
                      class="form-control"
                      id="fhirUrl"
                      v-model="editableFhirSettings.url"
                      placeholder="https://fhir-server.com/fhir"
                      required
                  >
                </div>
                <div class="mb-3">
                  <label for="fhirUsername" class="form-label fw-semibold">FHIR Username</label>
                  <input
                      type="text"
                      class="form-control"
                      id="fhirUsername"
                      v-model="editableFhirSettings.username"
                      placeholder="Enter FHIR username"
                      required
                  >
                </div>
                <div class="mb-3">
                  <label for="fhirPassword" class="form-label fw-semibold">FHIR Password</label>
                  <input
                      type="password"
                      class="form-control"
                      id="fhirPassword"
                      v-model="editableFhirSettings.password"
                      placeholder="Enter FHIR password"
                      required
                  >
                </div>
                <div class="d-flex gap-2">
                  <button type="submit" class="btn btn-primary" :disabled="isSaving">
                    <i class="bi bi-check-lg me-1"></i>
                    {{ isSaving ? 'Saving...' : 'Save' }}
                  </button>
                  <button type="button" class="btn btn-secondary" @click="cancelEditingFhir">
                    <i class="bi bi-x-lg me-1"></i>
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted } from 'vue';
import settingsStore from '../stores/settingsStore.js';
import PageHeader from '../components/PageHeader.vue';

const isEditingFhir = ref(false);
const isSaving = ref(false);
const fhirSettings = reactive({
  url: '',
  username: '',
  password: ''
});

const editableFhirSettings = reactive({
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

function startEditingFhir() {
  editableFhirSettings.url = fhirSettings.url;
  editableFhirSettings.username = fhirSettings.username;
  editableFhirSettings.password = fhirSettings.password ? decodeBase64(fhirSettings.password) : '';
  isEditingFhir.value = true;
}

function cancelEditingFhir() {
  Object.assign(editableFhirSettings, { url: '', username: '', password: '' });
  isEditingFhir.value = false;
}

async function saveFhirSettings() {
  isSaving.value = true;

  try {
    const payload = {
      fhirUrl: editableFhirSettings.url,
      fhirUsername: editableFhirSettings.username,
      fhirPassword: encodeBase64(editableFhirSettings.password)
    };

    await settingsStore.updateSettings(payload);
    fhirSettings.url = editableFhirSettings.url;
    fhirSettings.username = editableFhirSettings.username;
    fhirSettings.password = payload.fhirPassword;
    isEditingFhir.value = false;
  } catch (error) {
    console.error('Error saving FHIR settings:', error);
    alert('Error saving FHIR settings. Please try again.');
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
      fhirSettings.password = data.fhirPassword || '';
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
.settings-page { min-height: 100%; padding: 2rem; }
.page-content { margin-top: 1rem; }
.settings-container { max-width: 800px; margin: 0 auto; }

.card { border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden; }
.card-header { background: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem 1.5rem; }
.card-body { padding: 1.5rem; }

.form-label { color: #374151; font-size: 0.9rem; margin-bottom: 0.5rem; }
.form-control { border: 1px solid #d1d5db; border-radius: 8px; padding: 0.75rem 1rem; font-size: 0.95rem; transition: all 0.2s ease; }
.form-control:focus { border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1); }

.btn-primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 8px; transition: all 0.2s ease; box-shadow: 0 2px 4px rgba(102, 126, 234, 0.2); }
.btn-primary:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3); }
.btn-secondary { border-radius: 8px; }
.btn-outline-primary { border-radius: 8px; color: #667eea; border-color: #667eea; }
.btn-outline-primary:hover { background: #667eea; border-color: #667eea; }

@media (max-width: 768px) { .settings-page { padding: 1rem; } .card-header { padding: 1rem; } .card-body { padding: 1rem; } }
@media (max-width: 576px) { .settings-page { padding: 0.75rem; } }
</style>
