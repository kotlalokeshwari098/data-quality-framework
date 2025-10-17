<template>
  <div class="quality-checks-page">
    <PageHeader
      title="Quality Checks"
      mobileTitle="Checks"
      subtitle="View and manage CQL quality checks for your data"
      icon="bi bi-check2-square"
    />

    <div class="page-content">
      <CQLCheckTable />
    </div>
  </div>
</template>

<script setup>
import CQLCheckTable from "@/components/CQLCheckTable.vue";
import PageHeader from "@/components/PageHeader.vue";
</script>

<style scoped>
.quality-checks-page { min-height: 100%; padding: 2rem; }
.page-content { max-width: 1400px; margin: 0 auto; }
@media (max-width: 768px) { .quality-checks-page { padding: 1rem; } }
@media (max-width: 576px) { .quality-checks-page { padding: 0.75rem; } }
</style>

