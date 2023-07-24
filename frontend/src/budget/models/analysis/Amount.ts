import { asDecimal } from 'src/utils/JsonUtil';

export default class Amount {
  actual = 0;
  expected = 0;
  cumulatedCredit = 0;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any, out: Amount = new Amount()): Amount {
    out.actual = asDecimal(json.actual);
    out.expected = asDecimal(json.expected);
    out.cumulatedCredit = asDecimal(json.cumulatedCredit);
    return out;
  }
}
