<template>
  <q-page class="row items-center justify-evenly">
    <BoardEntriesTable :board-id="boardId" @add="addBoardEntry"></BoardEntriesTable>
  </q-page>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import { ref, watch } from 'vue';
import { toBoardId } from '../util/budget-route-params-util';
import BoardEntriesTable from '../components/BoardEntriesTable.vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const route = useRoute();

const boardId = ref<string>(toBoardId(route.params));

function addBoardEntry(): void {
  void router.push({
    path: `/budget/board/${boardId.value}/add-entry`,
  });
}
watch(
  () => route.params,
  (newParams) => {
    boardId.value = toBoardId(newParams);
  },
);
</script>
