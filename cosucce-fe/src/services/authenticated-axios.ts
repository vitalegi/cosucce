/* eslint-disable @typescript-eslint/no-explicit-any */
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { AxiosError } from 'axios';
import type OidcService from './oidc-resource';
import authService from 'src/auth/auth-service';

export default class AuthenticatedAxios {
  instance;
  oidcService;

  constructor(instance: AxiosInstance, oidcService: OidcService) {
    this.instance = instance;
    this.oidcService = oidcService;
  }

  async get<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.authenticatedCall(() =>
      this.instance.get<T, R, D>(url, config),
    );
  }
  delete<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.delete(url, config));
  }
  post<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.post(url, data, config));
  }
  request<T = any, R = AxiosResponse<T>, D = any>(
    config: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.request(config));
  }
  put<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.put(url, data, config));
  }
  patch<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.patch(url, data, config));
  }

  private async authenticatedCall<D>(fn: () => Promise<D>): Promise<D> {
    try {
      return await fn();
    } catch (e1) {
      const error1 = AxiosError.from(e1);
      if (!error1.response?.status || error1.response.status !== 401) {
        console.log('backend call failure');
        throw e1;
      }
      try {
        console.log('refresh token');
        const auth = await this.oidcService.oidcRefresh();
        authService.setAuthenticated(auth);
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
      } catch (e2) {
        const back = window.location.pathname + window.location.search;
        authService.login(`${process.env.SELF_URL}/oidc/login`, back);
        throw Error('No session available, do SSO');
      }
      console.log('do call #2');
      return await fn();
    }
  }
}
