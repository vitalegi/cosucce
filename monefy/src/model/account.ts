import { asBoolean, asString } from 'src/utils/JsonUtil';

export default class Account {
  id = '';
  name = '';
  currency = '';
  active = true;
  icon = '';
  color = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static fromJson(obj: any): Account {
    const out = new Account();
    out.id = asString(obj.id);
    out.name = asString(obj.name);
    out.active = asBoolean(obj.active, false);
    out.icon = asString(obj.icon, '');
    out.color = asString(obj.color, '');
    return out;
  }
}
