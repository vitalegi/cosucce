import ObjectUtil from 'src/utils/object-util';

export default class Board {
  boardId = '';
  name = '';
  creationDate = new Date();
  lastUpdate = new Date();
  version = 0;

  public static fromJson(obj: unknown): Board {
    const out = new Board();
    if (!obj) {
      throw new Error('Object is null');
    }
    out.boardId = ObjectUtil.propAsString(obj, 'boardId');
    out.name = ObjectUtil.propAsString(obj, 'name');
    out.creationDate = ObjectUtil.propAsDate(obj, 'creationDate');
    out.lastUpdate = ObjectUtil.propAsDate(obj, 'lastUpdate');
    out.version = ObjectUtil.propAsInt(obj, 'version');
    return out;
  }
}
