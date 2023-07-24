import { asDate, asString } from 'src/utils/JsonUtil';

export default class SpandoEntry {
  entryId = '';
  type = '';
  date = new Date();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): SpandoEntry {
    const out = new SpandoEntry();
    out.entryId = asString(json.entryId);
    out.type = asString(json.type);
    out.date = asDate(json.date);
    return out;
  }
}
