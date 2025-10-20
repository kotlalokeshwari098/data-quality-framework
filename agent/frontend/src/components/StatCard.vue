<template>
  <div class="stat-card">
    <div class="stat-number" :class="numberClass">{{ number }}</div>
    <div class="stat-label">{{ label }}</div>
  </div>
</template>

<script setup>
defineProps({
  number: {
    type: [String, Number],
    required: true
  },
  label: {
    type: String,
    required: true
  },
  numberClass: {
    type: String,
    default: 'text-dark'
  }
})
</script>

<style scoped>
.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 1.25rem;
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition-base), box-shadow var(--transition-base);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-number {
  font-size: 1.75rem;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.813rem;
  color: var(--color-gray-500);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
</style>
