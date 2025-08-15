export class OidcAuth {
  idToken = '';
  accessToken = '';
  expiresIn = 0;
  tokenType = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static fromJson(json: any): OidcAuth {
    const out = new OidcAuth();
    out.idToken = json.idToken;
    out.accessToken = json.accessToken;
    out.expiresIn = json.expiresIn;
    out.tokenType = json.tokenType;
    return out;
  }
}
