import JsonUtil from 'src/utils/json-util';

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
    out.id = JsonUtil.asString(obj.id);
    out.name = JsonUtil.asString(obj.name);
    out.active = JsonUtil.asBoolean(obj.active, false);
    out.icon = JsonUtil.asString(obj.icon, '');
    out.color = JsonUtil.asString(obj.color, '');
    return out;
  }
}
