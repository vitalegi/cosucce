import { AxiosResponse } from 'axios';
import Changelog, { Action, EntityType } from 'src/models/changelog';
import localDb from './local-db';
import { AbstractChangelogFactory, ChangelogFactory } from './changelog/changelog-factory';
import LocalPersistence from './local/local-persistence';
import RemotePersistence from './remote/remote-persistence';
import { AxiosWrapperAuth } from 'src/services/authenticated-axios';
import BoardAccount from 'src/budget/models/board-account';

class BoardAccountChangelogFactory extends AbstractChangelogFactory<BoardAccount> {
  protected override _entityId(entity: BoardAccount): string {
    return entity.accountId;
  }
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  protected override _entityType(entity: BoardAccount): EntityType {
    return 'board-account';
  }

  protected override async _oldETag(entityId: string): Promise<string | undefined> {
    const e = await localDb.boardAccounts.get(entityId);
    if (e === undefined) {
      return undefined;
    }
    return BoardAccount.hash(e);
  }
  protected override _newETag(entity: BoardAccount): string {
    return BoardAccount.hash(entity);
  }
  protected override _applyETag(entity: BoardAccount, etag: string): void {
    entity.etag = etag;
  }
}

abstract class AbstractBoardAccountPersistence implements ChangelogFactory<BoardAccount> {
  private _factory;
  protected _axios;

  public constructor(axios: AxiosWrapperAuth) {
    this._factory = new BoardAccountChangelogFactory();
    this._axios = axios;
  }

  async addChangelog(action: Action, entity: BoardAccount): Promise<Changelog> {
    return this._factory.addChangelog(action, entity);
  }
}

export class AddBoardAccountPersistence
  extends AbstractBoardAccountPersistence
  implements LocalPersistence<BoardAccount>, RemotePersistence
{
  public constructor(axios: AxiosWrapperAuth) {
    super(axios);
  }

  async executeLocal(entity: BoardAccount): Promise<void> {
    await localDb.boardAccounts.add(entity);
  }
  async executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse> {
    const entity = changelog.payload;
    return await this._axios.post(
      `/budget/board/${entity.boardId}/account`,
      {
        accountId: entity.accountId,
        label: entity.label,
        icon: entity.icon,
        enabled: entity.enabled,
        etag: changelog.newETag,
      },
      {},
      allowSSORedirect,
    );
  }
}

export class UpdateBoardAccountPersistence
  extends AbstractBoardAccountPersistence
  implements LocalPersistence<BoardAccount>, RemotePersistence
{
  public constructor(axios: AxiosWrapperAuth) {
    super(axios);
  }
  async executeLocal(entity: BoardAccount): Promise<void> {
    await localDb.boardAccounts.put(entity);
  }
  async executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse> {
    const entity = changelog.payload;
    return await this._axios.put(
      `/budget/board/${entity.boardId}/account/${entity.accountId}`,
      {
        label: entity.label,
        icon: entity.icon,
        enabled: entity.enabled,
        etag: changelog.oldETag,
        newETag: changelog.newETag,
      },
      {},
      allowSSORedirect,
    );
  }
}

export class DeleteBoardAccountPersistence
  extends AbstractBoardAccountPersistence
  implements LocalPersistence<BoardAccount>, RemotePersistence
{
  public constructor(axios: AxiosWrapperAuth) {
    super(axios);
  }

  async executeLocal(entity: BoardAccount): Promise<void> {
    await localDb.boards.delete(entity.boardId);
  }
  async executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse> {
    return await this._axios.delete(
      `/budget/board/${changelog.payload.boardId}/account/${changelog.payload.entryId}`,
      {},
      allowSSORedirect,
    );
  }
}
