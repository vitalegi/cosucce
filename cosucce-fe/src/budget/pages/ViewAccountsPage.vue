<template>
  <q-page class="row items-center justify-evenly">
    <BoardAccountsTable :boardId="boardId" @add="addAccount" />
  </q-page>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import BoardAccountsTable from 'src/budget/components/accounts/BoardAccountsTable.vue';
import { ref, watch } from 'vue';
import { toBoardId } from '../util/budget-route-params-util';
import routing from 'src/router/routing';

const route = useRoute();
const router = useRouter();

const boardId = ref<string>(toBoardId(route.params));

function addAccount(): Promise<unknown> {
  return routing.budget().settingsAddAccount(router, boardId.value);
}

watch(
  () => route.params,
  (newParams) => {
    boardId.value = toBoardId(newParams);
  },
);
</script>
