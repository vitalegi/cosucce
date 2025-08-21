import { MD5 } from 'crypto-js';
import ObjectUtil from 'src/utils/object-util';

export default class BoardCategory {
  categoryId = '';
  boardId = '';
  etag = '';
  label = '';
  icon = '';
  enabled = true;
  creationDate = new Date();
  lastUpdate = new Date();

  public static fromJson(obj: unknown): BoardCategory {
    const out = new BoardCategory();
    if (!obj) {
      throw new Error('Object is null');
    }
    out.categoryId = ObjectUtil.propAsString(obj, 'categoryId');
    out.boardId = ObjectUtil.propAsString(obj, 'boardId');
    out.etag = ObjectUtil.propAsString(obj, 'etag');
    out.label = ObjectUtil.propAsString(obj, 'label');
    out.icon = ObjectUtil.propAsString(obj, 'icon');
    out.enabled = ObjectUtil.propAsBoolean(obj, 'enabled');
    out.creationDate = ObjectUtil.propAsDate(obj, 'creationDate');
    out.lastUpdate = ObjectUtil.propAsDate(obj, 'lastUpdate');
    return out;
  }

  public static hash(e: BoardCategory): string {
    const str = JSON.stringify(
      [e.categoryId, e.boardId, e.etag, e.label, e.icon, e.enabled].filter((e) => !!e),
    );
    return MD5(str).toString();
  }
}
