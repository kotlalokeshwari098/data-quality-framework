<template>
  <div class="d-flex min-vh-100">
    <AppSidebar @sectionChange="handleSectionChange" @sidebarToggle="handleSidebarToggle" />

    <div class="flex-grow-1 d-flex flex-column" :class="{ 'ms-sidebar-collapsed': isSidebarCollapsed, 'ms-sidebar-expanded': !isSidebarCollapsed }">
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm px-4">
      <div class="container-fluid">
        <div class="ms-auto d-flex align-items-center text-white">
          <i class="bi bi-person-fill fs-4 me-2"></i>
          <div v-if="defaultPasswordFlag" class="position-relative me-2"
               @mouseenter="showTooltip = true"
               @mouseleave="showTooltip = false">
            <i class="bi bi-exclamation-triangle-fill text-warning fs-5"></i>
            <div v-if="showTooltip" class="custom-tooltip">
              For your account's security, please change your password.<br>
              Your current password is a system-generated default.
            </div>
          </div>

          <div class="position-relative">
            <span class="username-dropdown cursor-pointer" @click="toggleDropdown">
              {{ username }}
              <i class="bi bi-chevron-down ms-1"></i>
            </span>

            <div v-if="showDropdown" class="dropdown-menu-custom position-absolute">
              <button class="dropdown-item-custom" @click="showChangePasswordModal">
                <i class="bi bi-key me-2"></i>
                Change Password
                <span v-if="defaultPasswordFlag" class="badge bg-warning text-dark ms-2">Required</span>
              </button>
            </div>
          </div>
          <button class="btn text-white ms-3" @click="logout">
            <i class="bi bi-box-arrow-right"></i> Logout
          </button>
        </div>
      </div>
    </nav>

    <PasswordChangeModal :isVisible="showPasswordModal" @close="closePasswordModal" />

      <div v-if="activeSection === 'quality'" class="flex-grow-1 d-flex flex-column">
        <header class="text-center py-3 bg-light">
          <h1 class="h1 fw-bold m-0">Data Quality Agent</h1>
        </header>

        <main class="flex-grow-1 p-4 bg-light">
          <data-quality-reports></data-quality-reports>
          <c-q-l-Check-table></c-q-l-Check-table>
        </main>
      </div>

    <SettingsPage
        v-if="activeSection === 'settings'"
        :username="username"
    />

    <footer class="footer bg-secondary text-white text-center">
      © 2025 BBMRI-ERIC
    </footer>
  </div>
  </div>
</template>

<script setup>
import CQLCheckTable from "../CQLCheckTable.vue";
import DataQualityReports from "../DataQualityReports.vue";
import PasswordChangeModal from "../PasswordChangeModal.vue";
import AppSidebar from '../AppSidebar.vue';
import SettingsPage from "../SettingsPage.vue";
import {clearAuth, getUsername} from '../../js/api.js';
import { useUserStore } from '../../stores/userStore.js';
import { useRouter } from 'vue-router';
import {ref, onMounted, onUnmounted, watch} from "vue";


const router = useRouter();
const username = ref(getUsername());
const showDropdown = ref(false);
const showPasswordModal = ref(false);
const showTooltip = ref(false);
const activeSection = ref('quality');
const isSidebarCollapsed = ref(false);

const { defaultPasswordFlag, initializeDefaultPasswordStatus } = useUserStore();

watch(showPasswordModal, (newValue) => {
  if (!newValue) {
    initializeDefaultPasswordStatus();
  }
});

function toggleDropdown() {
  showDropdown.value = !showDropdown.value;
}

function showChangePasswordModal() {
  showDropdown.value = false;
  showPasswordModal.value = true;
}

async function closePasswordModal() {
  showPasswordModal.value = false;
}

function handleClickOutside(event) {
  if (showDropdown.value && !event.target.closest('.username-dropdown')) {
    showDropdown.value = false;
  }
}

function logout() {
  clearAuth();
  router.replace('/login');
}

function handleSectionChange(section) {
  activeSection.value = section;
}

function handleSidebarToggle(collapsed) {
  isSidebarCollapsed.value = collapsed;
}


onMounted(async () => {
  document.addEventListener('click', handleClickOutside);
  initializeDefaultPasswordStatus();
});
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>



.footer {
  padding: 1rem 0;
}

.cursor-pointer {
  cursor: pointer;
}
.username-dropdown {
  padding: 0.5rem;
  border-radius: 0.25rem;
  transition: background-color 0.2s;
}
.username-dropdown:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
.dropdown-menu-custom {
  top: 100%;
  right: 0;
  background-color: white;
  border: 1px solid #dee2e6;
  border-radius: 0.375rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
  min-width: 200px;
  z-index: 1000;
  margin-top: 0.25rem;
}
.dropdown-item-custom {
  display: block;
  width: 100%;
  padding: 0.5rem 1rem;
  color: #212529;
  background: none;
  border: none;
  text-align: left;
  transition: background-color 0.2s;
}
.dropdown-item-custom:hover {
  background-color: #f8f9fa;
}
.custom-tooltip {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 0.5rem;
  border-radius: 0.25rem;
  white-space: nowrap;
  z-index: 1000;
}

.ms-sidebar-expanded {
  margin-left: 250px;
  transition: margin-left 0.3s ease;
}

.ms-sidebar-collapsed {
  margin-left: 70px;
  transition: margin-left 0.3s ease;
}
</style>
