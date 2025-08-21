import Changelog, { ChangelogStatus } from 'src/models/changelog';
import localDb from 'src/persistence/local-db';
import persistenceManager from 'src/persistence/persistence-manager';
import backendService from 'src/services/backend-service';
import Board from 'src/budget/models/board';

function ts() {
  return Date.now();
}

/**
 * Returns all the elements of set1 that aren't in set2
 * @param set1
 * @param set2
 */
function subtract(set1: string[], set2: string[]): string[] {
  const map = new Map<string, boolean>();
  for (const e of set1) {
    map.set(e, true);
  }
  for (const e of set2) {
    if (map.has(e)) {
      map.set(e, false);
    }
  }
  return Array.from(map.entries())
    .filter((e: [string, boolean]) => e[1])
    .map((e: [string, boolean]) => e[0]);
}

abstract class SyncServer2Client<E> {
  public async synchronize(): Promise<void> {
    const start = ts();
    let partial = ts();
    console.log(`${this.name()} - import start`);
    const remoteElements = await this.getFromRemote();
    console.log(
      `${this.name()} - downloaded ${remoteElements.length} elements from remote in ${ts() - partial}ms`,
    );

    partial = ts();
    const localIds = await this.getLocalIds();
    const remoteIds = this.getRemoteIds(remoteElements);

    const toBeDeleted = subtract(localIds, remoteIds);
    console.log(
      `${this.name()} - identified ${toBeDeleted.length} local elements to be deleted - ${ts() - partial}ms`,
    );

    partial = ts();
    await this.deleteFromLocal(toBeDeleted);
    console.log(
      `${this.name()} - deleted ${toBeDeleted.length} local elements - ${ts() - partial}ms`,
    );

    partial = ts();
    await this.bulkPut(remoteElements);
    console.log(
      `${this.name()} - imported ${remoteElements.length} elements locally - ${ts() - partial}ms`,
    );
    console.log(`${this.name()} - import done - ${ts() - start}ms`);
  }

  protected abstract name(): string;
  protected abstract getFromRemote(): Promise<E[]>;
  protected abstract getLocalIds(): Promise<string[]>;
  protected abstract getRemoteIds(elements: E[]): string[];
  protected abstract deleteFromLocal(ids: string[]): Promise<void>;
  protected abstract bulkPut(elements: E[]): Promise<void>;
}

class BoardSyncServer2Client extends SyncServer2Client<Board> {
  protected override name(): string {
    return 'Board';
  }
  protected override getFromRemote(): Promise<Board[]> {
    return backendService.boardResource().getAll();
  }
  protected override async getLocalIds(): Promise<string[]> {
    return (await localDb.boards.toArray()).map((b) => b.boardId);
  }
  protected override getRemoteIds(elements: Board[]): string[] {
    return elements.map((b) => b.boardId);
  }
  protected override deleteFromLocal(ids: string[]): Promise<void> {
    return localDb.boards.bulkDelete(ids);
  }
  protected override async bulkPut(elements: Board[]): Promise<void> {
    await localDb.boards.bulkPut(elements);
  }
}

export class BudgetSyncService {
  server2clients;
  public constructor() {
    this.server2clients = new Array<SyncServer2Client<unknown>>();
    this.server2clients.push(new BoardSyncServer2Client());
  }

  public async synchronize(): Promise<void> {
    console.log('synchronize start');
    const start = ts();
    await this.synchronizeChangelogs();
    for (const server2client of this.server2clients) {
      await server2client.synchronize();
    }
    console.log(`synchronize done in ${ts() - start}ms`);
  }

  protected async synchronizeChangelogs(): Promise<void> {
    const start = ts();
    const changes = (await localDb.changelogs.toArray())
      .sort((a, b) => Changelog.sortById(a, b))
      .filter((e) => e.status !== 'done');
    console.log(`Found ${changes.length} changeset to sync - ${ts() - start}ms`);

    for (const c of changes) {
      try {
        const result = await this.synchronizeChangelog(c);
        if (result === 'unauthorized') {
          console.log('Unauthorized, stop sync');
          break;
        }
      } catch (e) {
        console.error(`Error processing changelog ${Changelog.toString(c)}`, e);
      }
    }
    console.log(`Changelog boards done - ${ts() - start}ms`);
  }

  protected async synchronizeChangelog(changelog: Changelog): Promise<ChangelogStatus> {
    if (changelog.changelogId === undefined) {
      throw Error(`Missing changelogId on ${Changelog.toString(changelog)}`);
    }
    const engine = persistenceManager.getPersistenceEngine(changelog.action, changelog.entityType);
    const status = await engine.syncRemote(changelog.changelogId, false);
    if (status === 'done') {
      console.log(`Changelog synchronized ${Changelog.toString(changelog)}.`);
      //  await localDb.changelogs.delete(changelog.changelogId);
    }
    return status;
  }
}
const budgetSyncService = new BudgetSyncService();
export default budgetSyncService;
