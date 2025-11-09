<template>
  <div class="container-fluid vh-100 d-flex align-items-center justify-content-center bg-light">
    <div class="row w-100 justify-content-center">
      <div class="col-12 col-xl-10">
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
/* Rely on Bootstrap layout to control spacing and height like agent does */
</style>
