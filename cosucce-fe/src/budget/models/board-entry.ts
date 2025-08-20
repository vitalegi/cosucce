import { MD5 } from 'crypto-js';
import ObjectUtil from 'src/utils/object-util';

export default class BoardEntry {
  entryId = '';
  boardId = '';
  date = '';
  accountId = '';
  categoryId = '';
  description = '';
  amount = '';
  lastUpdatedBy = '';
  creationDate = new Date();
  lastUpdate = new Date();
  etag = '';

  public static fromJson(obj: unknown): BoardEntry {
    const out = new BoardEntry();
    if (!obj) {
      throw new Error('Object is null');
    }
    out.entryId = ObjectUtil.propAsString(obj, 'entryId');
    out.boardId = ObjectUtil.propAsString(obj, 'boardId');
    out.date = ObjectUtil.propAsString(obj, 'date');
    out.accountId = ObjectUtil.propAsString(obj, 'accountId');
    out.categoryId = ObjectUtil.propAsString(obj, 'categoryId');
    out.description = ObjectUtil.propAsString(obj, 'description');
    out.amount = ObjectUtil.propAsString(obj, 'amount');
    out.lastUpdatedBy = ObjectUtil.propAsString(obj, 'lastUpdatedBy');
    out.creationDate = ObjectUtil.propAsDate(obj, 'creationDate');
    out.lastUpdate = ObjectUtil.propAsDate(obj, 'lastUpdate');
    out.etag = ObjectUtil.propAsString(obj, 'etag');
    return out;
  }

  public static hash(e: BoardEntry): string {
    const str = JSON.stringify(
      [
        e.boardId,
        e.entryId,
        e.date,
        e.accountId,
        e.categoryId,
        e.description,
        e.amount,
        e.lastUpdatedBy,
      ].filter((e) => !!e),
    );
    return MD5(str).toString();
  }
}
