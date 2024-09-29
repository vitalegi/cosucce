import { asDate, asString } from 'src/utils/JsonUtil';
import Account from './account';
import Category from './category';
import { convertToExpenseType, ExpenseType } from './expense-type';

export default class Expense {
  id = '';
  date = new Date();
  expenseType: ExpenseType = 'debit';
  account = new Account();
  category = new Category();
  amount = '';
  description = '';
  creationDate = new Date();
  lastUpdate = new Date();
}

export class ExpenseDto {
  id = '';
  date = new Date();
  expenseType: ExpenseType = 'debit';
  accountId = '';
  categoryId = '';
  amount = '';
  description = '';
  creationDate = new Date();
  lastUpdate = new Date();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static fromJson(obj: any): ExpenseDto {
    const out = new ExpenseDto();
    out.id = asString(obj.id);
    out.date = asDate(obj.date);
    out.expenseType = convertToExpenseType(obj.expense);
    out.accountId = asString(obj.accountId);
    out.categoryId = asString(obj.categoryId);
    out.amount = asString(obj.amount);
    out.description = asString(obj.description);

    out.creationDate = asDate(obj.creationDate);
    out.lastUpdate = asDate(obj.lastUpdate);
    return out;
  }
}
