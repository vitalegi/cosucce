import { defineStore, acceptHMRUpdate } from 'pinia';
import Board from '../models/board';
import backendService from 'src/services/backend-service';

export const useBoardStore = defineStore('board', {
  state: () => ({
    board: new Array<Board>(),
  }),
  getters: {},
  actions: {
    async addBoard(name: string): Promise<Board> {
      console.log(`add board ${name}`);
      return await backendService.boardResource().add(name);
    },
    updateBoard(id: string, name: string): Board {
      const board = new Board();
      board.boardId = id;
      board.name = name;
      console.log(`update board ${name}`);
      return board;
    },
  },
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBoardStore, import.meta.hot));
}
