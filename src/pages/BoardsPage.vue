<template>
  <q-page>
    <div class="q-pa-md row">
      <div class="q-pa-xs col-xs-12 col-sm-4 col-md-3">
        <q-card @click="newBoard()">
          <q-card-section>
            <div class="text-h6">Crea nuova</div>
          </q-card-section>
        </q-card>
      </div>
      <div
        v-for="board in boards"
        :key="board.id"
        class="q-pa-xs col-xs-12 col-sm-4 col-md-3"
      >
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
import { ref } from 'vue';
import boardService from 'src/integrations/BoardService';
import Board from 'src/models/Board';
import { useRouter } from 'vue-router';

const router = useRouter();

const getBoards = async (): Promise<Board[]> => {
  return await boardService.getBoards();
};

const boards = ref(new Array<Board>());

getBoards().then((b) => boards.value.push(...b));

const goToBoard = (boardId: string): void => {
  router.push(`/board/${boardId}`);
};
const newBoard = async (): Promise<void> => {
  const board = await boardService.addBoard('La mia board');
  router.push(`/board/${board.id}`);
};
</script>
