import { asString } from 'src/utils/JsonUtil';

export default class BoardInvite {
  id = '';
  boardId = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static fromJson(json: any): BoardInvite {
    const out = new BoardInvite();
    out.id = asString(json.id);
    out.boardId = asString(json.boardId);
    return out;
  }

  encodeToken(): string {
    return btoa(JSON.stringify(this));
  }

  static fromToken(token: string): BoardInvite {
    return BoardInvite.fromJson(JSON.parse(atob(token)));
  }
}
