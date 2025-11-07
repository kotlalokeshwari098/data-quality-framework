<template>
  <div class="container-fluid login-container d-flex align-items-center justify-content-center bg-light">
    <div class="row w-100 justify-content-center">
      <div class="col-12 col-md-10 col-lg-8 col-xl-7 col-xxl-6">
        <LoginForm />
        <Copyright />
      </div>
    </div>
  </div>
</template>

<script setup>
import LoginForm from '../components/LoginForm.vue'
import Copyright from '../components/Copyright.vue'
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  padding: 1rem 0;
}

/* On larger screens, center vertically */
@media (min-width: 768px) {
  .login-container {
    height: 100vh;
  }
}

/* On smaller screens, allow natural height */
@media (max-width: 767px) {
  .login-container {
    min-height: 100vh;
    height: auto;
    padding: 2rem 0;
  }
}
</style>
