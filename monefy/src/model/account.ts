export default class Account {
  id = '';
  name = '';
  currency = '';
  active = true;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static fromJson(obj: any): Account {
    const out = new Account();
    out.id = obj.id;
    out.name = obj.name;
    out.active = obj.active;
    return out;
  }
}
