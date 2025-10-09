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
import { defineProps, ref, reactive, onMounted } from 'vue';
import { updateFhirSettings } from '../js/api.js';

const props = defineProps({
  username: {
    type: String,
    required: true
  }
});

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

function startEditingFhir() {
  Object.assign(editableFhirSettings, fhirSettings);
  isEditingFhir.value = true;
}

function cancelEditingFhir() {
  Object.assign(editableFhirSettings, { url: '', username: '', password: '' });
  isEditingFhir.value = false;
}

async function saveFhirSettings() {
  isSaving.value = true;

  try {
    await updateFhirSettings(editableFhirSettings);
    Object.assign(fhirSettings, editableFhirSettings);
    isEditingFhir.value = false;
    alert('FHIR settings saved successfully!');
  } catch (error) {
    console.error('Error saving FHIR settings:', error);
    alert('Error saving FHIR settings. Please try again.');
  } finally {
    isSaving.value = false;
  }
}

onMounted(() => {
});
</script>

<style scoped>
</style>
