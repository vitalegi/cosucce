import { asDate, asInt, asString } from 'src/utils/JsonUtil';

export default class UserOtp {
  id = 0;
  userId = 0;
  validTo = new Date();
  otp = '';
  status = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): UserOtp {
    const out = new UserOtp();
    out.id = asInt(json.id);
    out.userId = asInt(json.userId);
    out.validTo = asDate(json.validTo);
    out.otp = asString(json.otp);
    out.status = asString(json.status);
    return out;
  }
}
