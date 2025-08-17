import OidcService from './oidc-resource';
import BackendStatus from 'src/models/backend-status';
import BoardResource from 'src/budget/services/board-resource';
import { OidcAuth } from 'src/auth/auth';
import Base64Utils from 'src/utils/base64';
import axios from 'axios';
import { AxiosWrapperAuth, AxiosWrapperPublic } from './authenticated-axios';

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

export class AuthService {
  _oidcService: OidcService;

  public constructor(publicApi: AxiosWrapperPublic) {
    this._oidcService = new OidcService(publicApi);
  }

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
  async tokenAuthorization(code: string, redirectUrl: string): Promise<OidcAuth> {
    console.log('token auth start');
    const auth = await this._oidcService.oidcToken(code, redirectUrl);
    console.log('token auth done');
    this.setAuthenticated(auth);
    return auth;
  }

  async tokenRefresh(): Promise<OidcAuth> {
    console.log('token refresh start');
    const newAuth = await this._oidcService.oidcRefresh();
    console.log('token refresh done');
    this.setAuthenticated(newAuth);
    return newAuth;
  }

  async logout(): Promise<void> {
    await this._oidcService.logout();
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

class BackendService {
  private _publicApi;
  private _oidcService;
  private _boardResource;

  public constructor(publicApi: AxiosWrapperPublic, authenticatedApi: AxiosWrapperAuth) {
    this._publicApi = publicApi;
    this._oidcService = new OidcService(publicApi);
    this._boardResource = new BoardResource(authenticatedApi);
  }

  public oidcService(): OidcService {
    return this._oidcService;
  }
  public boardResource(): BoardResource {
    return this._boardResource;
  }

  public async uptime(): Promise<BackendStatus> {
    try {
      const out = await this._publicApi.get('uptime', { timeout: 500 });
      return BackendStatus.fromJson(out.data, true);
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {
      return BackendStatus.fromJson({}, false);
    }
  }
}

function buildAxiosPublicApi(): AxiosWrapperPublic {
  return new AxiosWrapperPublic(axios.create({ baseURL: '/api' }));
}

function buildAxiosProtectedApi(authService: AuthService): AxiosWrapperAuth {
  const api = axios.create({ baseURL: '/api' });
  api.interceptors.request.use((config) => {
    const idToken = authService.getIdToken();
    if (idToken && idToken !== '') {
      config.headers.Authorization = 'Bearer ' + authService.getIdToken();
    }
    return config;
  });
  const oidcService = new OidcService(new AxiosWrapperPublic(axios.create({ baseURL: '/api' })));
  return new AxiosWrapperAuth(api, oidcService, authService);
}

export const publicApi = buildAxiosPublicApi();
export const authService = new AuthService(publicApi);
export const authenticatedApi = buildAxiosProtectedApi(authService);
const backendService = new BackendService(publicApi, authenticatedApi);
export default backendService;
