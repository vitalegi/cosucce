import { asInt, asDecimal } from 'src/utils/JsonUtil';

export default class UserAmount {
  userId = 0;
  actual = 0;
  expected = 0;
  cumulatedCredit = 0;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): UserAmount {
    const out = new UserAmount();
    out.userId = asInt(json.userId);
    out.actual = asDecimal(json.actual);
    out.expected = asDecimal(json.expected);
    out.cumulatedCredit = asDecimal(json.cumulatedCredit);
    return out;
  }
}
