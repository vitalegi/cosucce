import Board from 'src/budget/models/Board';
import BoardEntry from 'src/budget/models/BoardEntry';
import BoardUser from 'src/budget/models/BoardUser';
import MonthlyUserAnalysis from 'src/budget/models/analysis/MonthlyUserAnalysis';
import { asString } from 'src/utils/JsonUtil';
import api from '../../integrations/BackendService';
import BoardInvite from 'src/budget/models/BoardInvite';
import BoardSplit from 'src/budget/models/BoardSplit';
import MonthlyAnalysis from 'src/budget/models/analysis/MonthlyAnalysis';

const sleep = (ms: number): Promise<void> => {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
};

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
    const out = await api.post('/board', null, {
      name: name,
    });
    return Board.fromJson(out);
  };
  updateBoardName = async (id: string, name: string): Promise<Board> => {
    const out = await api.put(`/board/${id}`, null, {
      name: name,
    });
    return Board.fromJson(out);
  };

  deleteBoard = async (id: string): Promise<void> => {
    await api.delete(`/board/${id}`, null, {});
  };

  addBoardEntry = async (
    boardId: string,
    entry: BoardEntry,
  ): Promise<BoardEntry> => {
    const out = await api.post(`/board/${boardId}/entry`, null, entry);
    return BoardEntry.fromJson(out);
  };

  addBoardEntries = async (
    boardId: string,
    entries: Array<BoardEntry>,
  ): Promise<Array<BoardEntry>> => {
    const out = await api.post(`/board/${boardId}/entries`, null, {
      entries: entries,
    });
    return out.map(BoardEntry.fromJson);
  };

  updateBoardEntry = async (
    boardId: string,
    entry: BoardEntry,
  ): Promise<BoardEntry> => {
    const out = await api.put(`/board/${boardId}/entry`, null, entry);
    return BoardEntry.fromJson(out);
  };

  deleteBoardEntry = async (
    boardId: string,
    entryId: string,
  ): Promise<void> => {
    await api.delete(`/board/${boardId}/entry/${entryId}`, null, null);
  };

  getBoardEntries = async (boardId: string): Promise<BoardEntry[]> => {
    const out = await api.get(`/board/${boardId}/entries`, null);
    return out.map(BoardEntry.fromJson);
  };

  getBoardEntry = async (
    boardId: string,
    boardEntryId: string,
  ): Promise<BoardEntry> => {
    const out = await api.get(`/board/${boardId}/entry/${boardEntryId}`, null);
    return BoardEntry.fromJson(out);
  };

  getBoardUsers = async (boardId: string): Promise<Array<BoardUser>> => {
    const out = await api.get(`/board/${boardId}/users`, null);
    return out.map(BoardUser.fromJson);
  };
  addBoardInvite = async (boardId: string): Promise<BoardInvite> => {
    const out = await api.post(`/board/${boardId}/invite`, null, {});
    return BoardInvite.fromJson(out);
  };
  useBoardInvite = async (boardId: string, invite: string): Promise<void> => {
    await api.get(`/board/${boardId}/invite/${invite}`, null);
  };
  getBoardCategories = async (boardId: string): Promise<string[]> => {
    const out = await api.get(`/board/${boardId}/categories`, null);
    return out.map(asString);
  };
  getBoardAnalysisMonthUser = async (
    boardId: string,
  ): Promise<MonthlyUserAnalysis[]> => {
    const out = await api.get(`/board/${boardId}/analysis/month-user`, null);
    return out.map(MonthlyUserAnalysis.fromJson);
  };
  getBoardAnalysisMonth = async (
    boardId: string,
  ): Promise<MonthlyAnalysis[]> => {
    const out = await api.get(`/board/${boardId}/analysis/month`, null);
    return out.map(MonthlyAnalysis.fromJson);
  };
  addBoardSplit = async (boardSplit: BoardSplit): Promise<BoardSplit> => {
    const out = await api.post(
      `/board/${boardSplit.boardId}/split`,
      null,
      boardSplit,
    );
    return BoardSplit.fromJson(out);
  };
  getBoardSplits = async (boardId: string): Promise<BoardSplit[]> => {
    const out = await api.get(`/board/${boardId}/splits`, null);
    return out.map(BoardSplit.fromJson);
  };
  deleteBoardSplit = async (boardId: string, id: string): Promise<void> => {
    await api.delete(`/board/${boardId}/split/${id}`, null, {});
  };
  updateBoardSplit = async (boardSplit: BoardSplit): Promise<BoardSplit> => {
    const out = await api.put(
      `/board/${boardSplit.boardId}/split`,
      null,
      boardSplit,
    );
    return BoardSplit.fromJson(out);
  };
  getGrants = async (boardId: string): Promise<string[]> => {
    const out = await api.get(`/board/${boardId}/grants`, null);
    return out.map(asString);
  };
}

export default new BoardService();
