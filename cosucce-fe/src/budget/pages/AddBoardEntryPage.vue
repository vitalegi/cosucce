<template>
  <q-page class="row items-center justify-evenly">
    <BoardEntryEditor
      :boardId="boardId"
      date=""
      accountId=""
      categoryId=""
      description=""
      amount=""
      @save="save"
    ></BoardEntryEditor>
  </q-page>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import BoardEntryEditor from '../components/BoardEntryEditor.vue';
import budgetSyncService from '../services/budget-sync';
import { useRoute, useRouter } from 'vue-router';
import { toBoardId } from '../util/budget-route-params-util';

const router = useRouter();
const route = useRoute();

void budgetSyncService.synchronize();

async function save(): Promise<void> {
  await router.push({
    path: '/budget',
  });
}
const boardId = ref<string>(toBoardId(route.params));

watch(
  () => route.params,
  (newParams) => {
    boardId.value = toBoardId(newParams);
  },
);
</script>
