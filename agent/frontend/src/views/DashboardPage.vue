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
      <HealthStatusBanner />

      <!-- Loading Spinner -->
      <div v-if="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="text-muted mt-3">Loading reports...</p>
      </div>

      <template v-else>
        <!-- Statistics Grid -->
        <div class="stats-grid mb-4">
          <StatCard
            :number="totalReports"
            label="Total Reports"
            numberClass="text-primary"
          />
          <StatCard
            :number="successfulChecks"
            label="Passed"
            numberClass="text-success"
          />
          <StatCard
            :number="errorChecks"
            label="Errors"
            numberClass="text-danger"
          />
          <StatCard
            :number="warningChecks"
            label="Warnings"
            numberClass="text-warning"
          />
        </div>

        <!-- Generate Report Section -->
        <div class="card mb-4 border-0 shadow-sm" v-if="latestReport">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">Latest Report</h5>
              <small class="text-muted">Generated: {{ formatDate(latestReport.generatedAt) }}</small>
            </div>
            <GenerateReportButton
              @click="generateReportWithReset"
              :loading="reportStore.isGenerating"
              :disabled="healthStore.healthStatus?.status !== 'UP'"
              text="Generate New Report"
            />
          </div>
        </div>

        <!-- No Reports State -->
        <div v-if="!latestReport" class="text-center py-5">
          <template v-if="reportStore.isGenerating">
            <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
              <span class="visually-hidden">Generating report...</span>
            </div>
            <p class="text-muted mt-3">Generating your first report...</p>
          </template>
          <template v-else>
            <i class="bi bi-clipboard-data display-1 text-muted opacity-50"></i>
            <p class="text-muted mt-3">No reports available.</p>
            <GenerateReportButton
              @click="generateReportWithReset"
              :disabled="healthStore.healthStatus?.status !== 'UP'"
              text="Generate First Report"
            />
          </template>
        </div>

        <!-- Quality Checks Grid -->
        <div v-if="latestReport?.results" class="quality-checks-grid">
          <QualityCheckCard
            v-for="result in latestReport.results"
            :key="getCheckKey(result)"
            :check="result"
            :total-entities="latestReport.numberOfEntities"
            :report-id="latestReport.id"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import HealthStatusBanner from "@/components/HealthStatusBanner.vue";
import PasswordChangeModal from "@/components/PasswordChangeModal.vue";
import PageHeader from "@/components/PageHeader.vue";
import StatCard from "@/components/StatCard.vue";
import QualityCheckCard from "@/components/QualityCheckCard.vue";
import GenerateReportButton from "@/components/GenerateReportButton.vue";
import { useUserStore } from '@/stores/userStore.js';
import healthStore from '@/stores/healthStore.js';
import reportStore from '@/stores/reportStore.js';
import { ref, watch, computed, onMounted } from "vue";

const showPasswordModal = ref(false);
const isLoading = ref(true);

const { initializeDefaultPasswordStatus } = useUserStore();

watch(showPasswordModal, (newValue) => {
  if (!newValue) {
    initializeDefaultPasswordStatus();
  }
});

const closePasswordModal = () => {
  showPasswordModal.value = false;
};

// Load reports on page mount
onMounted(async () => {
  isLoading.value = true;
  try {
    await Promise.all([
      reportStore.fetchReports(),
      healthStore.checkHealth()
    ]);
  } catch (error) {
    console.error('Error loading dashboard data:', error);
  } finally {
    isLoading.value = false;
  }
});

// Computed properties for statistics
const totalReports = computed(() => {
  return reportStore.reports.length;
});

const latestReport = computed(() => {
  if (!reportStore.reports.length) return null;
  return [...reportStore.reports].sort((a, b) => new Date(b.generatedAt) - new Date(a.generatedAt))[0];
});

const calculatePercentage = (result) => {
  const total = latestReport.value?.numberOfEntities || 1;
  return (result.obfuscatedValue / total) * 100;
};

const successfulChecks = computed(() => {
  if (!latestReport.value?.results) return 0;
  return latestReport.value.results.filter(result => {
    const percentage = calculatePercentage(result);
    return percentage < result.warningThreshold && !result.error;
  }).length;
});

const errorChecks = computed(() => {
  if (!latestReport.value?.results) return 0;
  return latestReport.value.results.filter(result => {
    const percentage = calculatePercentage(result);
    return percentage >= result.errorThreshold || result.error;
  }).length;
});

const warningChecks = computed(() => {
  if (!latestReport.value?.results) return 0;
  return latestReport.value.results.filter(result => {
    const percentage = calculatePercentage(result);
    return percentage >= result.warningThreshold && percentage < result.errorThreshold && !result.error;
  }).length;
});

const getCheckKey = (result) => {
  return result.checkId + '_' + (result.stratum || 'all');
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const generateReportWithReset = async () => {
  await reportStore.generateReport();
};
</script>

<style scoped>
.dashboard-page {
  min-height: 100%;
  padding: var(--spacing-xl);
}

.page-content {
  width: 100%;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.quality-checks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-md);
  align-items: start;
}

.quality-checks-grid > * {
  min-height: 100px;
}

@media (min-width: 992px) {
  .quality-checks-grid {
    grid-template-columns: repeat(auto-fill, 320px);
  }

  .quality-checks-grid > * {
    height: 320px;
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: var(--spacing-md);
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-md);
  }
}

@media (max-width: 576px) {
  .dashboard-page {
    padding: 0.75rem;
  }
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
  .quality-checks-grid {
    grid-template-columns: 1fr;
  }
}
</style>
