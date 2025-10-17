<template>
  <aside class="sidebar" :class="{ show: showMobileMenu }">
    <div class="sidebar-container">
      <!-- Brand -->
      <router-link to="/dashboard" class="sidebar-brand">
        <div class="brand-icon">
          <i class="bi bi-bar-chart-fill"></i>
        </div>
        <div class="brand-text">
          <h4>Data Quality</h4>
          <small>Agent</small>
        </div>
      </router-link>

      <!-- Navigation Menu -->
      <nav class="sidebar-nav">
        <router-link to="/dashboard" class="nav-link" :class="{ active: $route.path === '/dashboard' }" @click="closeMobileMenu">
          <i class="bi bi-grid-3x3-gap-fill"></i>
          <span>Dashboard</span>
        </router-link>
        <router-link to="/quality-checks" class="nav-link" :class="{ active: $route.path === '/quality-checks' }" @click="closeMobileMenu">
          <i class="bi bi-clipboard-check-fill"></i>
          <span>Quality Checks</span>
        </router-link>
        <router-link to="/reports" class="nav-link" :class="{ active: $route.path === '/reports' }" @click="closeMobileMenu">
          <i class="bi bi-file-earmark-text-fill"></i>
          <span>Reports</span>
        </router-link>
        <router-link to="/settings" class="nav-link" :class="{ active: $route.path === '/settings' }" @click="closeMobileMenu">
          <i class="bi bi-gear-fill"></i>
          <span>Settings</span>
        </router-link>
      </nav>
    </div>
  </aside>

  <!-- Mobile Menu Toggle -->
  <button class="mobile-menu-toggle" @click="toggleMobileMenu">
    <i class="bi bi-list"></i>
  </button>

  <!-- Mobile Overlay -->
  <div class="mobile-overlay" :class="{ show: showMobileMenu }" @click="closeMobileMenu"></div>
</template>

<script setup>
import { ref } from 'vue'

const showMobileMenu = ref(false)

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

const closeMobileMenu = () => {
  showMobileMenu.value = false
}
</script>

<style scoped>
/* Sidebar */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  width: 260px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.sidebar-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 1.5rem 0;
}

/* Brand Section */
.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0 1.5rem;
  margin-bottom: 2rem;
  color: white;
  text-decoration: none;
  transition: all 0.2s ease;
}

.sidebar-brand:hover {
  transform: translateX(2px);
}

.brand-icon {
  width: 48px;
  height: 48px;
  min-width: 48px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transition: all 0.2s ease;
}

.sidebar-brand:hover .brand-icon {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.05);
}

.brand-text h4 {
  margin: 0;
  font-weight: 600;
  font-size: 1.25rem;
  line-height: 1.2;
}

.brand-text small {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.875rem;
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0 1rem;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.875rem 1rem;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  border-radius: 12px;
  font-weight: 500;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.nav-link i {
  font-size: 1.25rem;
  min-width: 1.25rem;
  transition: transform 0.2s ease;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  transform: translateX(2px);
}

.nav-link:hover i {
  transform: scale(1.1);
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.25);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-link.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 60%;
  background: white;
  border-radius: 0 4px 4px 0;
}

/* Mobile Menu Toggle */
.mobile-menu-toggle {
  display: none;
  position: fixed;
  top: 1rem;
  left: 1rem;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  z-index: 1001;
  transition: all 0.2s ease;
}

.mobile-menu-toggle:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.mobile-overlay {
  display: none;
}

/* Mobile Responsive */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .sidebar.show {
    transform: translateX(0);
  }

  .mobile-menu-toggle {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .mobile-overlay {
    display: block;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
    z-index: 999;
  }

  .mobile-overlay.show {
    opacity: 1;
    visibility: visible;
  }
}
</style>
