<template>
  <q-page class="row items-center justify-evenly">
    <TableBoards @add="addBoard()" />
  </q-page>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import TableBoards from '../components/TableBoards.vue';
const router = useRouter();

function addBoard(): void {
  void router.push({
    path: '/budget/add-board',
  });
}
</script>
