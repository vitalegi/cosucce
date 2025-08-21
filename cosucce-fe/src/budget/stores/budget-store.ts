import { defineStore, acceptHMRUpdate } from 'pinia';
import persistenceManager from 'src/persistence/persistence-manager';
import BoardEntry from 'src/budget/models/board-entry';
import Board from 'src/budget/models/board';
import PersistenceEngine from 'src/persistence/persistence-engine';
import { Action, EntityType } from 'src/models/changelog';
import BoardAccount from 'src/budget/models/board-account';
import BoardCategory from 'src/budget/models/board-category';

type BoardElement = {
  boardId: string;
  name: string;
};

type BoardEntryElement = {
  entryId: string;
  boardId: string;
  date: string;
  accountId: string;
  categoryId: string;
  description: string;
  amount: string;
};

type BoardAccountElement = {
  accountId: string;
  boardId: string;
  label: string;
  icon: string;
  enabled: boolean;
};

type BoardCategoryElement = {
  categoryId: string;
  boardId: string;
  label: string;
  icon: string;
  enabled: boolean;
};

function createBoardEntry(data: BoardEntryElement): BoardEntry {
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

function createBoard(data: BoardElement): Board {
  const e = new Board();
  e.boardId = data.boardId;
  e.name = data.name;
  return e;
}

function createBoardAccount(data: BoardAccountElement): BoardAccount {
  const e = new BoardAccount();
  e.accountId = data.accountId;
  e.boardId = data.boardId;
  e.label = data.label;
  e.icon = data.icon;
  e.enabled = data.enabled;
  e.creationDate = new Date();
  e.lastUpdate = new Date();
  return e;
}
function createBoardCategory(data: BoardCategoryElement): BoardCategory {
  const e = new BoardCategory();
  e.categoryId = data.categoryId;
  e.boardId = data.boardId;
  e.label = data.label;
  e.icon = data.icon;
  e.enabled = data.enabled;
  e.creationDate = new Date();
  e.lastUpdate = new Date();
  return e;
}

async function execute<E, T>(
  entityType: EntityType,
  action: Action,
  transformer: (e: E) => T,
  engine: PersistenceEngine<T>,
  data: E,
): Promise<void> {
  console.log(`${action} ${entityType} - ${JSON.stringify(data)}`);
  const e = transformer(data);
  const changelogId = await engine.changeLocal(action, e);
  await engine.syncRemote(changelogId, true);
}

export const useBudgetStore = defineStore('budget', {
  state: () => ({}),
  getters: {},
  actions: {
    async addBoard(data: BoardElement): Promise<void> {
      await execute<BoardElement, Board>(
        'board',
        'add',
        (e) => createBoard(e),
        persistenceManager.addBoard(),
        data,
      );
    },
    async updateBoard(data: BoardElement): Promise<void> {
      await execute<BoardElement, Board>(
        'board',
        'update',
        (e) => createBoard(e),
        persistenceManager.updateBoard(),
        data,
      );
    },

    async addBoardEntry(data: BoardEntryElement): Promise<void> {
      await execute<BoardEntryElement, BoardEntry>(
        'board-entry',
        'add',
        (e) => createBoardEntry(e),
        persistenceManager.addBoardEntry(),
        data,
      );
    },
    async updateBoardEntry(data: BoardEntryElement): Promise<void> {
      await execute<BoardEntryElement, BoardEntry>(
        'board-entry',
        'update',
        (e) => createBoardEntry(e),
        persistenceManager.updateBoardEntry(),
        data,
      );
    },

    async addBoardAccount(data: BoardAccountElement): Promise<void> {
      await execute<BoardAccountElement, BoardAccount>(
        'board-account',
        'add',
        (e) => createBoardAccount(e),
        persistenceManager.addBoardAccount(),
        data,
      );
    },
    async updateBoardAccount(data: BoardAccountElement): Promise<void> {
      await execute<BoardAccountElement, BoardAccount>(
        'board-account',
        'update',
        (e) => createBoardAccount(e),
        persistenceManager.updateBoardAccount(),
        data,
      );
    },

    async addBoardCategory(data: BoardCategoryElement): Promise<void> {
      await execute<BoardCategoryElement, BoardCategory>(
        'board-category',
        'add',
        (e) => createBoardCategory(e),
        persistenceManager.addBoardCategory(),
        data,
      );
    },
    async updateBoardCategory(data: BoardCategoryElement): Promise<void> {
      await execute<BoardCategoryElement, BoardCategory>(
        'board-category',
        'update',
        (e) => createBoardCategory(e),
        persistenceManager.updateBoardCategory(),
        data,
      );
    },
  },
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBudgetStore, import.meta.hot));
}
