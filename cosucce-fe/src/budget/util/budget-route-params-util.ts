import { castParamToString } from 'src/utils/params-util';
import { RouteParams } from 'vue-router';

export function toBoardId(params: RouteParams): string {
  return castParamToString(params.boardId);
}
