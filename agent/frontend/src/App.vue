<template>
  <div id="app">
    <Sidebar v-if="isAuth" />
    <TopNavbar v-if="isAuth" />

    <main class="main-content" :class="{ 'no-sidebar': !isAuth }">
      <router-view />
    </main>

    <AppFooter v-if="!isAuth" />
    <NotificationContainer ref="notificationContainer" />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import TopNavbar from './components/TopNavbar.vue'
import AppFooter from './components/AppFooter.vue'
import NotificationContainer from './components/NotificationContainer.vue'
import { isAuthenticated } from './js/api.js'

const router = useRouter()
const route = useRoute()
const notificationContainer = ref(null)
const isAuth = ref(isAuthenticated())

// Watch for route changes to update auth status
watch(() => route.path, () => {
  isAuth.value = isAuthenticated()
})

onMounted(() => {
  if (notificationContainer.value) {
    // Could set up notification service here if needed
  }
})
</script>

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
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  touch-action: manipulation;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.main-content {
  flex: 1;
  background: #f8fafc;
  margin-left: 260px;
  margin-top: 64px;
  min-height: calc(100vh - 64px);
  transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  width: calc(100% - 260px);
}

/* When not authenticated (login page) */
.main-content.no-sidebar {
  margin-left: 0;
  margin-top: 0;
  min-height: 100vh;
  width: 100%;
}

/* Mobile-specific styles */
@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    margin-top: 56px;
    padding: 1rem 0.5rem;
    min-height: calc(100vh - 56px);
    width: 100%;
  }

  .main-content.no-sidebar {
    margin-top: 0;
    min-height: 100vh;
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
    font-size: 16px;
    min-height: 44px;
  }
}
</style>
