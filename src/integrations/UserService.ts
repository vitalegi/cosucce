import UserData from 'src/models/UserData';
import UserOtp from 'src/models/UserOtp';
import api from './BackendService';

export class UserService {
  getUser = async (): Promise<UserData> => {
    const out = await api.get('/user', null);
    return UserData.fromJson(out);
  };
  updateUsername = async (username: string): Promise<UserData> => {
    const out = await api.put('/user', null, { username: username });
    return UserData.fromJson(out);
  };
  addOtp = async (): Promise<UserOtp> => {
    const out = await api.put('/user/otp', null, {});
    return UserOtp.fromJson(out);
  };
}

export default new UserService();
