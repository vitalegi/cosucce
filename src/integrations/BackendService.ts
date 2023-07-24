/* eslint-disable @typescript-eslint/no-explicit-any */
import axios, { Method } from 'axios';
import { getUser } from 'src/boot/firebase';
import { Notify } from 'quasar';

const baseUrl = process.env.VUE_APP_BACKEND;
if (baseUrl === undefined) {
  throw Error('Missing baseUrl configuration');
}

class BackendService {
  exchange = async (
    url: string,
    method: Method,
    queryParams: any,
    data: any
  ): Promise<any> => {
    const user = await getUser();
    const idToken = await user?.getIdToken();
    try {
      const out = await axios.request({
        url: `${baseUrl}${url}`,
        params: queryParams,
        method: method,
        headers: { Authorization: `Bearer ${idToken}` },
        data: data,
      });
      return out.data;
    } catch (e) {
      if (e && e.response && e.response.status) {
        if (e.response.status === 403) {
          Notify.create({
            message: 'Accesso non autorizzato',
          });
        } else if (e.response.status === 500) {
          Notify.create({
            message: 'Errore lato server, verificare i log',
          });
        } else {
          Notify.create({
            message: `Errore ${e.response.status}`,
          });
        }
      } else {
        Notify.create({
          message: 'Verificare la connettività',
        });
      }
      throw e;
    }
  };

  get = async (url: string, queryParams: any): Promise<any> => {
    return this.exchange(url, 'GET', queryParams, null);
  };

  put = async (url: string, queryParams: any, data: any): Promise<any> => {
    return this.exchange(url, 'PUT', queryParams, data);
  };
  post = async (url: string, queryParams: any, data: any): Promise<any> => {
    return this.exchange(url, 'POST', queryParams, data);
  };
  delete = async (url: string, queryParams: any, data: any): Promise<any> => {
    return this.exchange(url, 'DELETE', queryParams, data);
  };
}

export default new BackendService();
