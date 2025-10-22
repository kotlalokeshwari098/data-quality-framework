<template>
  <div class="servers-page">
    <PageHeader
      title="Reporting Management"
      mobileTitle="Servers"
      subtitle="Manage and register central servers to which reports should be sent"
      icon="bi bi-file-earmark-text-fill"
    />

    <div class="page-content">
      <div class="servers-container">
        <!-- Server Registration Card -->
        <div class="servers-card">
          <div class="servers-section">
            <div class="section-header">
              <div>
                <h2 class="section-title">
                  <i class="bi bi-plus-circle"></i>
                  Register New Server
                </h2>
                <p class="section-description">
                  Add a new central server to the data quality framework
                </p>
              </div>
            </div>

            <form @submit.prevent="registerServer" class="server-form">
              <div class="form-group">
                <label for="serverName" class="form-label">
                  Server Name
                </label>
                <input
                  type="text"
                  class="form-control"
                  id="serverName"
                  v-model="newServer.name"
                  placeholder="e.g., Production Central Server"
                  required
                  maxlength="255"
                >
                <small class="form-help">
                  A descriptive name for this server (max 255 characters)
                </small>
              </div>

              <div class="form-group">
                <label for="serverUrl" class="form-label">
                  Server URL
                </label>
                <input
                  type="url"
                  class="form-control"
                  id="serverUrl"
                  v-model="newServer.url"
                  placeholder="https://central.example.com"
                  required
                  maxlength="500"
                  pattern="^https?://[^\s/$.?#].[^\s]*$"
                >
                <small class="form-help">
                  The base URL of the central server (max 500 characters)
                </small>
              </div>

              <div class="form-actions">
                <SaveButton
                  type="submit"
                  :loading="isRegistering"
                  :text="isRegistering ? 'Registering...' : 'Register Server'"
                />
                <CancelButton
                  type="button"
                  @click="clearForm"
                  :text="'Clear Form'"
                />
              </div>
            </form>
          </div>
        </div>

        <!-- Servers List Card -->
        <div class="servers-card">
          <div class="servers-section">
            <div class="section-header">
              <div>
                <h2 class="section-title">
                  <i class="bi bi-list-ul"></i>
                  Registered Servers
                </h2>
                <p class="section-description">
                  Manage your registered central servers
                </p>
              </div>
              <button
                class="btn btn-secondary"
                @click="refreshServers"
                :disabled="serverStore.loading"
              >
                <i class="bi bi-arrow-clockwise"></i>
                Refresh
              </button>
            </div>

            <!-- Loading State -->
            <div v-if="serverStore.loading && servers.length === 0" class="loading-state">
              <div class="loading-spinner">
                <i class="bi bi-arrow-clockwise spinning"></i>
              </div>
              <p>Loading servers...</p>
            </div>

            <!-- Empty State -->
            <div v-else-if="!serverStore.loading && servers.length === 0" class="empty-state">
              <div class="empty-icon">
                <i class="bi bi-server"></i>
              </div>
              <h3>No Servers Registered</h3>
              <p>Register your first central server using the form above.</p>
            </div>

            <!-- Servers Table -->
            <div v-else class="servers-table-container">
              <table class="servers-table">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>URL</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="server in servers" :key="server.id" class="server-row">
                    <td class="server-name">{{ server.name }}</td>
                    <td class="server-url">
                      <a :href="server.url" target="_blank" rel="noopener noreferrer">
                        {{ server.url }}
                        <i class="bi bi-box-arrow-up-right"></i>
                      </a>
                    </td>
                    <td class="server-status">
                      <span :class="['status-badge', getStatusClass(server.status)]">
                        <i :class="getStatusIcon(server.status)"></i>
                        {{ formatStatus(server.status) }}
                      </span>
                    </td>
                    <td class="server-actions">
                      <button
                        class="btn-icon btn-icon-edit"
                        @click="editServer(server)"
                        :title="`Edit ${server.name}`"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button
                        class="btn-icon btn-icon-delete"
                        @click="confirmDelete(server)"
                        :title="`Delete ${server.name}`"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Server Modal -->
    <BaseModal v-if="showEditModal" @close="closeEditModal">
      <template #header>
        <h3>Edit Server</h3>
      </template>
      <template #body>
        <form @submit.prevent="updateServer" class="modal-form">
          <div class="form-group">
            <label for="editServerName" class="form-label">
              Server Name
            </label>
            <input
              type="text"
              class="form-control"
              id="editServerName"
              v-model="editingServer.name"
              required
              maxlength="255"
            >
          </div>

          <div class="form-group">
            <label for="editServerUrl" class="form-label">
              Server URL
            </label>
            <input
              type="url"
              class="form-control"
              id="editServerUrl"
              v-model="editingServer.url"
              required
              maxlength="500"
              pattern="^https?://[^\s/$.?#].[^\s]*$"
            >
          </div>
        </form>
      </template>
      <template #footer>
        <div class="modal-actions">
          <CancelButton @click="closeEditModal" text="Cancel" />
          <SaveButton
            @click="updateServer"
            :loading="isUpdating"
            :text="isUpdating ? 'Updating...' : 'Update Server'"
          />
        </div>
      </template>
    </BaseModal>

    <!-- Delete Confirmation Modal -->
    <BaseModal v-if="showDeleteModal" @close="closeDeleteModal">
      <template #header>
        <h3>Confirm Deletion</h3>
      </template>
      <template #body>
        <p>Are you sure you want to delete the server <strong>{{ deletingServer?.name }}</strong>?</p>
        <p class="text-warning">This action cannot be undone.</p>
      </template>
      <template #footer>
        <div class="modal-actions">
          <CancelButton @click="closeDeleteModal" text="Cancel" />
          <button
            class="btn btn-danger"
            @click="deleteServer"
            :disabled="isDeleting"
          >
            <i class="bi bi-trash"></i>
            {{ isDeleting ? 'Deleting...' : 'Delete Server' }}
          </button>
        </div>
      </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject, computed } from 'vue';
