import Dexie, { EntityTable, Table } from 'dexie';
import Board from '../budget/models/board';
import Changelog from '../models/changelog';

const localDb = new Dexie('cosucce') as Dexie & {
  changelogs: Table<Changelog, number>;
  boards: EntityTable<Board, 'boardId'>;
};
localDb.version(1).stores({
  changelogs: '++changelogId, entityType, action, entityId, status, creationDate, lastUpdate',
  boards: '&boardId, name, creationDate, lastUpdate',
});

export default localDb;
