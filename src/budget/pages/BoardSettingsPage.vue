<template>
  <q-page>
    <div class="q-pa-md row">
      <BoardMembersComponent :boardId="boardId" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import { useRouter } from 'vue-router';
import BoardMembersComponent from 'src/budget/components/BoardMembersComponent.vue';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const router = useRouter();
</script>