import serverStore from '../stores/serverStore.js';
import PageHeader from '../components/PageHeader.vue';
import HealthStatusBanner from '../components/HealthStatusBanner.vue';
import SaveButton from '../components/SaveButton.vue';
import CancelButton from '../components/CancelButton.vue';
import BaseModal from '../components/BaseModal.vue';

const notify = inject('notify');

const isRegistering = ref(false);
const isUpdating = ref(false);
const isDeleting = ref(false);
const showEditModal = ref(false);
const showDeleteModal = ref(false);

const newServer = reactive({
  name: '',
  url: ''
});

const editingServer = reactive({
  id: '',
  name: '',
  url: ''
});

const deletingServer = ref(null);

const servers = computed(() => serverStore.servers);

function clearForm() {
  newServer.name = '';
  newServer.url = '';
}

async function registerServer() {
  if (!newServer.name.trim() || !newServer.url.trim()) {
    notify.error('Validation Error', 'Please fill in all required fields');
    return;
  }

  isRegistering.value = true;
  try {
    await serverStore.createServer({
      name: newServer.name.trim(),
      url: newServer.url.trim()
    });

    notify.success('Server Registered', `${newServer.name} has been registered successfully`);
    clearForm();
  } catch (error) {
    notify.error('Registration Failed', serverStore.error || 'Unable to register server. Please try again.');
  } finally {
    isRegistering.value = false;
  }
}

function editServer(server) {
  editingServer.id = server.id;
  editingServer.name = server.name;
  editingServer.url = server.url;
  showEditModal.value = true;
}

function closeEditModal() {
  showEditModal.value = false;
  editingServer.id = '';
  editingServer.name = '';
  editingServer.url = '';
}

async function updateServer() {
  if (!editingServer.name.trim() || !editingServer.url.trim()) {
    notify.error('Validation Error', 'Please fill in all required fields');
    return;
  }

  isUpdating.value = true;
  try {
    await serverStore.updateServer(editingServer.id, {
      name: editingServer.name.trim(),
      url: editingServer.url.trim()
    });

    notify.success('Server Updated', `${editingServer.name} has been updated successfully`);
    closeEditModal();
  } catch (error) {
    notify.error('Update Failed', serverStore.error || 'Unable to update server. Please try again.');
  } finally {
    isUpdating.value = false;
  }
}

function confirmDelete(server) {
  deletingServer.value = server;
  showDeleteModal.value = true;
}

function closeDeleteModal() {
  showDeleteModal.value = false;
  deletingServer.value = null;
}

async function deleteServer() {
  if (!deletingServer.value) return;

  isDeleting.value = true;
  try {
    await serverStore.deleteServer(deletingServer.value.id);
    notify.success('Server Deleted', `${deletingServer.value.name} has been deleted successfully`);
    closeDeleteModal();
  } catch (error) {
    notify.error('Delete Failed', serverStore.error || 'Unable to delete server. Please try again.');
  } finally {
    isDeleting.value = false;
  }
}

async function refreshServers() {
  try {
    await serverStore.fetchServers();
  } catch (error) {
    notify.error('Refresh Failed', 'Unable to refresh servers list. Please try again.');
  }
}

function getStatusClass(status) {
  switch (status) {
    case 'ACTIVE':
      return 'status-active';
    case 'INACTIVE':
      return 'status-inactive';
    case 'ERROR':
      return 'status-error';
    default:
      return 'status-unknown';
  }
}

