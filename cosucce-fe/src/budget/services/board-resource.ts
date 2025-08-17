import { AxiosWrapperAuth } from 'src/services/authenticated-axios';
import Board from '../models/board';

export default class BoardResource {
  api;

  public constructor(api: AxiosWrapperAuth) {
    this.api = api;
  }

  public async getAll(): Promise<Board[]> {
    const out = await this.api.get('/budget/board');
    return out.data.map((e: unknown) => Board.fromJson(e));
  }
  public async add(id: string, name: string): Promise<Board> {
    const out = await this.api.post('/budget/board', {
      boardId: id,
      name: name,
    });
    return Board.fromJson(out.data);
  }
  public async update(id: string, name: string, version: number): Promise<Board> {
    const out = await this.api.put(`/budget/board/${id}`, {
      name: name,
      version: version,
    });
    return Board.fromJson(out.data);
  }
  public async delete(id: string): Promise<Board> {
    const out = await this.api.delete(`/budget/board/${id}`);
    return Board.fromJson(out.data);
  }
}
