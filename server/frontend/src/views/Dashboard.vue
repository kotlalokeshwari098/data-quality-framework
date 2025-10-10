<template>
  <div class="container-fluid py-3 py-md-4">
    <!-- Agents Section - Top Left -->
    <div class="row">
      <div class="col-12 col-md-6 col-lg-4 mb-4">
        <AgentsList />
      </div>
    </div>
  </div>
</template>

<script setup>
import AgentsList from '../components/AgentsList.vue'
</script>

<style scoped>
@media (max-width: 576px) {
  .container-fluid {
    padding-left: 0.75rem;
    padding-right: 0.75rem;
  }
}
</style>
