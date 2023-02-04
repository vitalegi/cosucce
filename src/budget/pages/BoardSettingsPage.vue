<template>
  <q-page>
    <div class="q-pa-md row">
      <BoardMembersComponent :boardId="boardId" />
      <BoardSplitsComponent :boardId="boardId" />
      <BoardEntriesImportComponent :boardId="boardId" />
      <BoardDangerZoneComponent :boardId="boardId" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import BoardMembersComponent from 'src/budget/components/BoardMembersComponent.vue';
import BoardSplitsComponent from 'src/budget/components/BoardSplitsComponent.vue';
import BoardEntriesImportComponent from 'src/budget/components/BoardEntriesImportComponent.vue';
import BoardDangerZoneComponent from 'src/budget/components/BoardDangerZoneComponent.vue';

defineProps({
  boardId: {
    type: String,
    required: true,
  },
});
</script>
