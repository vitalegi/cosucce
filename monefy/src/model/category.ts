import JsonUtil from 'src/utils/json-util';
import { convertToExpenseType, ExpenseType } from './expense-type';

export default class Category {
  id = '';
  type: ExpenseType = 'debit';
  name = '';
  active = true;
  icon = '';
  color = '';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static fromJson(obj: any): Category {
    const out = new Category();
    out.id = JsonUtil.asString(obj.id);
    out.type = convertToExpenseType(obj.type);
    out.name = JsonUtil.asString(obj.name);
    out.active = JsonUtil.asBoolean(obj.active, false);
    out.icon = JsonUtil.asString(obj.icon, '');
    out.color = JsonUtil.asString(obj.color, '');
    return out;
  }
}
