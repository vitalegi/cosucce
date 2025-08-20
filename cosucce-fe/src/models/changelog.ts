export type Action = 'add' | 'update' | 'delete';

export type ChangelogStatus =
  | 'new'
  | 'ongoing'
  | 'done'
  | 'generic-error'
  | 'unauthorized'
  | 'forbidden'
  | 'conflict';

export type EntityType = 'board' | 'board-entry' | 'board-account' | 'board-category';

export default class Changelog {
  changelogId?: number;
  entityId = '';
  action: Action = 'add';
  entityType: EntityType = 'board';
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  payload: any = {};
  oldETag: string | undefined;
  newETag = '';
  status: ChangelogStatus = 'new';
  creationDate = new Date();
  lastUpdate = new Date();

  public static toString(changelog: Changelog): string {
    return `Changelog ${changelog.changelogId} ${changelog.action} ${changelog.entityType} (${changelog.entityId}) createdOn ${changelog.creationDate.toISOString()}`;
  }

  public static sortById(a: Changelog, b: Changelog): number {
    const idA = a.changelogId || 0;
    const idB = b.changelogId || 0;
    if (idA === idB) {
      return 0;
    }
    return idA > idB ? 1 : -1;
  }
}
