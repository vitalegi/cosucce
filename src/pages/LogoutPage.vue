<template>
  <q-page>
    <div class="q-pa-md row">Logout in corso...</div>
  </q-page>
</template>

<script setup lang="ts">
import { signOut } from 'boot/firebase';
import { useRouter } from 'vue-router';

const router = useRouter();

signOut().then(() => {
  router.push('/');
});
</script>
