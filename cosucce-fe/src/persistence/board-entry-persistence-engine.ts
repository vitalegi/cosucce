import { AxiosResponse } from 'axios';
import Changelog, { Action, EntityType } from '../models/changelog';
import localDb from './local-db';
import { AbstractChangelogFactory, ChangelogFactory } from './changelog/changelog-factory';
import LocalPersistence from './local/local-persistence';
import RemotePersistence from './remote/remote-persistence';
import { AxiosWrapperAuth } from 'src/services/authenticated-axios';
import BoardEntry from 'src/budget/models/board-entry';

class BoardEntryChangelogFactory extends AbstractChangelogFactory<BoardEntry> {
  protected override _entityId(entity: BoardEntry): string {
    return entity.entryId;
  }
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  protected override _entityType(entity: BoardEntry): EntityType {
    return 'board-entry';
  }

  protected override async _oldETag(entityId: string): Promise<string | undefined> {
    const e = await localDb.boardEntries.get(entityId);
    if (e === undefined) {
      return undefined;
    }
    return BoardEntry.hash(e);
  }
  protected override _newETag(entity: BoardEntry): string {
    return BoardEntry.hash(entity);
  }
  protected override _applyETag(entity: BoardEntry, etag: string): void {
    entity.etag = etag;
  }
}

export class AddBoardEntryPersistence
  implements ChangelogFactory<BoardEntry>, LocalPersistence<BoardEntry>, RemotePersistence
{
  private _factory;
  private _axios;

  public constructor(axios: AxiosWrapperAuth) {
    this._factory = new BoardEntryChangelogFactory();
    this._axios = axios;
  }

  async addChangelog(action: Action, entity: BoardEntry): Promise<Changelog> {
    return this._factory.addChangelog(action, entity);
  }

  async executeLocal(entity: BoardEntry): Promise<void> {
    await localDb.boardEntries.add(entity);
  }
  async executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse> {
    const entity = changelog.payload;
    return await this._axios.post(
      `/budget/board/${entity.boardId}/entry`,
      {
        entryId: entity.entryId,
        accountId: entity.accountId,
        categoryId: entity.categoryId,
        description: entity.description,
        amount: entity.amount,
        etag: changelog.newETag,
      },
      {},
      allowSSORedirect,
    );
  }
}

export class UpdateBoardEntryPersistence
  implements ChangelogFactory<BoardEntry>, LocalPersistence<BoardEntry>, RemotePersistence
{
  private _factory;
  private _axios;

  public constructor(axios: AxiosWrapperAuth) {
    this._factory = new BoardEntryChangelogFactory();
    this._axios = axios;
  }

  async addChangelog(action: Action, entity: BoardEntry): Promise<Changelog> {
    return this._factory.addChangelog(action, entity);
  }

  async executeLocal(entity: BoardEntry): Promise<void> {
    await localDb.boardEntries.put(entity);
  }
  async executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse> {
    const entity = changelog.payload;
    return await this._axios.put(
      `/budget/board/${entity.boardId}/entry/${entity.entryId}`,
      {
        entryId: entity.entryId,
        accountId: entity.accountId,
        categoryId: entity.categoryId,
        description: entity.description,
        amount: entity.amount,
        etag: changelog.oldETag,
        newETag: changelog.newETag,
      },
      {},
      allowSSORedirect,
    );
  }
}

export class DeleteBoardEntryPersistence
  implements ChangelogFactory<BoardEntry>, LocalPersistence<BoardEntry>, RemotePersistence
{
  private _factory;
  private _axios;

  public constructor(axios: AxiosWrapperAuth) {
    this._factory = new BoardEntryChangelogFactory();
    this._axios = axios;
  }

  async addChangelog(action: Action, entity: BoardEntry): Promise<Changelog> {
    return this._factory.addChangelog(action, entity);
  }

  async executeLocal(entity: BoardEntry): Promise<void> {
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
