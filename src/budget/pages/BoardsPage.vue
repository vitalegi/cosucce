<template>
  <q-page>
    <div class="q-pa-md row">
      <div class="q-gutter-sm q-pa-xs col-12 row">
        <q-space />
        <q-btn flat color="primary" @click="newBoard()">New</q-btn>
        <q-btn flat color="primary" @click="joinBoard()"> Join </q-btn>
      </div>
      <div v-for="board in boards" :key="board.id" class="q-pa-xs col-xs-12 col-sm-4 col-md-3">
        <q-card @click="goToBoard(board.id)">
          <q-card-section>
            <div class="text-h6">{{ board.name }}</div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import boardService from 'src/budget/integrations/BoardService';
import { useRouter } from 'vue-router';
import { useBoardsStore } from 'src/budget/stores/boards-store';
import { computed } from 'vue';
import spinner from 'src/utils/Spinner';

const router = useRouter();
const boardsStore = useBoardsStore();
const boards = computed(() => boardsStore.boards);

const goToBoard = (boardId: string): void => {
  router.push(`/board/${boardId}`);
};
const newBoard = async (): Promise<void> => {
  await spinner.sync(async () => {
    const board = await boardService.addBoard('La mia board');
    await boardsStore.update();
    router.push(`/board/${board.id}`);
  });
};

const joinBoard = async (): Promise<void> => {
  router.push('/board/join');
};
</script>
