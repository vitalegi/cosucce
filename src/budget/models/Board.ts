import { asDate, asString } from 'src/utils/JsonUtil';

export default class Board {
  id = '';
  name = '';
  creationDate = new Date();
  lastUpdate = new Date();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): Board {
    const out = new Board();
    out.id = asString(json.id);
    out.name = asString(json.name);
    out.creationDate = asDate(json.creationDate);
    out.lastUpdate = asDate(json.lastUpdate);
    return out;
  }
}
