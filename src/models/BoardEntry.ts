import { asDate, asString, asDecimal } from 'src/utils/JsonUtil';

export default class BoardEntry {
  id = '';
  boardId = '';
  date = new Date();
  creationDate = new Date();
  lastUpdate = new Date();
  ownerId = '';
  category = '';
  description = '';
  amount = 0;

  static fromJson(json: any): BoardEntry {
    const out = new BoardEntry();
    out.id = asString(json.id);
    out.boardId = asString(json.boardId);
    out.date = asDate(json.date);
    out.creationDate = asDate(json.creationDate);
    out.lastUpdate = asDate(json.lastUpdate);
    out.ownerId = asString(json.ownerId);
    out.category = asString(json.category);
    out.description = asString(json.description);
    out.amount = asDecimal(json.amount);
    return out;
  }
}
