<template>
  <aside
      class="sidebar d-flex flex-column position-fixed top-0 start-0 vh-100 shadow bg-light"
      :class="{ collapsed: isCollapsed }"
  >
    <div class="sidebar-header navbar navbar-dark bg-primary px-4">
      <button class="navbar-toggler border-0 p-0" type="button" @click="toggleSidebar">
        <i v-if="isCollapsed" class="bi bi-list fs-4"></i>
        <i v-else class="bi bi-chevron-left fs-4"></i>
      </button>
    </div>

    <nav class="sidebar-nav flex-grow-1 py-3">
      <ul class="nav flex-column list-unstyled mb-0">
        <li class="nav-item mb-1">
          <a
              href="#"
              class="nav-link d-flex align-items-center px-3 py-2 fw-medium"
              :class="{ active: activeSection === 'quality' }"
              @click.prevent="setActiveSection('quality')"
          >
            <i class="bi bi-bar-chart-fill me-2"></i>
            <span v-show="!isCollapsed">Data Quality</span>
          </a>
        </li>
        <li class="nav-item mb-1">
          <a
              href="#"
              class="nav-link d-flex align-items-center px-3 py-2 fw-medium"
              :class="{ active: activeSection === 'settings' }"
              @click.prevent="setActiveSection('settings')"
          >
            <i class="bi bi-gear-fill me-2"></i>
            <span v-show="!isCollapsed">Settings</span>
          </a>
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script setup>
import { ref } from 'vue';

const emit = defineEmits(['sectionChange', 'sidebarToggle']);
const activeSection = ref('quality');
const isCollapsed = ref(false);

function setActiveSection(section) {
  activeSection.value = section;
  emit('sectionChange', section);
}

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value;
  emit('sidebarToggle', isCollapsed.value);
}
</script>

<style scoped>
.sidebar {
  width: 250px;
  transition: width 0.3s ease;
  overflow: hidden;
  z-index: 1000;
}

.sidebar.collapsed {
  width: 70px;
}

.sidebar-header {
  position: relative;
  flex-shrink: 0;
  min-height: 54px;
}

.navbar-toggler {
  color: white !important;
  border: none !important;
  outline: none !important;
}

.navbar-toggler:focus {
  box-shadow: none !important;
  outline: none !important;
}

.navbar-toggler i {
  color: white !important;
}

.sidebar.collapsed .nav-link {
  justify-content: center !important;
}

.nav-link {
  text-decoration: none;
  white-space: nowrap;
  overflow: hidden;
}

.nav-link span {
  transition: opacity 0.3s ease;
}

.sidebar.collapsed .nav-link span {
  opacity: 0;
}

.sidebar:not(.collapsed) .nav-link span {
  opacity: 1;
}

.nav-link.active {
  background-color: var(--bs-primary);
  color: white;
}

.nav-link:hover:not(.active) {
  background-color: var(--bs-light);
  color: var(--bs-dark);
}
</style>
