<template>
  <div v-if="healthStore.healthStatus"
       :class="['alert', 'mb-4', healthStore.healthStatus.status === 'UP' ? 'alert-success' : 'alert-danger']">
    <div class="d-flex justify-content-between align-items-center">
      <span>
        <i :class="['bi', healthStore.healthStatus.status === 'UP' ? 'bi-check-circle' : 'bi-exclamation-triangle']"></i>
        FHIR Server: {{ healthStore.healthStatus.status }}
        <span v-if="healthStore.healthStatus.details?.error"> - {{ healthStore.healthStatus.details.error }}</span>
      </span>
      <button class="btn btn-sm btn-outline-secondary"
              @click="healthStore.checkHealth()"
              :disabled="healthStore.isChecking">
        <span v-if="healthStore.isChecking" class="spinner-border spinner-border-sm me-1"></span>
        Refresh
      </button>
    </div>
  </div>
</template>

<script setup>
import healthStore from '../stores/healthStore.js'
</script>

<style scoped>
.alert {
  border-radius: 0;
}
</style>
