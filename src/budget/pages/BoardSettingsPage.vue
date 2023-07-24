<template>
  <q-page>
    <div class="q-pa-md row">
      <BoardChangeNameComponent :board-id="boardId" />
      <BoardMembersComponent :boardId="boardId" />
      <BoardSplitsComponent :boardId="boardId" />
      <BoardEntriesImportComponent :boardId="boardId" />
      <BoardEntriesExportComponent :boardId="boardId" />
      <BoardDangerZoneComponent :boardId="boardId" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import BoardMembersComponent from 'src/budget/components/BoardMembersComponent.vue';
import BoardSplitsComponent from 'src/budget/components/BoardSplitsComponent.vue';
import BoardEntriesImportComponent from 'src/budget/components/BoardEntriesImportComponent.vue';
import BoardEntriesExportComponent from 'src/budget/components/BoardEntriesExportComponent.vue';
import BoardDangerZoneComponent from 'src/budget/components/BoardDangerZoneComponent.vue';
import BoardChangeNameComponent from 'src/budget/components/BoardChangeNameComponent.vue';

defineProps({
  boardId: {
    type: String,
    required: true,
  },
});
</script>
