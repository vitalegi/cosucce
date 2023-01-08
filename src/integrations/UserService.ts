import api from './BackendService';

export class UserService {
  notifyAccess = async (): Promise<void> => {
    await api.get('/user', null);
  };
}

export default new UserService();
