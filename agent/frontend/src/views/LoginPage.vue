<template>
  <div class="container-fluid vh-100 d-flex align-items-center justify-content-center bg-light">
    <div class="row w-100 justify-content-center">
      <div class="col-12 col-xl-10">
        <div class="card shadow-lg border-0 overflow-hidden mobile-card">
          <div class="row g-0 min-vh-75">
            <!-- Left side - Info panel (hidden on small screens) -->
            <div class="col-lg-6 d-none d-lg-flex">
              <div class="info-panel p-4 p-xl-5 d-flex flex-column justify-content-center w-100">
                <div>
                  <h1 class="display-5 fw-bold mb-3">Data Quality Agent</h1>
                  <p class="lead mb-4">Local Repository Data Quality Monitoring</p>

                  <div class="mb-4">
                    <div class="mb-4">
                      <h5 class="fw-semibold mb-2">Local Data Validation</h5>
                      <p class="text-light mb-0">Performs comprehensive data quality checks directly on your local repository or biobank data.</p>
                    </div>

                    <div class="mb-4">
                      <h5 class="fw-semibold mb-2">Automated Quality Reports</h5>
                      <p class="text-light mb-0">Generates detailed quality reports and automatically submits them to the central monitoring server.</p>
                    </div>

                    <div class="mb-4">
                      <h5 class="fw-semibold mb-2">CQL-Based Checks</h5>
                      <p class="text-light mb-0">Executes configurable Clinical Quality Language (CQL) checks to ensure data integrity and compliance.</p>
                    </div>
                  </div>

                  <div class="border-top border-secondary pt-4">
                    <div class="row text-center">
                      <div class="col-4">
                        <div class="h4 fw-bold mb-1">CQL</div>
                        <small class="text-uppercase text-light">Quality Checks</small>
                      </div>
                      <div class="col-4">
                        <div class="h4 fw-bold mb-1">Auto</div>
                        <small class="text-uppercase text-light">Reporting</small>
                      </div>
                      <div class="col-4">
                        <div class="h4 fw-bold mb-1">24/7</div>
                        <small class="text-uppercase text-light">Monitoring</small>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right side - Login form -->
            <div class="col-lg-6 col-12">
              <div class="p-3 p-sm-4 p-lg-5 d-flex flex-column justify-content-center h-100">
                <div class="w-100 login-form-container">
                  <!-- Mobile header (visible only on small screens) -->
                  <div class="d-lg-none text-center mb-4 mobile-header">
                    <div class="brand-icon-mobile mx-auto mb-3">
                      <i class="bi bi-bar-chart-fill"></i>
                    </div>
                    <h2 class="h4 fw-bold text-dark mb-2">Data Quality Agent</h2>
                    <p class="text-muted small">Local repository monitoring</p>
                  </div>

                  <div class="text-center mb-4">
                    <h1 class="h4 h-lg-3 fw-bold text-dark mb-2">Welcome Back</h1>
                    <p class="text-muted mb-0 small">Please sign in to your account</p>
                  </div>

                  <form @submit.prevent="login" class="login-form" novalidate>
                    <div class="mb-3">
                      <label for="username" class="form-label fw-semibold">Username</label>
                      <input
                        id="username"
                        v-model="username"
                        type="text"
                        class="form-control form-control-mobile"
                        :class="{ 'is-invalid': error && !loading }"
                        placeholder="Enter your username"
                        :disabled="loading"
                        required
                        autocomplete="username"
                      />
                    </div>

                    <div class="mb-4">
                      <label for="password" class="form-label fw-semibold">Password</label>
                      <input
                        id="password"
                        v-model="password"
                        type="password"
                        class="form-control form-control-mobile"
                        :class="{ 'is-invalid': error && !loading }"
                        placeholder="Enter your password"
                        :disabled="loading"
                        required
                        autocomplete="current-password"
                      />
                    </div>

                    <button
                      type="submit"
                      class="btn btn-primary w-100 py-3 fw-semibold btn-mobile"
                      :disabled="loading"
                    >
                      <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                      {{ loading ? 'Signing in...' : 'Sign In' }}
                    </button>

                    <div v-if="error" class="alert alert-danger mt-3 mb-0">
                      {{ error }}
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authenticate } from '../js/api.js'
import { useUserStore } from '../stores/userStore.js'

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const route = useRoute()
const router = useRouter()
const { updateDefaultPasswordStatus } = useUserStore()

async function login() {
  error.value = ''
  loading.value = true
  try {
    const loginResult = await authenticate(username.value, password.value)
    updateDefaultPasswordStatus(loginResult.defaultPassword)

    await router.replace((route.query.redirect && String(route.query.redirect)) || '/')
  } catch (e) {
    error.value = e?.message || 'Invalid username or password'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.min-vh-75 {
  min-height: 75vh;
}

.mobile-card {
  border-radius: 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

/* Info Panel - Gradient Background */
.info-panel {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.info-panel h1 {
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.info-panel .lead {
  color: rgba(255, 255, 255, 0.95);
}

.info-panel h5 {
  color: white;
}

.info-panel .text-light {
  color: rgba(255, 255, 255, 0.85) !important;
}

.info-panel .border-secondary {
  border-color: rgba(255, 255, 255, 0.2) !important;
}

/* Mobile Header */
.brand-icon-mobile {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2rem;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* Form Styling */
.login-form-container {
  max-width: 400px;
  margin: 0 auto;
}

.form-label {
  color: #374151;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.form-control,
.form-control-mobile {
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  transition: all 0.2s ease;
}

.form-control:focus,
.form-control-mobile:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Mobile Responsive */
@media (max-width: 991px) {
  .mobile-card {
    margin: 1rem;
  }

  .min-vh-75 {
    min-height: auto;
  }
}

@media (max-width: 768px) {
  .mobile-header {
    padding: 1rem 0;
  }

  .form-control-mobile {
    font-size: 16px;
    min-height: 48px;
  }

  .btn-mobile {
    min-height: 48px;
    font-size: 1rem;
  }
}

@media (max-width: 576px) {
  .mobile-card {
    margin: 0.5rem;
    border-radius: 0.75rem;
  }
}
</style>