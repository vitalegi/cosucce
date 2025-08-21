<template>
  <q-page class="row items-center justify-evenly">
    <BoardEditor name="" @save="save"></BoardEditor>
  </q-page>
</template>

<script setup lang="ts">
import budgetSyncService from 'src/budget/services/budget-sync';
import { useRouter } from 'vue-router';
import BoardEditor from 'src/budget/components/boards/BoardEditor.vue';

const router = useRouter();

void budgetSyncService.synchronize();

async function save(): Promise<void> {
  await router.push({
    path: '/budget',
  });
}
</script>
