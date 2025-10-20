<template>
  <header class="top-navbar">
    <div class="top-navbar-content">
      <div class="top-navbar-spacer"></div>

      <!-- User Section -->
      <div class="user-section">
        <div v-if="defaultPasswordFlag" class="password-warning"
             @mouseenter="showTooltip = true"
             @mouseleave="showTooltip = false">
          <i class="bi bi-exclamation-triangle-fill text-warning"></i>
          <div v-if="showTooltip" class="warning-tooltip">
            For your account's security, please change your password.<br>
            Your current password is a system-generated default.
          </div>
        </div>

        <div class="user-dropdown">
          <button @click="toggleDropdown" class="user-dropdown-toggle">
            <div class="user-avatar">
              <span class="user-initials">{{ getUserInitials() }}</span>
            </div>
            <span class="username">{{ username }}</span>
            <i class="bi bi-chevron-down"></i>
          </button>

          <div v-if="showDropdown" class="dropdown-menu-custom">
            <button class="dropdown-item-custom" @click="showChangePasswordModal">
              <i class="bi bi-key me-2"></i>
              Change Password
              <span v-if="defaultPasswordFlag" class="badge bg-warning text-dark ms-2">Required</span>
            </button>
            <button class="dropdown-item-custom dropdown-item-signout" @click="handleLogout">
              <i class="bi bi-box-arrow-right me-2"></i>
              Sign Out
            </button>
          </div>
        </div>
      </div>
    </div>
  </header>

  <PasswordChangeModal :isVisible="showPasswordModal" @close="closePasswordModal" />
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { clearAuth, getUsername } from '../js/api.js'
import { useUserStore } from '../stores/userStore.js'
import PasswordChangeModal from './PasswordChangeModal.vue'

const router = useRouter()
const username = ref(getUsername())
const showDropdown = ref(false)
const showPasswordModal = ref(false)
const showTooltip = ref(false)

const { defaultPasswordFlag, initializeDefaultPasswordStatus } = useUserStore()

const handleLogout = () => {
  clearAuth()
  router.push('/login')
}

const getUserInitials = () => {
  const user = username.value || 'U'
  return user.substring(0, 2).toUpperCase()
}

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const showChangePasswordModal = () => {
  showDropdown.value = false
  showPasswordModal.value = true
}

const closePasswordModal = () => {
  showPasswordModal.value = false
  initializeDefaultPasswordStatus()
}

const handleClickOutside = (event) => {
  if (showDropdown.value && !event.target.closest('.user-dropdown')) {
    showDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  initializeDefaultPasswordStatus()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* Top Navigation Bar */
.top-navbar {
  position: fixed;
  top: 0;
  left: 260px;
  right: 0;
  height: 64px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  z-index: 900;
  transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.top-navbar-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
}

.top-navbar-spacer {
  flex: 1;
}

/* User Section */
.user-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.password-warning {
  position: relative;
  cursor: pointer;
}

.password-warning i {
  font-size: 1.25rem;
}

.warning-tooltip {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 0.5rem;
  padding: 0.75rem;
  background: #1f2937;
  color: white;
  border-radius: 8px;
  font-size: 0.875rem;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  min-width: 250px;
}

.warning-tooltip::before {
  content: '';
  position: absolute;
  top: -4px;
  right: 1rem;
  width: 8px;
  height: 8px;
  background: #1f2937;
  transform: rotate(45deg);
}

.user-dropdown {
  position: relative;
}

.user-dropdown-toggle {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 1rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-dropdown-toggle:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}

.user-avatar {
  width: 36px;
  height: 36px;
  min-width: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.85rem;
  color: white;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.username {
  font-weight: 500;
  color: #374151;
  font-size: 0.9rem;
}

.dropdown-menu-custom {
  position: absolute;
  top: calc(100% + 0.25rem);
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  min-width: 200px;
  z-index: 1000;
  overflow: hidden;
}

.dropdown-item-custom {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  transition: background 0.2s ease;
  font-size: 0.9rem;
  color: #374151;
}

.dropdown-item-custom:hover {
  background: #f3f4f6;
}

.dropdown-item-signout {
  color: #dc2626;
}

.dropdown-item-signout:hover {
  background: #fee2e2;
}

/* Mobile Responsive */
@media (max-width: 768px) {
  .top-navbar {
    left: 0;
    height: 56px;
  }

  .top-navbar-content {
    padding: 0 4rem 0 1rem;
  }

  .user-section {
    gap: 0.75rem;
  }

  .user-avatar {
    width: 32px;
    height: 32px;
    min-width: 32px;
    font-size: 0.75rem;
  }

  .username {
    display: none;
  }

  .warning-tooltip {
    min-width: 200px;
    font-size: 0.75rem;
  }
}
</style>
