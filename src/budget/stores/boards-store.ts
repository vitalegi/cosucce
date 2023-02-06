import { defineStore } from 'pinia';
import boardService from 'src/budget/integrations/BoardService';
import Board from 'src/budget/models/Board';
import spinner from 'src/utils/Spinner';
import { ref } from 'vue';

export const useBoardsStore = defineStore('boards', () => {
  const boards = ref(new Array<Board>());
  const update = async (): Promise<void> => {
    await spinner.sync(async () => {
      boards.value = await boardService.getBoards();
    });
  };
  update();
  return { boards, update };
});
