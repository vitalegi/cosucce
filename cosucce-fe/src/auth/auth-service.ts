import type { OidcAuth } from './auth';
import Base64Utils from 'src/utils/base64';
import backendService from 'src/services/backend-service';

function oidcUrl() {
  if (process.env.OIDC_URL !== undefined) {
    return process.env.OIDC_URL;
  }
  throw Error('Missing env variable OIDC_URL');
}

function oidcClientId() {
  if (process.env.OIDC_CLIENT_ID !== undefined) {
    return process.env.OIDC_CLIENT_ID;
  }
  throw Error('Missing env variable OIDC_CLIENT_ID');
}

const OIDC_URL = oidcUrl();
const OIDC_CLIENT_ID = oidcClientId();

class AuthService {
  loginToHomepage(): void {
    this.login(`${process.env.SELF_URL}/oidc/login`, '/');
  }
  login(redirectUrl: string, state: string): void {
    const url = `${OIDC_URL}/login?response_type=code&client_id=${OIDC_CLIENT_ID}&redirect_uri=${encodeURIComponent(redirectUrl)}&scope=email+openid+phone+profile&state=${Base64Utils.encodeUrl(state)}`;
    console.debug('redirect to', url);
    console.log(`back url ${redirectUrl} state ${state}`);
    window.location.replace(url);
  }

  logoutUrl(): string {
    //return `${OIDC_URL}/logout?client_id=${OIDC_CLIENT_ID}&logout_uri=${encodeURIComponent(process.env.SELF_URL + '/oidc/logout')}`;
    return `${OIDC_URL}/logout?client_id=${OIDC_CLIENT_ID}&logout_uri=${encodeURIComponent(process.env.SELF_URL + '/oidc/logout')}&redirect_uri=${encodeURIComponent(process.env.SELF_URL + '/oidc/logout')}`;
  }
  async tokenAuthorization(
    code: string,
    redirectUrl: string,
  ): Promise<OidcAuth> {
    console.log('token auth start');
    const auth = await backendService
      .oidcService()
      .oidcToken(code, redirectUrl);
    console.log('token auth done');
    this.setAuthenticated(auth);
    return auth;
  }

  async tokenRefresh(): Promise<OidcAuth> {
    console.log('token refresh start');
    const newAuth = await backendService.oidcService().oidcRefresh();
    console.log('token refresh done');
    this.setAuthenticated(newAuth);
    return newAuth;
  }

  async logout(): Promise<void> {
    await backendService.oidcService().logout();
    this.clearAuthentication();
  }
  public setAuthenticated(auth: OidcAuth): void {
    window.localStorage.setItem('oidc_access_token', auth.accessToken);
    window.localStorage.setItem('oidc_id_token', auth.idToken);
  }

  public clearAuthentication(): void {
    window.localStorage.removeItem('oidc_access_token');
    window.localStorage.removeItem('oidc_id_token');
  }

  public getIdToken(): string | null {
    return window.localStorage.getItem('oidc_id_token');
  }
}
const authService = new AuthService();
export default authService;
