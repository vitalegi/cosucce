import Board from 'src/budget/models/Board';
import BoardEntry from 'src/budget/models/BoardEntry';
import BoardUser from 'src/budget/models/BoardUser';
import MonthlyUserAnalysis from 'src/budget/models/analysis/MonthlyUserAnalysis';
import { asString } from 'src/utils/JsonUtil';
import api from '../../integrations/BackendService';

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
    const out = await api.post('/board/', null, {
      name: name,
    });
    return Board.fromJson(out);
  };

  addBoardEntry = async (
    boardId: string,
    entry: BoardEntry
  ): Promise<BoardEntry> => {
    const out = await api.post(`/board/${boardId}/entry`, null, entry);
    return BoardEntry.fromJson(out);
  };

  updateBoardEntry = async (
    boardId: string,
    entry: BoardEntry
  ): Promise<BoardEntry> => {
    const out = await api.put(`/board/${boardId}/entry`, null, entry);
    return BoardEntry.fromJson(out);
  };

  deleteBoardEntry = async (
    boardId: string,
    entryId: string
  ): Promise<void> => {
    await api.delete(`/board/${boardId}/entry/${entryId}`, null, null);
  };

  getBoardEntries = async (boardId: string): Promise<BoardEntry[]> => {
    const out = await api.get(`/board/${boardId}/entries`, null);
    return out.map(BoardEntry.fromJson);
  };

  getBoardEntry = async (
    boardId: string,
    boardEntryId: string
  ): Promise<BoardEntry> => {
    const out = await api.get(`/board/${boardId}/entry/${boardEntryId}`, null);
    return BoardEntry.fromJson(out);
  };

  getBoardUsers = async (boardId: string): Promise<Array<BoardUser>> => {
    const out = await api.get(`/board/${boardId}/users`, null);
    return out.map(BoardUser.fromJson);
  };
  getBoardCategories = async (boardId: string): Promise<string[]> => {
    const out = await api.get(`/board/${boardId}/categories`, null);
    return out.map(asString);
  };
  getBoardAnalysisMonthUser = async (
    boardId: string
  ): Promise<MonthlyUserAnalysis[]> => {
    const out = await api.get(`/board/${boardId}/analysis/month-user`, null);
    return out.map(MonthlyUserAnalysis.fromJson);
  };
}

export default new BoardService();
