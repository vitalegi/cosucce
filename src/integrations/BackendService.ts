import axios, { Method } from 'axios';
import { getUser } from 'src/boot/firebase';

const BASE_PATH = 'http://localhost:8080';

class BackendService {
  exchange = async (
    url: string,
    method: Method,
    queryParams: any,
    data: any
  ): Promise<any> => {
    const user = await getUser();
    const idToken = await user?.getIdToken();
    const out = await axios.request({
      url: `${BASE_PATH}${url}`,
      params: queryParams,
      method: method,
      headers: { Authorization: `Bearer ${idToken}` },
      data: data,
    });
    return out.data;
  };

  get = async (url: string, queryParams: any): Promise<any> => {
    return this.exchange(url, 'GET', queryParams, null);
  };

  put = async (url: string, queryParams: any, data: any): Promise<any> => {
    return this.exchange(url, 'PUT', queryParams, data);
  };
}

export default new BackendService();
