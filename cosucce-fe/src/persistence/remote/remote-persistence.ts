import { AxiosResponse } from 'axios';
import Changelog from 'src/models/changelog';

export default interface RemotePersistence {
  executeRemote(changelog: Changelog, allowSSORedirect: boolean): Promise<AxiosResponse>;
}
