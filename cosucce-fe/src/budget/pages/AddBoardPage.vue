<template>
  <q-page class="row items-center justify-evenly">
    <BoardEditor name="bbb" @save="save"></BoardEditor>
  </q-page>
</template>

<script setup lang="ts">
import BoardEditor from '../components/BoardEditor.vue';
import budgetSyncService from '../services/budget-sync';
import { useRouter } from 'vue-router';

const router = useRouter();

void budgetSyncService.synchronize();

async function save(): Promise<void> {
  await router.push({
    path: '/budget',
  });
}
</script>
