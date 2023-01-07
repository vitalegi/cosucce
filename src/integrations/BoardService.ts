import Board from 'src/models/Board';
import BoardEntry from 'src/models/BoardEntry';
import api from './BackendService';

export class BoardService {
  getBoards = async (): Promise<Board[]> => {
    const out = await api.get('/board', null);
    return out.map(Board.fromJson);
  };

  getBoard = async (boardId: string): Promise<Board> => {
    const out = await api.get(`/board/${boardId}`, null);
    return Board.fromJson(out);
  };

  addBoard = async (name: string): Promise<Board> => {
    const out = await api.put('/board/', null, {
      name: name,
    });
    return Board.fromJson(out);
  };

  getBoardEntries = async (boardId: string): Promise<BoardEntry[]> => {
    const out = await api.get(`/board/${boardId}/entries`, null);
    return out.map(BoardEntry.fromJson);
  };
}

export default new BoardService();
