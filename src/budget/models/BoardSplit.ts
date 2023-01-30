import { asDecimal, asInt, asIntNullable, asString } from 'src/utils/JsonUtil';

export default class BoardSplit {
  id = '';
  userId = 0;
  boardId = '';
  fromYear: number | null = null;
  fromMonth: number | null = null;
  toYear: number | null = null;
  toMonth: number | null = null;
  value1 = 0;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): BoardSplit {
    const out = new BoardSplit();
    out.id = asString(json.id);
    out.userId = asInt(json.userId);
    out.boardId = asString(json.boardId);
    out.fromYear = asIntNullable(json.fromYear);
    out.fromMonth = asIntNullable(json.fromMonth);
    out.toYear = asIntNullable(json.toYear);
    out.toMonth = asIntNullable(json.toMonth);
    out.value1 = asDecimal(json.value1);
    return out;
  }
}
