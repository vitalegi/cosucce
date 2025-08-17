import { defineStore, acceptHMRUpdate } from 'pinia';
import Board from '../models/board';
import persistenceManager from 'src/persistence/persistence-manager';

export const useBoardStore = defineStore('board', {
  state: () => ({}),
  getters: {},
  actions: {
    async addBoard(id: string, name: string): Promise<void> {
      console.log(`add board ${name}`);
      const board = new Board(id, name);
      const engine = persistenceManager.addBoard();
      const changelogId = await engine.changeLocal('add', board);
      await engine.syncRemote(changelogId, true);
    },
    async updateBoard(id: string, name: string): Promise<void> {
      console.log(`update board ${id} ${name}`);
      const board = new Board(id, name);
      const engine = persistenceManager.updateBoard();
      const changelogId = await engine.changeLocal('update', board);
      await engine.syncRemote(changelogId, true);
    },
  },
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBoardStore, import.meta.hot));
}
