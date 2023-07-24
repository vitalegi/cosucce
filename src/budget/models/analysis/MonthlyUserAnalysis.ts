import UserAmount from 'src/budget/models/analysis/UserAmount';
import { asInt } from 'src/utils/JsonUtil';

export default class BoardUserAnalysis {
  year = 0;
  month = 0;
  users = new Array<UserAmount>();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): BoardUserAnalysis {
    const out = new BoardUserAnalysis();
    out.year = asInt(json.year);
    out.month = asInt(json.month);
    if (json.users) {
      out.users = json.users.map(UserAmount.fromJson);
    }
    return out;
  }

  static sort(a: BoardUserAnalysis, b: BoardUserAnalysis): number {
    const v1 = a.year * 12 + a.month;
    const v2 = b.year * 12 + b.month;
    return v1 - v2;
  }
}
