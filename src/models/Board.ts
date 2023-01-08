import { asDate, asInt, asString } from 'src/utils/JsonUtil';

export default class Board {
  id = '';
  name = '';
  ownerId = 0;
  creationDate = new Date();
  lastUpdate = new Date();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): Board {
    const out = new Board();
    out.id = asString(json.id);
    out.name = asString(json.name);
    out.ownerId = asInt(json.ownerId);
    out.creationDate = asDate(json.creationDate);
    out.lastUpdate = asDate(json.lastUpdate);
    return out;
  }
}
