export default class BackendStatus {
  ok = false;
  applicationStart?: Date;
  applicationReady?: Date;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static fromJson(json: any, ok: boolean): BackendStatus {
    const out = new BackendStatus();
    out.ok = ok;
    if (json.applicationStart) {
      out.applicationStart = new Date(Date.parse(json.applicationStart));
    }
    if (json.applicationReady) {
      out.applicationReady = new Date(Date.parse(json.applicationReady));
    }
    return out;
  }
}
