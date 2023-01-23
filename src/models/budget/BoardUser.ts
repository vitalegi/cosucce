import UserData from 'src/models/UserData';
import { asString } from 'src/utils/JsonUtil';

export default class BoardUser {
  user = new UserData();
  role = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): BoardUser {
    const out = new BoardUser();
    out.user = UserData.fromJson(json.user);
    out.role = asString(json.role);
    return out;
  }
}