function getStatusIcon(status) {
  switch (status) {
    case 'ACTIVE':
      return 'bi bi-check-circle-fill';
    case 'INACTIVE':
      return 'bi bi-pause-circle-fill';
    case 'ERROR':
      return 'bi bi-x-circle-fill';
    default:
      return 'bi bi-question-circle-fill';
  }
}

function formatStatus(status) {
  if (!status) return 'Unknown';
  return status.charAt(0) + status.slice(1).toLowerCase();
}

onMounted(() => {
  refreshServers();
});
</script>

<style scoped>
.servers-page {
  min-height: 100%;
  padding: var(--spacing-xl);
}

.page-content {
  max-width: 1400px;
  margin: 0 auto;
}

.servers-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.servers-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}

.servers-section {
  padding: var(--spacing-2xl);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-xl);
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-gray-800);
  margin: 0 0 var(--spacing-sm) 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.section-title i {
  color: var(--color-primary);
}

.section-description {
  font-size: 0.95rem;
  color: var(--color-gray-500);
  margin: 0;
  line-height: 1.5;
}

.server-form {
  max-width: 600px;
}

.form-group {
  margin-bottom: 1.75rem;
}

.form-label {
  display: block;
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--color-gray-700);
  margin-bottom: var(--spacing-sm);
}

.form-control {
  width: 100%;
  padding: 0.75rem var(--spacing-md);
  font-size: 1rem;
  border: 2px solid var(--color-gray-200);
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
  background: var(--bg-card);
}

.form-control:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-help {
  display: block;
  margin-top: var(--spacing-sm);
  font-size: 0.875rem;
  color: var(--color-gray-500);
  line-height: 1.4;
}

.form-actions {
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 2px solid var(--color-gray-100);
  display: flex;
  gap: var(--spacing-md);
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

.btn-secondary {
  background: var(--color-gray-100);
  color: var(--color-gray-700);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--color-gray-200);
}

.btn-danger {
  background: var(--color-red-500);
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: var(--color-red-600);
}

.loading-state, .empty-state {
  text-align: center;
  padding: 3rem var(--spacing-xl);
  color: var(--color-gray-500);
}

.loading-spinner i {
  font-size: 2rem;
  color: var(--color-primary);
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-icon i {
  font-size: 3rem;
  color: var(--color-gray-300);
  margin-bottom: var(--spacing-md);
}

.empty-state h3 {
  margin: var(--spacing-md) 0 var(--spacing-sm) 0;
  color: var(--color-gray-600);
}

.servers-table-container {
  overflow-x: auto;
}

.servers-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: var(--spacing-lg);
}

.servers-table th,
.servers-table td {
  padding: var(--spacing-md) var(--spacing-lg);
  text-align: left;
  border-bottom: 1px solid var(--color-gray-200);
}

.servers-table th {
  font-weight: 600;
  color: var(--color-gray-700);
  background: var(--color-gray-50);
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.server-row:hover {
  background: var(--color-gray-50);
}

.server-name {
  font-weight: 600;
  color: var(--color-gray-800);
}

.server-url a {
  color: var(--color-primary);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.server-url a:hover {
  text-decoration: underline;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: 0.375rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 600;
}

.status-active {
  background: var(--color-green-100);
  color: var(--color-green-800);
}

.status-inactive {
  background: var(--color-yellow-100);
  color: var(--color-yellow-800);
}

.status-error {
  background: var(--color-red-100);
  color: var(--color-red-800);
}

.status-unknown {
  background: var(--color-gray-100);
  color: var(--color-gray-800);
}

.server-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.btn-icon {
  width: 2.5rem;
  height: 2.5rem;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-base);
}

.btn-icon-edit {
  background: var(--color-blue-100);
  color: var(--color-blue-700);
}

.btn-icon-edit:hover {
  background: var(--color-blue-200);
}

.btn-icon-delete {
  background: var(--color-red-100);
  color: var(--color-red-700);
}

.btn-icon-delete:hover {
  background: var(--color-red-200);
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.modal-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
}

.text-warning {
  color: var(--color-yellow-700);
  font-style: italic;
}

@media (max-width: 768px) {
  .servers-page {
    padding: var(--spacing-md);
  }

  .servers-section {
    padding: var(--spacing-lg);
  }

  .section-header {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .server-form {
    max-width: 100%;
  }

  .form-actions {
    flex-direction: column;
  }

  .servers-table-container {
    overflow-x: scroll;
  }

  .modal-actions {
    flex-direction: column;
  }
}

@media (max-width: 576px) {
  .servers-page {
    padding: 0.75rem;
  }

  .servers-section {
    padding: 1.25rem;
  }

  .section-title {
    font-size: 1.2rem;
    gap: var(--spacing-sm);
  }
}
</style>
