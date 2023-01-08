import Board from 'src/models/Board';
import BoardEntry from 'src/models/BoardEntry';
import BoardUser from 'src/models/BoardUser';
import { asString } from 'src/utils/JsonUtil';
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

  addBoardEntry = async (
    boardId: string,
    entry: BoardEntry
  ): Promise<BoardEntry> => {
    const out = await api.put(`/board/${boardId}/entry`, null, entry);
    return BoardEntry.fromJson(out);
  };

  getBoardEntries = async (boardId: string): Promise<BoardEntry[]> => {
    const out = await api.get(`/board/${boardId}/entries`, null);
    return out.map(BoardEntry.fromJson);
  };

  getBoardUsers = async (boardId: string): Promise<BoardUser[]> => {
    const out = await api.get(`/board/${boardId}/users`, null);
    return out.map(BoardUser.fromJson);
  };
  getBoardCategories = async (boardId: string): Promise<string[]> => {
    const out = await api.get(`/board/${boardId}/categories`, null);
    return out.map(asString);
  };
}

export default new BoardService();
