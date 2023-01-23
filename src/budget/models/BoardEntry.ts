import { asDate, asString, asDecimal, asInt } from 'src/utils/JsonUtil';

export default class BoardEntry {
  id = '';
  boardId = '';
  date = new Date();
  creationDate = new Date();
  lastUpdate = new Date();
  ownerId = 0;
  category = '';
  description = '';
  amount = 0;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): BoardEntry {
    const out = new BoardEntry();
    out.id = asString(json.id);
    out.boardId = asString(json.boardId);
    out.date = asDate(json.date);
    out.creationDate = asDate(json.creationDate);
    out.lastUpdate = asDate(json.lastUpdate);
    out.ownerId = asInt(json.ownerId);
    out.category = asString(json.category);
    out.description = asString(json.description);
    out.amount = asDecimal(json.amount);
    return out;
  }
}
