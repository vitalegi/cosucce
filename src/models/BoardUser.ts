import { asString, asInt } from 'src/utils/JsonUtil';

export default class BoardUser {
  userId = 0;
  username = '';
  role = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): BoardUser {
    const out = new BoardUser();
    out.userId = asInt(json.user.id);
    out.username = asString(json.user.username);
    out.role = asString(json.role);
    return out;
  }
}
