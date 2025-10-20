<template>
  <div
    v-if="props.isVisible"
    class="modal fade show d-block modal-backdrop-custom"
    tabindex="-1"
    @click.self="handleClose"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content border-0 shadow-lg">
        <div class="modal-header bg-gradient-purple border-0 text-white py-3">
          <div class="d-flex align-items-center">
            <div class="icon-wrapper me-3">
              <i class="bi bi-key fs-4"></i>
            </div>
            <div>
              <h5 class="modal-title mb-0">Change Password</h5>
              <small class="opacity-90">Update your account password</small>
            </div>
          </div>
          <button
            type="button"
            class="btn-close-custom"
            @click="handleClose"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body p-4">
          <form @submit.prevent="changePassword">
            <div class="form-section mb-3">
              <div class="mb-3">
                <label for="currentPassword" class="form-label fw-semibold">
                  Current Password <span class="text-danger">*</span>
                </label>
                <input
                  type="password"
                  class="form-control"
                  id="currentPassword"
                  v-model="passwordForm.currentPassword"
                  placeholder="Enter current password"
                  required
                >
              </div>
              <div class="mb-3">
                <label for="newPassword" class="form-label fw-semibold">
                  New Password <span class="text-danger">*</span>
                </label>
                <input
                  type="password"
                  class="form-control"
                  id="newPassword"
                  v-model="passwordForm.newPassword"
                  placeholder="Enter new password"
                  minlength="8"
                  required
                >
                <small class="form-text text-muted">
                  <i class="bi bi-info-circle me-1"></i>
                  Password must be at least 8 characters long
                </small>
              </div>
              <div class="mb-3">
                <label for="confirmPassword" class="form-label fw-semibold">
                  Confirm New Password <span class="text-danger">*</span>
                </label>
                <input
                  type="password"
                  class="form-control"
                  id="confirmPassword"
                  v-model="passwordForm.confirmPassword"
                  placeholder="Confirm new password"
                  minlength="8"
                  required
                >
              </div>
            </div>
            <div v-if="passwordError" class="alert alert-danger py-2 mb-3" role="alert">
              <i class="bi bi-exclamation-circle me-2"></i>
              {{ passwordError }}
            </div>
            <div v-if="passwordSuccess" class="alert alert-success py-2 mb-3" role="alert">
              <i class="bi bi-check-circle me-2"></i>
              {{ passwordSuccess }}
            </div>
            <div class="modal-footer-custom bg-light border-0 py-3 d-flex justify-content-center">
              <div class="d-flex gap-2">
                <button type="button" class="btn btn-secondary" @click="handleClose">
                  <i class="bi bi-x-circle me-2"></i>
                  Cancel
                </button>
                <button
                  type="submit"
                  class="btn btn-primary-gradient"
                  :disabled="isChangingPassword"
                >
                  <span v-if="isChangingPassword" class="spinner-border spinner-border-sm me-2" role="status"></span>
                  <i v-else class="bi bi-check-circle me-2"></i>
                  Change Password
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useUserStore } from '../stores/userStore.js';

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close']);

const { isChangingPassword, passwordError, passwordSuccess, changePassword: storeChangePassword } = useUserStore();

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

function handleClose() {
  resetPasswordForm();
  emit('close');
}

function resetPasswordForm() {
  passwordForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  passwordError.value = '';
  passwordSuccess.value = '';
}

async function changePassword() {
  const success = await storeChangePassword(
    passwordForm.value.currentPassword,
    passwordForm.value.newPassword,
    passwordForm.value.confirmPassword
  );
  if (success) {
    setTimeout(() => {
      handleClose();
    }, 1000);
  }
}

watch(() => props.isVisible, (newValue) => {
  if (newValue) {
    resetPasswordForm();
  }
});
</script>

<style scoped>
.modal-backdrop-custom {
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(2px);
}

.modal-dialog-centered {
  display: flex;
  align-items: center;
  min-height: calc(100% - 3.5rem);
}

.modal-content {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.bg-gradient-purple {
  background: var(--gradient-primary);
}

.btn-close-custom {
  background: transparent;
  border: none;
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  transition: all var(--transition-base);
  position: relative;
  opacity: 1;
  cursor: pointer;
  margin-left: auto;
  flex-shrink: 0;
}

.btn-close-custom::before,
.btn-close-custom::after {
  content: '';
  position: absolute;
  width: 18px;
  height: 2px;
  background-color: white;
  border-radius: 1px;
  transition: all var(--transition-base);
}

.btn-close-custom::before {
  transform: rotate(45deg);
}

.btn-close-custom::after {
  transform: rotate(-45deg);
}

.btn-close-custom:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.btn-close-custom:hover::before,
.btn-close-custom:hover::after {
  background-color: white;
  width: 20px;
}

.btn-close-custom:active {
  transform: scale(0.95);
}

.btn-close-custom:focus {
  outline: 2px solid rgba(255, 255, 255, 0.5);
  outline-offset: 2px;
}

.icon-wrapper {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  background: linear-gradient(to bottom, var(--color-gray-50) 0%, var(--bg-card) 100%);
}

.form-section {
  background: var(--bg-card);
  padding: var(--spacing-lg);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-gray-200);
}

.form-label {
  margin-bottom: var(--spacing-sm);
  font-size: 0.875rem;
  color: var(--color-gray-600);
}

.form-control {
  font-size: 0.875rem;
  border: 1px solid var(--color-gray-300);
  transition: all var(--transition-base);
}

.form-control:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
}

.form-text {
  display: block;
  margin-top: var(--spacing-xs);
  font-size: 0.813rem;
}

.btn-primary-gradient {
  background: var(--gradient-primary);
  color: white;
  border: none;
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--radius-sm);
  font-weight: 600;
  transition: all var(--transition-slow);
  box-shadow: var(--shadow-primary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-primary-gradient:hover:not(:disabled) {
  background: var(--gradient-primary-reverse);
  transform: translateY(-2px);
  box-shadow: var(--shadow-primary-hover);
  color: white;
}

.btn-primary-gradient:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: var(--shadow-primary);
}

.btn-primary-gradient:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: var(--color-gray-500);
  border-color: var(--color-gray-500);
  color: white;
  padding: var(--spacing-sm) 1.25rem;
  border-radius: var(--radius-sm);
  font-weight: 500;
  transition: all var(--transition-base);
}

.btn-secondary:hover {
  background: var(--color-gray-600);
  border-color: var(--color-gray-600);
  transform: translateY(-1px);
}

.alert {
  border-radius: var(--radius-md);
  border: none;
  display: flex;
  align-items: center;
}

.alert-danger {
  background-color: #fee;
  color: #c00;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
}
</style>