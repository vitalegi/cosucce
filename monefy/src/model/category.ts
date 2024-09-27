import { convertToExpenseType, ExpenseType } from './expense-type';

export default class Category {
  id = '';
  type: ExpenseType = 'debit';
  name = '';
  active = true;

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static fromJson(obj: any): Category {
    const out = new Category();
    out.id = obj.id;
    out.name = obj.name;
    out.active = obj.active;
    out.type = convertToExpenseType(obj.expense);
    return out;
  }
}
