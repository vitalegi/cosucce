import { asString } from 'src/utils/JsonUtil';

export type ExpenseType = 'credit' | 'debit';

export function convertToExpenseType(str: string): ExpenseType {
  const type = asString(str);
  if (type === 'credit' || type === 'debit') {
    return type;
  } else {
    throw Error(`Type ${type} is not supported`);
  }
}
