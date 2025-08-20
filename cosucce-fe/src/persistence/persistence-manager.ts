import PersistenceEngine from './persistence-engine';
import {
  AddBoardPersistence,
  DeleteBoardPersistence,
  UpdateBoardPersistence,
} from './board-persistence-engine';
import Board from 'src/budget/models/board';
import { authenticatedApi } from 'src/services/backend-service';
import { AxiosWrapperAuth } from 'src/services/authenticated-axios';
import { Action, EntityType } from 'src/models/changelog';
import {
  AddBoardEntryPersistence,
  DeleteBoardEntryPersistence,
  UpdateBoardEntryPersistence,
} from './board-entry-persistence-engine';
import BoardEntry from 'src/budget/models/board-entry';

export class PersistenceManager {
  private _addBoard;
  private _updateBoard;
  private _deleteBoard;
  private _addBoardEntry;
  private _updateBoardEntry;
  private _deleteBoardEntry;

  public constructor(authenticatedApi: AxiosWrapperAuth) {
    this._addBoard = new PersistenceEngine(new AddBoardPersistence(authenticatedApi));
    this._updateBoard = new PersistenceEngine(new UpdateBoardPersistence(authenticatedApi));
    this._deleteBoard = new PersistenceEngine(new DeleteBoardPersistence(authenticatedApi));

    this._addBoardEntry = new PersistenceEngine(new AddBoardEntryPersistence(authenticatedApi));
    this._updateBoardEntry = new PersistenceEngine(
      new UpdateBoardEntryPersistence(authenticatedApi),
    );
    this._deleteBoardEntry = new PersistenceEngine(
      new DeleteBoardEntryPersistence(authenticatedApi),
    );
  }

  public addBoard(): PersistenceEngine<Board> {
    return this._addBoard;
  }
  public updateBoard(): PersistenceEngine<Board> {
    return this._updateBoard;
  }
  public deleteBoard(): PersistenceEngine<Board> {
    return this._deleteBoard;
  }

  public addBoardEntry(): PersistenceEngine<BoardEntry> {
    return this._addBoardEntry;
  }
  public updateBoardEntry(): PersistenceEngine<BoardEntry> {
    return this._updateBoardEntry;
  }
  public deleteBoardEntry(): PersistenceEngine<BoardEntry> {
    return this._deleteBoardEntry;
  }

  public getPersistenceEngine(action: Action, entityType: EntityType): PersistenceEngine<unknown> {
    switch (entityType) {
      case 'board':
        switch (action) {
          case 'add':
            return this._addBoard;
          case 'update':
            return this._updateBoard;
          case 'delete':
            return this._deleteBoard;
          default:
            throw Error(`Pair not supported, action: ${action}, entityType: ${entityType}`);
        }
      case 'board-entry':
        switch (action) {
          case 'add':
            return this._addBoardEntry;
          case 'update':
            return this._updateBoardEntry;
          case 'delete':
            return this._deleteBoardEntry;
        }
    }
    throw Error(`Pair not supported, action: ${action}, entityType: ${entityType}`);
  }
}

const persistenceManager = new PersistenceManager(authenticatedApi);
export default persistenceManager;
