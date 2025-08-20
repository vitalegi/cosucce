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

  protected override async _oldETag(entityId: string): Promise<string | undefined> {
    const e = await localDb.boards.get(entityId);
    if (e === undefined) {
      return undefined;
    }
    return Board.hash(e);
  }
  protected override _newETag(entity: Board): string {
    return Board.hash(entity);
  }
  protected override _applyETag(entity: Board, etag: string): void {
    entity.etag = etag;
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
    const entity = changelog.payload;
    return await this._axios.post(
      '/budget/board',
      { boardId: entity.boardId, name: entity.name, etag: changelog.newETag },
      {},
      allowSSORedirect,
    );
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
    const entity = changelog.payload;
    return await this._axios.put(
      '/budget/board/' + changelog.payload.boardId,
      {
        boardId: entity.boardId,
        name: entity.name,
        etag: changelog.oldETag,
        newETag: changelog.newETag,
      },
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
      {},
      allowSSORedirect,
    );
  }
}
