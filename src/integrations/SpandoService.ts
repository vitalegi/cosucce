import SpandoDays from 'src/spando/models/SpandoDays';
import SpandoEntry from 'src/spando/models/SpandoEntry';
import api from './BackendService';

export class SpandoService {
  changeSpandoEntry = async (date: string): Promise<SpandoEntry> => {
    const out = await api.post(`/spando/${date}`, null, {});
    return SpandoEntry.fromJson(out);
  };
  deleteSpandoEntry = async (date: string): Promise<void> => {
    await api.delete(`/spando/${date}`, null, {});
  };
  getSpandos = async (): Promise<SpandoDays[]> => {
    const out = await api.get('/spando', null);
    return out.map(SpandoDays.fromJson);
  };
  getEstimates = async (): Promise<SpandoDays[]> => {
    const out = await api.get('/spando/estimate', null);
    return out.map(SpandoDays.fromJson);
  };
}

export default new SpandoService();
