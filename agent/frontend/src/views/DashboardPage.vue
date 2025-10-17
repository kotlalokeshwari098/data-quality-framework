<template>
  <div class="dashboard-page">
    <PasswordChangeModal :isVisible="showPasswordModal" @close="closePasswordModal" />

    <PageHeader
      title="Dashboard"
      mobileTitle="Dashboard"
      subtitle="Monitor and validate your local repository data quality"
      icon="bi bi-speedometer2"
    />

    <div class="page-content">
      <DataQualityReports />
    </div>
  </div>
</template>

<script setup>
import DataQualityReports from "@/components/DataQualityReports.vue";
import PasswordChangeModal from "@/components/PasswordChangeModal.vue";
import PageHeader from "@/components/PageHeader.vue";
import { useUserStore } from '@/stores/userStore.js';
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
.dashboard-page { min-height: 100%; padding: 2rem; }
.page-content { max-width: 1400px; margin: 0 auto; }
@media (max-width: 768px) { .dashboard-page { padding: 1rem; } }
@media (max-width: 576px) { .dashboard-page { padding: 0.75rem; } }
</style>
