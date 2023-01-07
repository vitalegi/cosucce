<template>
  <div class="q-pa-md">View board {{ boardId }}</div>
</template>

<script setup lang="ts">
import { getAuth, signInWithEmailAndPassword } from 'firebase/auth';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

defineProps<string>(),
  {
    boardId: '',
  };
</script>
