import type { AxiosInstance } from 'axios';
import { OidcAuth } from 'src/auth/auth';

export default class OidcService {
  axios: AxiosInstance;
  public constructor(axios: AxiosInstance) {
    this.axios = axios;
  }
  public async oidcToken(code: string, redirectUrl: string): Promise<OidcAuth> {
    const out = await this.axios.post('/oidc/token', {
      code: code,
      redirectUrl: redirectUrl,
    });
    return OidcAuth.fromJson(out.data);
  }
  public async oidcRefresh(): Promise<OidcAuth> {
    const out = await this.axios.post('/oidc/refresh', {});
    return OidcAuth.fromJson(out.data);
  }
  public async logout(): Promise<void> {
    await this.axios.get('/oidc/logout');
  }
}
