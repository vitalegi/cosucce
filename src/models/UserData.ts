import { asInt, asIntNullable, asString } from 'src/utils/JsonUtil';

export default class UserData {
  id = 0;
  username = '';
  telegramUserId: number | null = null;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): UserData {
    const out = new UserData();
    out.id = asInt(json.id);
    out.username = asString(json.username);
    out.telegramUserId = asIntNullable(json.telegramUserId);
    return out;
  }
}
