import UserAmount from 'src/models/budget/analysis/UserAmount';
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
}
