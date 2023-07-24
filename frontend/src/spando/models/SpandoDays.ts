import { asString } from 'src/utils/JsonUtil';

export default class SpandoDays {
  from = '';
  to = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): SpandoDays {
    const out = new SpandoDays();
    out.from = asString(json.from);
    out.to = asString(json.to);
    return out;
  }

  getCalendarEntry(): string | { from: string; to: string } {
    if (this.from === this.to) {
      return this.from;
    }
    return { from: this.from, to: this.to };
  }
}
