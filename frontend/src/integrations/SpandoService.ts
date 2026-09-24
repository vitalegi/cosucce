import SpandoDays from 'src/spando/models/SpandoDays';
import SpandoEntry from 'src/spando/models/SpandoEntry';
import api from './BackendService';

export class SpandoService {
  changeSpandoEntry = async (date: string): Promise<SpandoEntry> => {
    const out = await api.post(`/api/spando/${date}`, null, {});
    return SpandoEntry.fromJson(out);
  };
  deleteSpandoEntry = async (date: string): Promise<void> => {
    await api.delete(`/api/spando/${date}`, null, {});
  };
  getSpandos = async (): Promise<SpandoDays[]> => {
    const out = await api.get('/api/spando', null);
    return out.map(SpandoDays.fromJson);
  };
  getEstimates = async (): Promise<SpandoDays[]> => {
    const out = await api.get('/api/spando/estimate', null);
    return out.map(SpandoDays.fromJson);
  };
}

export default new SpandoService();
