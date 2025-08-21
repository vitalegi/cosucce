import Dexie, { EntityTable, Table } from 'dexie';
import Board from 'src/budget/models/board';
import Changelog from 'src/models/changelog';
import BoardEntry from 'src/budget/models/board-entry';
import BoardAccount from 'src/budget/models/board-account';
import BoardCategory from 'src/budget/models/board-category';

const localDb = new Dexie('cosucce') as Dexie & {
  changelogs: Table<Changelog, number>;
  boards: EntityTable<Board, 'boardId'>;
  boardEntries: EntityTable<BoardEntry, 'entryId'>;
  boardAccounts: EntityTable<BoardAccount, 'accountId'>;
  boardCategories: EntityTable<BoardCategory, 'categoryId'>;
};
localDb.version(1).stores({
  changelogs: '++changelogId, entityType, action, entityId, status, creationDate, lastUpdate',
  boards: '&boardId, name, creationDate, lastUpdate',
  boardEntries:
    '&entryId, boardId, accountId, categoryId, description, amount, creationDate, lastUpdate',
  boardAccounts: '&accountId, boardId, label, icon, enabled, creationDate, lastUpdate',
  boardCategories: '&categoryId, boardId, label, icon, enabled, creationDate, lastUpdate',
});

export default localDb;
