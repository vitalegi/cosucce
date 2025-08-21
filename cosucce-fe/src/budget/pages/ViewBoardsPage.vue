<template>
  <q-page class="row items-center justify-evenly">
    <BoardsTable @add="addBoard" />
  </q-page>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import BoardsTable from 'src/budget/components/boards/BoardsTable.vue';
const router = useRouter();

function addBoard(): void {
  void router.push({
    path: '/budget/add-board',
  });
}
</script>
