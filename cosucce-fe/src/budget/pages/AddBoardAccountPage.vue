<template>
  <q-page class="row items-center justify-evenly">
    <BoardAccountEditor
      :boardId="boardId"
      label=""
      icon=""
      :enabled="true"
      @save="save"
    ></BoardAccountEditor>
  </q-page>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import budgetSyncService from 'src/budget/services/budget-sync';
import { useRoute, useRouter } from 'vue-router';
import { toBoardId } from 'src/budget/util/budget-route-params-util';
import BoardAccountEditor from 'src/budget/components/accounts/BoardAccountEditor.vue';
import routing from 'src/router/routing';

const route = useRoute();
const router = useRouter();
void budgetSyncService.synchronize();

const boardId = ref<string>(toBoardId(route.params));

async function save(): Promise<void> {
  await routing.budget().settingsViewAccounts(router, boardId.value);
}

watch(
  () => route.params,
  (newParams) => {
    boardId.value = toBoardId(newParams);
  },
);
</script>
