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
import {
  AddBoardAccountPersistence,
  UpdateBoardAccountPersistence,
  DeleteBoardAccountPersistence,
} from 'src/persistence/board-account-persistence-engine';
import {
  AddBoardCategoryPersistence,
  UpdateBoardCategoryPersistence,
  DeleteBoardCategoryPersistence,
} from 'src/persistence/board-category-persistence-engine';
import BoardCategory from 'src/budget/models/board-category';
import BoardAccount from 'src/budget/models/board-account';

export class PersistenceManager {
  private _persistences: { a: Action; t: EntityType; e: PersistenceEngine<unknown> }[];

  private _addBoard;
  private _updateBoard;
  private _deleteBoard;

  private _addBoardEntry;
  private _updateBoardEntry;
  private _deleteBoardEntry;

  private _addBoardAccount;
  private _updateBoardAccount;
  private _deleteBoardAccount;

  private _addBoardCategory;
  private _updateBoardCategory;
  private _deleteBoardCategory;

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

    this._addBoardAccount = new PersistenceEngine(new AddBoardAccountPersistence(authenticatedApi));
    this._updateBoardAccount = new PersistenceEngine(
      new UpdateBoardAccountPersistence(authenticatedApi),
    );
    this._deleteBoardAccount = new PersistenceEngine(
      new DeleteBoardAccountPersistence(authenticatedApi),
    );

    this._addBoardCategory = new PersistenceEngine(
      new AddBoardCategoryPersistence(authenticatedApi),
    );
    this._updateBoardCategory = new PersistenceEngine(
      new UpdateBoardCategoryPersistence(authenticatedApi),
    );
    this._deleteBoardCategory = new PersistenceEngine(
      new DeleteBoardCategoryPersistence(authenticatedApi),
    );

    this._persistences = [
      { a: 'add', t: 'board', e: this._addBoard },
      { a: 'update', t: 'board', e: this._updateBoard },
      { a: 'delete', t: 'board', e: this._deleteBoard },

      { a: 'add', t: 'board-entry', e: this._addBoardEntry },
      { a: 'update', t: 'board-entry', e: this._updateBoardEntry },
      { a: 'delete', t: 'board-entry', e: this._deleteBoardEntry },

      { a: 'add', t: 'board-account', e: this._addBoardAccount },
      { a: 'update', t: 'board-account', e: this._updateBoardAccount },
      { a: 'delete', t: 'board-account', e: this._deleteBoardAccount },

      { a: 'add', t: 'board-category', e: this._addBoardCategory },
      { a: 'update', t: 'board-category', e: this._updateBoardCategory },
      { a: 'delete', t: 'board-category', e: this._deleteBoardCategory },
    ];
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

  public addBoardAccount(): PersistenceEngine<BoardAccount> {
    return this._addBoardAccount;
  }
  public updateBoardAccount(): PersistenceEngine<BoardAccount> {
    return this._updateBoardAccount;
  }
  public deleteBoardAccount(): PersistenceEngine<BoardAccount> {
    return this._deleteBoardAccount;
  }

  public addBoardCategory(): PersistenceEngine<BoardCategory> {
    return this._addBoardCategory;
  }
  public updateBoardCategory(): PersistenceEngine<BoardCategory> {
    return this._updateBoardCategory;
  }
  public deleteBoardCategory(): PersistenceEngine<BoardCategory> {
    return this._deleteBoardCategory;
  }

  public getPersistenceEngine(action: Action, entityType: EntityType): PersistenceEngine<unknown> {
    const matches = this._persistences
      .filter((p) => p.a === action)
      .filter((p) => p.t === entityType);
    if (matches.length > 0) {
      return matches[0].e;
    }
    throw Error(`Pair not supported, action: ${action}, entityType: ${entityType}`);
  }
}

const persistenceManager = new PersistenceManager(authenticatedApi);
export default persistenceManager;
