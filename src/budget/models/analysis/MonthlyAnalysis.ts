import { asDecimal, asInt } from 'src/utils/JsonUtil';

export default class MonthlyAnalysis {
  year = 0;
  month = 0;
  amount = 0;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): MonthlyAnalysis {
    const out = new MonthlyAnalysis();
    out.year = asInt(json.year);
    out.month = asInt(json.month);
    out.amount = asDecimal(json.amount);
    return out;
  }
}
