/* eslint-disable @typescript-eslint/no-unused-vars */
import Changelog, { Action, ChangelogStatus } from 'src/models/changelog';
import { ChangelogFactory } from './changelog/changelog-factory';
import LocalPersistence from './local/local-persistence';
import localDb from './local-db';
import { isAxiosError, AxiosError, AxiosResponse } from 'axios';
import RemotePersistence from './remote/remote-persistence';

export default class PersistenceEngine<E> {
  private changelogFactory: ChangelogFactory<E>;
  private localPersistence;
  private remotePersistence;

  public constructor(api: ChangelogFactory<E> & LocalPersistence<E> & RemotePersistence) {
    this.changelogFactory = api;
    this.localPersistence = api;
    this.remotePersistence = api;
  }

  public async changeLocal(action: Action, element: E): Promise<number> {
    console.log(`Execute ${action} on ${JSON.stringify(element)}`);
    const changelog = await this.changelogFactory.addChangelog(action, element);
    await this.localPersistence.executeLocal(element);
    const changelogId = changelog.changelogId;
    if (changelogId === undefined) {
      throw new Error(
        `Changelog (${changelog.entityType}, ${changelog.entityId}, ${changelog.creationDate}) without an ID`,
      );
    }
    return changelogId;
  }

  public async syncRemote(
    changelogId: number,
    allowSSORedirect: boolean,
  ): Promise<ChangelogStatus> {
    const changelog = await this.getChangelog(changelogId);
    await localDb.changelogs.update(changelogId, { status: 'ongoing', lastUpdate: new Date() });

    try {
      const response = await this.remotePersistence.executeRemote(changelog, allowSSORedirect);
      return await this.remoteSuccess(changelogId, response);
    } catch (e) {
      if (!isAxiosError(e)) {
        return await this.remoteUnknownError(changelogId, e);
      }
      const err = e as AxiosError;
      switch (err.status) {
        case 401:
          return await this.remoteError401(changelogId, err);
        case 403:
          return await this.remoteError403(changelogId, err);
        case 409:
          return await this.remoteError409(changelogId, err);
        case 500:
          return await this.remoteError500(changelogId, err);
      }
      return await this.remoteErrorXXX(changelogId, err);
    }
    //return await localDb.transaction('rw', localDb.changelogs, async () => {});
  }

  protected async remoteSuccess(
    changelogId: number,
    response: AxiosResponse,
  ): Promise<ChangelogStatus> {
    console.log(`Changelog ${changelogId} applied successfully`);
    await localDb.changelogs.update(changelogId, { status: 'done', lastUpdate: new Date() });
    return 'done';
  }

  protected async remoteUnknownError(changelogId: number, e: unknown): Promise<ChangelogStatus> {
    console.error('Unknown error, try again later', e);
    await localDb.changelogs.update(changelogId, {
      status: 'generic-error',
      lastUpdate: new Date(),
    });
    return 'generic-error';
  }

  protected async remoteError401(changelogId: number, e: AxiosError): Promise<ChangelogStatus> {
    console.log(`User not authenticated, try again later`);
    await localDb.changelogs.update(changelogId, {
      status: 'unauthorized',
      lastUpdate: new Date(),
    });
    return 'unauthorized';
  }
  protected async remoteError403(changelogId: number, e: AxiosError): Promise<ChangelogStatus> {
    console.log(`User not authorized, try again later`);
    await localDb.changelogs.update(changelogId, { status: 'forbidden', lastUpdate: new Date() });
    return 'forbidden';
  }

  protected async remoteError409(changelogId: number, e: AxiosError): Promise<ChangelogStatus> {
    console.log(
      `Version mismatch, you have a stale version of ${JSON.stringify(e.response?.data)}`,
    );
    await localDb.changelogs.update(changelogId, {
      status: 'conflict',
      lastUpdate: new Date(),
    });
    return 'conflict';
  }
  protected async remoteError500(changelogId: number, e: AxiosError): Promise<ChangelogStatus> {
    await localDb.changelogs.update(changelogId, {
      status: 'generic-error',
      lastUpdate: new Date(),
    });
    console.log(`Backend error, try again later. Response: ${JSON.stringify(e.response?.data)}`);
    return 'generic-error';
  }

  protected async remoteErrorXXX(changelogId: number, e: AxiosError): Promise<ChangelogStatus> {
    await localDb.changelogs.update(changelogId, {
      status: 'generic-error',
      lastUpdate: new Date(),
    });
    console.log(`Generic error: ${JSON.stringify(e.response?.data)}`);
    return 'generic-error';
  }

  protected async getChangelog(changelogId: number): Promise<Changelog> {
    const changelog = await localDb.changelogs.get(changelogId);
    if (changelog === undefined) {
      throw new Error(`Changelog ${changelogId} not found`);
    }
    return changelog;
  }
}
