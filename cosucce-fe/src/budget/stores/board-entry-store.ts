import { defineStore, acceptHMRUpdate } from 'pinia';
import persistenceManager from 'src/persistence/persistence-manager';
import BoardEntry from 'src/budget/models/board-entry';

type Element = {
  entryId: string;
  boardId: string;
  date: string;
  accountId: string;
  categoryId: string;
  description: string;
  amount: string;
};
function create(data: Element): BoardEntry {
  const e = new BoardEntry();
  e.entryId = data.entryId;
  e.boardId = data.boardId;
  e.date = data.date;
  e.accountId = data.accountId;
  e.categoryId = data.categoryId;
  e.description = data.description;
  e.amount = data.amount;
  e.creationDate = new Date();
  e.lastUpdate = new Date();
  return e;
}

export const useBoardEntryStore = defineStore('boardEntry', {
  state: () => ({}),
  getters: {},
  actions: {
    async addBoardEntry(data: Element): Promise<void> {
      console.log(`add board entry ${JSON.stringify(data)}`);
      const e = create(data);
      const engine = persistenceManager.addBoardEntry();
      const changelogId = await engine.changeLocal('add', e);
      await engine.syncRemote(changelogId, true);
    },
    async updateBoardEntry(data: Element): Promise<void> {
      console.log(`update board entry ${JSON.stringify(data)}`);
      const e = create(data);
      const engine = persistenceManager.updateBoardEntry();
      const changelogId = await engine.changeLocal('update', e);
      await engine.syncRemote(changelogId, true);
    },
  },
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBoardEntryStore, import.meta.hot));
}
