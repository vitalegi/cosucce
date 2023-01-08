import UserData from 'src/models/UserData';
import api from './BackendService';

export class UserService {
  getUser = async (): Promise<UserData> => {
    return await api.get('/user', null);
  };
}

export default new UserService();
