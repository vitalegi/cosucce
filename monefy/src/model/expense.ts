import { asDate, asDecimal, asString } from 'src/utils/JsonUtil';
import Account from './account';
import Category from './category';
import { convertToExpenseType, ExpenseType } from './expense-type';

export default class Expense {
  id = '';
  date = new Date();
  expenseType: ExpenseType = 'debit';
  account = new Account();
  category = new Category();
  amount = 0;
  creationDate = new Date();
  lastUpdate = new Date();
}

export class ExpenseDto {
  id = '';
  date = new Date();
  expenseType: ExpenseType = 'debit';
  accountId = '';
  categoryId = '';
  amount = 0;
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
    out.amount = asDecimal(obj.amount);
    return out;
  }
}
