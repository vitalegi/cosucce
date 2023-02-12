import Amount from 'src/budget/models/analysis/Amount';
import { asInt } from 'src/utils/JsonUtil';

export default class UserAmount extends Amount {
  userId = 0;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): UserAmount {
    const out = new UserAmount();
    out.userId = asInt(json.userId);
    Amount.fromJson(json, out);
    return out;
  }
}
