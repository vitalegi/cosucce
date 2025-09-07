<template>
  <q-page class="row items-center justify-evenly">
    <BoardsTable @add="addBoard" />
  </q-page>
</template>

<script setup lang="ts">
import BoardsTable from 'src/budget/components/boards/BoardsTable.vue';
import routing from 'src/router/routing';
import { useRouter } from 'vue-router';

const router = useRouter();

function addBoard(): Promise<unknown> {
  return routing.budget().settingsAddAccount(router, '');
}
</script>
