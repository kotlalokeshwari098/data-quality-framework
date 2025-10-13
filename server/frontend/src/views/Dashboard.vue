<template>
  <div class="container-fluid py-3 py-md-4">
    <!-- Dashboard Header -->
    <PageHeader
      title="Site Performance Overview"
      subtitle="Monitor data quality across all locations"
      icon="bi bi-grid-3x3-gap-fill"
      :hide-subtitle-on-mobile="false"
    />

    <!-- Stats Cards Section -->
    <div class="row mb-4">
      <div class="col-12 col-sm-6 col-md-6 col-lg-3 mb-3 mb-lg-0">
        <StatsCard
          label="Sites Monitored"
          :value="`${activeAgentsCount}`"
          icon="bi bi-geo-alt-fill"
          iconColor="#0d6efd"
          iconBgColor="#cfe2ff"
          :trendText="`${activeAgentsCount} agents added this month`"
          trendType="positive"
        />
      </div>
    </div>

    <!-- Agents Section - Top Left -->
    <div class="row">
      <div class="col-12 col-sm-6 col-md-4 col-lg-2 mb-4">
        <AgentsList @agentsLoaded="handleAgentsLoaded" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AgentsList from '../components/AgentsList.vue'
import StatsCard from '../components/StatsCard.vue'
import PageHeader from '../components/PageHeader.vue'

const activeAgentsCount = ref(0)

const handleAgentsLoaded = (count) => {
  activeAgentsCount.value = count
}
</script>

<style scoped>
.dashboard-header {
  padding-bottom: 1rem;
}

.dashboard-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #212529;
}

.dashboard-subtitle {
  font-size: 1rem;
  color: #6c757d;
}

@media (min-width: 768px) {
  .dashboard-title {
    font-size: 2rem;
  }

  .dashboard-subtitle {
    font-size: 1.125rem;
  }
}

@media (max-width: 576px) {
  .container-fluid {
    padding-left: 0.75rem;
    padding-right: 0.75rem;
  }

  .dashboard-title {
    font-size: 1.5rem;
  }

  .dashboard-subtitle {
    font-size: 0.9rem;
  }
}
</style>
