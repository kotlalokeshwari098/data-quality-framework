<template>
  <div class="flex-grow-1 d-flex flex-column">
    <header class="text-center py-3 bg-light">
      <h1 class="h1 fw-bold m-0">Settings</h1>
    </header>

    <main class="flex-grow-1 p-4 bg-light">
      <div class="container" style="max-width: 800px;">
        <div class="card shadow-sm">
          <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <h5 class="card-title mb-0 fw-semibold">FHIR Server</h5>
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
                  <label for="fhirUrl" class="form-label">FHIR Server URL</label>
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
                  <label for="fhirUsername" class="form-label">FHIR Username</label>
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
                  <label for="fhirPassword" class="form-label">FHIR Password</label>
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
    </main>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted } from 'vue';
import settingsStore from '../stores/settingsStore.js';

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
</style>
