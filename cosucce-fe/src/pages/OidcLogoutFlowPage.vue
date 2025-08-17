<template>
  <q-page class="q-gutter-xs items-center justify-evenly"> Logout... </q-page>
</template>

<script setup lang="ts">
import { authService } from 'src/services/backend-service';
import { useRouter } from 'vue-router';

const router = useRouter();

async function doLogout() {
  await authService.logout();
  void router.push({ path: '/' });
}

void doLogout();
</script>
