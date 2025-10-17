<template>
  <div class="data-quality-page">
    <PasswordChangeModal :isVisible="showPasswordModal" @close="closePasswordModal" />

    <PageHeader
      title="Data Quality Agent"
      mobileTitle="Quality"
      subtitle="Monitor and validate your local repository data quality"
      icon="bi bi-bar-chart-fill"
    />

    <div class="page-content">
      <DataQualityReports />
      <CQLCheckTable />
    </div>
  </div>
</template>

<script setup>
import CQLCheckTable from "../CQLCheckTable.vue";
import DataQualityReports from "../DataQualityReports.vue";
import PasswordChangeModal from "../PasswordChangeModal.vue";
import PageHeader from "../PageHeader.vue";
import { useUserStore } from '../../stores/userStore.js';
import { ref, watch } from "vue";

const showPasswordModal = ref(false);

const { initializeDefaultPasswordStatus } = useUserStore();

watch(showPasswordModal, (newValue) => {
  if (!newValue) {
    initializeDefaultPasswordStatus();
  }
});

const closePasswordModal = () => {
  showPasswordModal.value = false;
};
</script>

<style scoped>
.data-quality-page { min-height: 100%; padding: 2rem; }
.page-content { max-width: 1400px; margin: 0 auto; }
@media (max-width: 768px) { .data-quality-page { padding: 1rem; } }
@media (max-width: 576px) { .data-quality-page { padding: 0.75rem; } }
</style>
