import { asInt, asString } from 'src/utils/JsonUtil';

export default class UserData {
  id = 0;
  username = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): UserData {
    const out = new UserData();
    out.id = asInt(json.id);
    out.username = asString(json.username);
    return out;
  }
}
