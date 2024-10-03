import JsonUtil from 'src/utils/json-util';
import Account from './account';
import Category from './category';

export const EXPENSE_DATE_FORMAT = 'yyyy-MM-dd';

export default class Expense {
  id = '';
  date = '';
  account = new Account();
  category = new Category();
  amount = '';
  description = '';
  creationDate = new Date();
  lastUpdate = new Date();
}

export class ExpenseDto {
  id = '';
  date = '';
  accountId = '';
  categoryId = '';
  amount = '';
  description = '';
  creationDate = new Date();
  lastUpdate = new Date();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static fromJson(obj: any): ExpenseDto {
    const out = new ExpenseDto();
    out.id = JsonUtil.asString(obj.id);
    out.date = JsonUtil.asString(obj.date);
    out.accountId = JsonUtil.asString(obj.accountId);
    out.categoryId = JsonUtil.asString(obj.categoryId);
    out.amount = JsonUtil.asString(obj.amount);
    out.description = JsonUtil.asString(obj.description);

    out.creationDate = JsonUtil.asDate(obj.creationDate);
    out.lastUpdate = JsonUtil.asDate(obj.lastUpdate);
    return out;
  }
}
