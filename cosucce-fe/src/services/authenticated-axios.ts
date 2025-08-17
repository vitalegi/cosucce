/* eslint-disable @typescript-eslint/no-explicit-any */
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { AxiosError } from 'axios';
import type OidcService from './oidc-resource';
import { AuthService } from './backend-service';

export interface AxiosWrapper {
  get<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect?: boolean,
  ): Promise<R>;

  delete<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect?: boolean,
  ): Promise<R>;
  post<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect?: boolean,
  ): Promise<R>;
  request<T = any, R = AxiosResponse<T>, D = any>(
    config: AxiosRequestConfig<D>,
    allowSSORedirect: boolean,
  ): Promise<R>;
  put<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect?: boolean,
  ): Promise<R>;
  patch<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect?: boolean,
  ): Promise<R>;
}

export class AxiosWrapperAuth implements AxiosWrapper {
  instance;
  oidcService;
  authService;

  constructor(instance: AxiosInstance, oidcService: OidcService, authService: AuthService) {
    this.instance = instance;
    this.oidcService = oidcService;
    this.authService = authService;
  }

  get<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect: boolean = true,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.get<T, R, D>(url, config), allowSSORedirect);
  }
  delete<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect: boolean = true,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.delete(url, config), allowSSORedirect);
  }
  post<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect: boolean = true,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.post(url, data, config), allowSSORedirect);
  }
  request<T = any, R = AxiosResponse<T>, D = any>(
    config: AxiosRequestConfig<D>,
    allowSSORedirect: boolean = true,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.request(config), allowSSORedirect);
  }
  put<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect: boolean = true,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.put(url, data, config), allowSSORedirect);
  }
  patch<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
    allowSSORedirect: boolean = true,
  ): Promise<R> {
    return this.authenticatedCall(() => this.instance.patch(url, data, config), allowSSORedirect);
  }

  private async authenticatedCall<D>(
    fn: () => Promise<D>,
    allowSSORedirect: boolean = true,
  ): Promise<D> {
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
        this.authService.setAuthenticated(auth);
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
      } catch (e2) {
        if (allowSSORedirect) {
          const back = window.location.pathname + window.location.search;
          this.authService.login(`${process.env.SELF_URL}/oidc/login`, back);
          throw Error('No session available, do SSO');
        } else {
          throw Error('No session available, SSO not allowed');
        }
      }
      console.log('do call #2');
      return await fn();
    }
  }
}

export class AxiosWrapperPublic implements AxiosWrapper {
  instance;

  constructor(instance: AxiosInstance) {
    this.instance = instance;
  }

  get<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.instance.get<T, R, D>(url, config);
  }
  delete<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.instance.delete(url, config);
  }
  post<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.instance.post(url, data, config);
  }
  request<T = any, R = AxiosResponse<T>, D = any>(config: AxiosRequestConfig<D>): Promise<R> {
    return this.instance.request(config);
  }
  put<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.instance.put(url, data, config);
  }
  patch<T = any, R = AxiosResponse<T>, D = any>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig<D>,
  ): Promise<R> {
    return this.instance.patch(url, data, config);
  }
}
