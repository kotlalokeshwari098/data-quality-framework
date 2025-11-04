<template>
  <div class="container-fluid vh-100 d-flex align-items-center justify-content-center bg-light">
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
/* LoginView styles are minimal since layout is controlled by the container */
</style>
