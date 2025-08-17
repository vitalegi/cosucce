import { AxiosResponse } from 'axios';
import Board from '../budget/models/board';
import Changelog, { Action, EntityType } from '../models/changelog';
import localDb from './local-db';
import { AbstractChangelogFactory, ChangelogFactory } from './changelog/changelog-factory';
import LocalPersistence from './local/local-persistence';
import RemotePersistence from './remote/remote-persistence';
import { AxiosWrapperAuth } from 'src/services/authenticated-axios';

class BoardChangelogFactory extends AbstractChangelogFactory<Board> {
  protected override _entityId(entity: Board): string {
    return entity.boardId;
  }
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  protected override _entityType(entity: Board): EntityType {
    return 'board';
  }
}

export class AddBoardPersistence
  implements ChangelogFactory<Board>, LocalPersistence<Board>, RemotePersistence
{
  private _factory;
  private _axios;

  public constructor(axios: AxiosWrapperAuth) {
    this._factory = new BoardChangelogFactory();
    this._axios = axios;
  }

  async addChangelog(action: Action, entity: Board): Promise<Changelog> {
    return this._factory.addChangelog(action, entity);
  }

  async executeLocal(entity: Board): Promise<void> {
    await localDb.boards.add(entity);
  }
  async executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse> {
    return await this._axios.post('/budget/board', changelog.payload, {}, allowSSORedirect);
  }
}

export class UpdateBoardPersistence
  implements ChangelogFactory<Board>, LocalPersistence<Board>, RemotePersistence
{
  private _factory;
  private _axios;

  public constructor(axios: AxiosWrapperAuth) {
    this._factory = new BoardChangelogFactory();
    this._axios = axios;
  }

  async addChangelog(action: Action, entity: Board): Promise<Changelog> {
    return this._factory.addChangelog(action, entity);
  }

  async executeLocal(entity: Board): Promise<void> {
    await localDb.boards.put(entity);
  }
  async executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse> {
    return await this._axios.put(
      '/budget/board/' + changelog.payload.boardId,
      changelog.payload,
      {},
      allowSSORedirect,
    );
  }
}

export class DeleteBoardPersistence
  implements ChangelogFactory<Board>, LocalPersistence<Board>, RemotePersistence
{
  private _factory;
  private _axios;

  public constructor(axios: AxiosWrapperAuth) {
    this._factory = new BoardChangelogFactory();
    this._axios = axios;
  }

  async addChangelog(action: Action, entity: Board): Promise<Changelog> {
    return this._factory.addChangelog(action, entity);
  }

  async executeLocal(entity: Board): Promise<void> {
    await localDb.boards.delete(entity.boardId);
  }
  async executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse> {
    return await this._axios.delete(
      '/budget/board/' + changelog.payload.boardId,
      changelog.payload,
      allowSSORedirect,
    );
  }
}
