import { asBoolean, asString } from 'src/utils/JsonUtil';
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
    out.id = asString(obj.id);
    out.type = convertToExpenseType(obj.type);
    out.name = asString(obj.name);
    out.active = asBoolean(obj.active, false);
    out.icon = asString(obj.icon, '');
    out.color = asString(obj.color, '');
    return out;
  }
}
