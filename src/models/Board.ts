import { asDate, asString } from 'src/utils/JsonUtil';

export default class Board {
  id = '';
  name = '';
  ownerId = '';
  creationDate = new Date();
  lastUpdate = new Date();

  static fromJson(json: any): Board {
    const out = new Board();
    out.id = asString(json.id);
    out.name = asString(json.name);
    out.ownerId = asString(json.ownerId);
    out.creationDate = asDate(json.creationDate);
    out.lastUpdate = asDate(json.lastUpdate);
    return out;
  }
}
