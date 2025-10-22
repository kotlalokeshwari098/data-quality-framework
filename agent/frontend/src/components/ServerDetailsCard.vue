<template>
  <div class="server-details-card">
    <div class="server-info-grid">
      <div class="info-item">
        <div class="info-label">
          <i class="bi bi-tag"></i>
          Server Name
        </div>
        <div class="info-value">{{ server?.name || 'N/A' }}</div>
      </div>

      <div class="info-item">
        <div class="info-label">
          <i class="bi bi-link-45deg"></i>
          Server URL
        </div>
        <div class="info-value">
          <a v-if="server?.url" :href="server.url" target="_blank" rel="noopener noreferrer" class="server-link">
            {{ server.url }}
            <i class="bi bi-box-arrow-up-right"></i>
          </a>
          <span v-else>N/A</span>
        </div>
      </div>

      <div class="info-item">
        <div class="info-label">
          <i class="bi bi-activity"></i>
          Status
        </div>
        <div class="info-value">
          <span :class="['status-badge', getStatusClass(server?.status)]">
            <i :class="getStatusIcon(server?.status)"></i>
            {{ formatStatus(server?.status) }}
          </span>
        </div>
      </div>

      <div class="info-item">
        <div class="info-label">
          <i class="bi bi-key"></i>
          Server ID
        </div>
        <div class="info-value server-id">{{ server?.id || 'N/A' }}</div>
      </div>
    </div>

    <div class="server-actions-bar">
      <button class="btn btn-edit" @click="handleEdit">
        <i class="bi bi-pencil"></i>
        Edit Server
      </button>
      <button class="btn btn-delete" @click="handleDelete">
        <i class="bi bi-trash"></i>
        Delete Server
      </button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  server: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['edit', 'delete']);

function handleEdit() {
  emit('edit', props.server);
}

function handleDelete() {
  emit('delete', props.server);
}

function getStatusClass(status) {
  const classes = {
    'ACTIVE': 'status-active',
    'INACTIVE': 'status-inactive',
    'ERROR': 'status-error'
  };
  return classes[status] || 'status-unknown';
}

function getStatusIcon(status) {
  const icons = {
    'ACTIVE': 'bi bi-check-circle-fill',
    'INACTIVE': 'bi bi-pause-circle-fill',
    'ERROR': 'bi bi-x-circle-fill'
  };
  return icons[status] || 'bi bi-question-circle-fill';
}

function formatStatus(status) {
  if (!status) return 'Unknown';
  return status.charAt(0) + status.slice(1).toLowerCase();
}
</script>

<style scoped>
.server-details-card {
  background: var(--color-gray-50);
  border-radius: var(--radius-lg);
  padding: var(--spacing-2xl);
  border: 2px solid var(--color-gray-100);
}

.server-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-2xl);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.info-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-gray-600);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-label i {
  color: var(--color-primary);
}

.info-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-gray-800);
  word-break: break-word;
}

.server-id {
  font-family: 'Monaco', 'Menlo', 'Courier New', monospace;
  font-size: 0.9rem;
  color: var(--color-gray-600);
}

.server-link {
  color: var(--color-primary);
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  transition: color var(--transition-base);
}

.server-link:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: 0.5rem 1rem;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 600;
  width: fit-content;
}

.status-active {
  background: #d1fae5;
  color: #065f46;
}

.status-inactive {
  background: #fef3c7;
  color: #92400e;
}

.status-error {
  background: #fee2e2;
  color: #991b1b;
}

.status-unknown {
  background: var(--color-gray-100);
  color: var(--color-gray-800);
}

.server-actions-bar {
  display: flex;
  gap: var(--spacing-md);
  padding-top: var(--spacing-xl);
  border-top: 2px solid var(--color-gray-200);
}

.btn {
  padding: 0.875rem var(--spacing-xl);
  font-size: 1rem;
  font-weight: 600;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.btn-edit {
  background: #3b82f6;
  color: white;
}

.btn-edit:hover {
  background: #2563eb;
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.btn-delete {
  background: var(--color-danger);
  color: white;
}

.btn-delete:hover {
  background: #b91c1c;
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

@media (max-width: 768px) {
  .server-details-card {
    padding: var(--spacing-lg);
  }

  .server-info-grid {
    grid-template-columns: 1fr;
  }

  .server-actions-bar {
    flex-direction: column;
  }
}
</style>
