<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import TopNavbar from './components/TopNavbar.vue'
import AppFooter from './components/AppFooter.vue'
import NotificationContainer from './components/NotificationContainer.vue'
import { authStore } from './stores/authStore.js'
import { notificationService } from './services/notificationService.js'

useRouter()
const notificationContainer = ref(null)

onMounted(() => {
  if (notificationContainer.value) {
    notificationService.setContainer(notificationContainer.value)
  }
})
</script>

<template>
  <div id="app">
    <Sidebar v-if="authStore.isAuthenticated" />
    <TopNavbar v-if="authStore.isAuthenticated" />

    <main class="main-content">
      <router-view />
    </main>

    <AppFooter v-if="!authStore.isAuthenticated" />
    <NotificationContainer ref="notificationContainer" />
  </div>
</template>

<style>
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: #f8fafc;
  min-height: 100vh;
  /* Improve text rendering on mobile */
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  /* Prevent zoom on double tap */
  touch-action: manipulation;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  /* Ensure app takes full width on mobile */
  width: 100%;
}

.main-content {
  flex: 1;
  background: #f8fafc;
  margin-left: 260px;
  margin-top: 64px;
  min-height: calc(100vh - 64px);
  transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  width: 100%;
}

/* When sidebar is collapsed */
.main-content.sidebar-collapsed {
  margin-left: 80px;
}

/* When not authenticated (login page) */
.main-content.no-sidebar {
  margin-left: 0;
  margin-top: 0;
  min-height: 100vh;
}

.main-content:has(.login-form) {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  margin-left: 0;
  margin-top: 0;
  margin-bottom: 0;
  min-height: calc(100vh - 120px);
}

/* Mobile-specific styles */
@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    margin-top: 56px;
    padding: 1rem 0.5rem;
    min-height: calc(100vh - 56px);
  }

  .main-content.sidebar-collapsed {
    margin-left: 0;
  }

  .main-content:has(.login-form) {
    margin-top: 0;
    min-height: calc(100vh - 100px);
  }

  .btn {
    min-height: 44px;
    padding: 0.75rem 1rem;
  }

  .btn-sm {
    min-height: 36px;
    padding: 0.5rem 0.75rem;
  }

  .form-control,
  .form-select {
    font-size: 16px; /* Prevent zoom on iOS */
    min-height: 44px;
  }
}
</style>
