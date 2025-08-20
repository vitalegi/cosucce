import { MD5 } from 'crypto-js';
import ObjectUtil from 'src/utils/object-util';

export default class Board {
  boardId = '';
  name = '';
  creationDate;
  lastUpdate;
  etag;

  public constructor(boardId = '', name = '', etag = '') {
    this.boardId = boardId;
    this.name = name;
    this.creationDate = new Date();
    this.lastUpdate = new Date();
    this.etag = etag;
  }

  public static fromJson(obj: unknown): Board {
    const out = new Board();
    if (!obj) {
      throw new Error('Object is null');
    }
    out.boardId = ObjectUtil.propAsString(obj, 'boardId');
    out.name = ObjectUtil.propAsString(obj, 'name');
    out.creationDate = ObjectUtil.propAsDate(obj, 'creationDate');
    out.lastUpdate = ObjectUtil.propAsDate(obj, 'lastUpdate');
    out.etag = ObjectUtil.propAsString(obj, 'etag');
    return out;
  }

  public static hash(element: Board): string {
    const str = JSON.stringify([element.boardId, element.name].filter((e) => !!e));
    return MD5(str).toString();
  }
}
