import axios from 'axios';
import authService from 'src/auth/auth-service';
import AuthenticatedAxios from './authenticated-axios';
import OidcService from './oidc-resource';
import BackendStatus from 'src/models/backend-status';
import BoardResource from 'src/budget/services/board-resource';

const authenticated = axios.create({ baseURL: '/api' });
const apiPublic = axios.create({ baseURL: '/api' });

authenticated.interceptors.request.use((config) => {
  const idToken = authService.getIdToken();
  if (idToken && idToken !== '') {
    config.headers.Authorization = 'Bearer ' + authService.getIdToken();
  }
  return config;
});

class BackendService {
  private _oidcService;
  private _boardResource;

  public constructor() {
    this._oidcService = new OidcService(apiPublic);
    const axiosWrapper = new AuthenticatedAxios(authenticated, this._oidcService);
    this._boardResource = new BoardResource(axiosWrapper);
  }

  public oidcService(): OidcService {
    return this._oidcService;
  }
  public boardResource(): BoardResource {
    return this._boardResource;
  }

  public async uptime(): Promise<BackendStatus> {
    try {
      const out = await apiPublic.get('uptime', { timeout: 500 });
      return BackendStatus.fromJson(out.data, true);
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {
      return BackendStatus.fromJson({}, false);
    }
  }
}

const backendService = new BackendService();
export default backendService;
