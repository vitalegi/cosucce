import Dexie, { EntityTable, Table } from 'dexie';
import Board from 'src/budget/models/board';
import Changelog from 'src/models/changelog';
import BoardEntry from 'src/budget/models/board-entry';

const localDb = new Dexie('cosucce') as Dexie & {
  changelogs: Table<Changelog, number>;
  boards: EntityTable<Board, 'boardId'>;
  boardEntries: EntityTable<BoardEntry, 'entryId'>;
};
localDb.version(1).stores({
  changelogs: '++changelogId, entityType, action, entityId, status, creationDate, lastUpdate',
  boards: '&boardId, name, creationDate, lastUpdate',
  boardEntries:
    '&entryId, boardId, accountId, categoryId, description, amount, creationDate, lastUpdate',
});

export default localDb;
