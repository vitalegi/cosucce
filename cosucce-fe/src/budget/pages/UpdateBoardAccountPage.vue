<template>
  <q-page class="row items-center justify-evenly">
    <BoardAccountEditor
      :boardId="boardId"
      :id="accountId"
      :label="element?.label || ''"
      :icon="element?.icon || ''"
      :enabled="element?.enabled || false"
      @save="save"
    ></BoardAccountEditor>
  </q-page>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import budgetSyncService from 'src/budget/services/budget-sync';
import { useRoute, useRouter } from 'vue-router';
import { toAccountId, toBoardId } from 'src/budget/util/budget-route-params-util';
import BoardAccountEditor from 'src/budget/components/accounts/BoardAccountEditor.vue';
import routing from 'src/router/routing';
import BoardAccount from '../models/board-account';
import localDb from 'src/persistence/local-db';

const route = useRoute();
const router = useRouter();
void budgetSyncService.synchronize();

const boardId = ref<string>(toBoardId(route.params));
const accountId = ref<string>(toAccountId(route.params));

async function getElement(accountId: string): Promise<BoardAccount> {
  const elements = await localDb.boardAccounts.where('accountId').equals(accountId).toArray();
  console.log('load ', accountId, elements);
  if (elements.length === 0) {
    throw Error(`Account ${accountId} not found`);
  }
  return elements[0];
}

const element = ref<BoardAccount>();

void getElement(accountId.value).then((e) => (element.value = e));

async function save(): Promise<void> {
  await routing.budget().settingsViewAccounts(router, boardId.value);
}

watch(
  () => route.params,
  (newParams) => {
    boardId.value = toBoardId(newParams);
    accountId.value = toAccountId(newParams);
    void getElement(accountId.value).then((e) => (element.value = e));
  },
);
</script>
