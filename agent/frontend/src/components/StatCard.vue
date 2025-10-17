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
  background: white;
  border-radius: 8px;
  padding: 1.25rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 1.75rem;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.813rem;
  color: #6c757d;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
</style>

