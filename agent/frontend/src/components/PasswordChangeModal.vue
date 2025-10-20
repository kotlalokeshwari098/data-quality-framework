<template>
  <div
    v-if="props.isVisible"
    class="modal fade show d-block modal-backdrop-custom"
    tabindex="-1"
    @click.self="handleClose"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content border-0 shadow-lg">
        <div class="modal-header bg-gradient-purple border-0 text-white py-4">
          <div class="d-flex align-items-center">
            <div class="icon-wrapper me-4">
              <i class="bi bi-key fs-3"></i>
            </div>
            <div>
              <h4 class="modal-title mb-1">Change Password</h4>
              <p class="opacity-90 mb-0" style="font-size: 0.95rem;">Update your account password</p>
            </div>
          </div>
          <button
            type="button"
            class="btn-close-custom"
            @click="handleClose"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body p-5">
          <form @submit.prevent="changePassword">
            <div class="mb-4">
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
            <div class="mb-4">
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
            <div class="mb-4">
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
            <div v-if="passwordError" class="alert alert-danger py-2 mb-3" role="alert">
              <i class="bi bi-exclamation-circle me-2"></i>
              {{ passwordError }}
            </div>
            <div v-if="passwordSuccess" class="alert alert-success py-2 mb-3" role="alert">
              <i class="bi bi-check-circle me-2"></i>
              {{ passwordSuccess }}
            </div>
            <div class="modal-footer-custom border-0 py-3 d-flex justify-content-center">
              <div class="d-flex gap-2">
                <CancelButton @click="handleClose" />
                <SaveButton
                  type="submit"
                  :loading="isChangingPassword"
                  text="Change Password"
                />
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
import SaveButton from './SaveButton.vue';
import CancelButton from './CancelButton.vue';

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
  max-width: 600px;
  width: 90%;
  margin: 0 auto;
}

.modal-content {
  border-radius: var(--radius-lg);
  overflow: hidden;
  width: 100%;
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
  background: var(--bg-card);
}


.form-label {
  margin-bottom: var(--spacing-md);
  font-size: 1rem;
  color: var(--color-gray-600);
}

.form-control {
  font-size: 1rem;
  padding: 0.875rem 1rem;
  border: 1px solid var(--color-gray-300);
  transition: all var(--transition-base);
}

.form-control:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
}

.form-text {
  display: block;
  margin-top: var(--spacing-sm);
  font-size: 0.875rem;
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