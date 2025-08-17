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

export class PersistenceManager {
  private _addBoard;
  private _updateBoard;
  private _deleteBoard;

  public constructor(authenticatedApi: AxiosWrapperAuth) {
    this._addBoard = new PersistenceEngine(new AddBoardPersistence(authenticatedApi));
    this._updateBoard = new PersistenceEngine(new UpdateBoardPersistence(authenticatedApi));
    this._deleteBoard = new PersistenceEngine(new DeleteBoardPersistence(authenticatedApi));
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

  public getPersistenceEngine(action: Action, entityType: EntityType): PersistenceEngine<unknown> {
    switch (entityType) {
      case 'board':
        switch (action) {
          case 'add':
            return this.addBoard();
          case 'update':
            return this.updateBoard();
          case 'delete':
            return this.deleteBoard();
        }
    }
    //throw Error(`Pair not supported, action: ${action}, entityType: ${entityType}`);
  }
}

const persistenceManager = new PersistenceManager(authenticatedApi);
export default persistenceManager;
