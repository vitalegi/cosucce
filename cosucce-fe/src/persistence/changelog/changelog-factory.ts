import Changelog, { Action, EntityType } from 'src/models/changelog';
import localDb from '../local-db';

export interface ChangelogFactory<E> {
  addChangelog(action: Action, entity: E): Promise<Changelog>;
}

export abstract class AbstractChangelogFactory<E> implements ChangelogFactory<E> {
  public async addChangelog(action: Action, entity: E): Promise<Changelog> {
    const changelog = new Changelog();
    changelog.entityId = this._entityId(entity);
    changelog.action = action;
    changelog.entityType = this._entityType(entity);
    changelog.payload = entity;
    changelog.status = 'new';
    changelog.creationDate = new Date();
    changelog.lastUpdate = new Date();
    const id = await localDb.changelogs.put(changelog);
    console.log(`Added new entry to changelog, id: ${id}`);
    const entry = await localDb.changelogs.get(id);
    if (!entry) {
      throw new Error('Changelog insert failed');
    }
    return entry;
  }

  protected abstract _entityId(entity: E): string;

  protected abstract _entityType(entity: E): EntityType;
}
