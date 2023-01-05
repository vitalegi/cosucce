<template>
  <q-page class="row items-center justify-evenly">
    <q-card> Logged out </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { signOut } from 'boot/firebase';
signOut();
</script>
