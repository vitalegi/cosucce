import { defineStore } from 'pinia';
import Account from 'src/model/account';
import Category from 'src/model/category';
import Expense, { ExpenseDto } from 'src/model/expense';
import { ExpenseType } from 'src/model/expense-type';
import { compareDates } from 'src/utils/DateUtil';
import { v4 as uuidv4 } from 'uuid';

class AccountUtil {
  public static create(
    name: string,
    currency: string,
    active: boolean,
  ): Account {
    const account = new Account();
    account.id = uuidv4().toString();
    account.name = name;
    account.active = active;
    account.currency = currency;
    return account;
  }

  public static getById(id: string, accounts: Map<string, Account>): Account {
    const value = accounts.get(id);
    if (value) {
      return value;
    }
    throw Error(`Account ${id} doesn't exist`);
  }
}

class CategoryUtil {
  public static create(name: string, active: boolean): Category {
    const category = new Category();
    category.id = uuidv4().toString();
    category.name = name;
    category.active = active;
    return category;
  }

  public static getById(
    id: string,
    categories: Map<string, Category>,
  ): Category {
    const value = categories.get(id);
    if (value) {
      return value;
    }
    throw Error(`Cateogry ${id} doesn't exist`);
  }
}

class ExpenseUtil {
  public static createDto(
    date: Date,
    expenseType: ExpenseType,
    accountId: string,
    categoryId: string,
    amount: string,
    description: string,
  ): ExpenseDto {
    const out = new ExpenseDto();
    out.date = date;
    out.expenseType = expenseType;
    out.accountId = accountId;
    out.categoryId = categoryId;
    out.amount = amount;
    out.description = description;
    out.creationDate = new Date();
    out.lastUpdate = new Date();
    return out;
  }

  public static build(
    dto: ExpenseDto,
    categories: Map<string, Category>,
    accounts: Map<string, Account>,
  ): Expense {
    const out = new Expense();
    out.id = dto.id;
    out.account = AccountUtil.getById(dto.accountId, accounts);
    out.category = CategoryUtil.getById(dto.categoryId, categories);
    out.date = dto.date;
    out.expenseType = dto.expenseType;
    out.amount = dto.amount;
    out.description = dto.description;
    return out;
  }

  public static inInterval(date: Date, from: Date, to: Date) {
    return from <= date && date <= to;
  }
}

export const useExpenseStore = defineStore('expense', {
  state: () => ({
    structuredEntries: new Array<Expense>(),
    expensesList: new Array<ExpenseDto>(),
    categoriesMap: new Map<string, Category>(),
    accountsMap: new Map<string, Account>(),
  }),
  getters: {
    expensesInInterval(state) {
      return (from: Date, to: Date) => {
        return state.structuredEntries
          .filter((e) => ExpenseUtil.inInterval(e.date, from, to))
          .sort((e1: Expense, e2: Expense) => {
            const c1 = compareDates(e1.date, e2.date);
            if (c1 === 0) {
              return c1;
            }
            return compareDates(e1.lastUpdate, e2.lastUpdate);
          });
      };
    },
    categories() {
      return (expenses: Expense[]) => {
        const map = new Map<string, Category>();
        expenses
          .map((e) => e.category)
          .forEach((c) => {
            if (!map.has(c.id)) {
              map.set(c.id, c);
            }
          });
        return Array.from(map.values());
      };
    },
    firstDate(state): Date | undefined {
      const list = state.expensesList;
      if (list.length === 0) {
        return undefined;
      }
      let min = list[0].date;
      for (const e of list) {
        if (min < e.date) {
          min = e.date;
        }
      }
      return min;
    },

    lastDate(state): Date | undefined {
      const list = state.expensesList;
      if (list.length === 0) {
        return undefined;
      }
      let max = list[0].date;
      for (const e of list) {
        if (max > e.date) {
          max = e.date;
        }
      }
      return max;
    },
  },
  actions: {
    async addCategory(name: string, active: boolean): Promise<Category> {
      const entry = CategoryUtil.create(name, active);
      this.categoriesMap.set(entry.id, entry);
      return entry;
    },
    async addAccount(
      name: string,
      currency: string,
      active: boolean,
    ): Promise<Account> {
      const entry = AccountUtil.create(name, currency, active);
      this.accountsMap.set(entry.id, entry);
      return entry;
    },
    async addExpense(
      date: Date,
      expenseType: ExpenseType,
      accountId: string,
      categoryId: string,
      amount: string,
      description: string,
    ): Promise<Expense> {
      const dto = ExpenseUtil.createDto(
        date,
        expenseType,
        accountId,
        categoryId,
        amount,
        description,
      );
      const out = ExpenseUtil.build(dto, this.categoriesMap, this.accountsMap);
      this.expensesList.push(dto);
      this.structuredEntries.push(out);
      return out;
    },
  },
});

const expenseStore = useExpenseStore();

async function init() {
  const category1 = await expenseStore.addCategory('Risparmi', true);
  const category2 = await expenseStore.addCategory('Bollette', true);

  const account1 = await expenseStore.addAccount('CC', 'EUR', true);
  const account2 = await expenseStore.addAccount('Contanti', 'EUR', true);

  expenseStore.addExpense(
    new Date(),
    'debit',
    account1.id,
    category1.id,
    '10.0000000000000000000001',
    'operazione 1',
  );
  expenseStore.addExpense(
    new Date(),
    'debit',
    account2.id,
    category2.id,
    '255550.33333',
    'operazione 2',
  );

  expenseStore.addExpense(
    new Date(),
    'debit',
    account2.id,
    category2.id,
    '21.66666',
    'operazione 3',
  );

  expenseStore.addExpense(
    new Date(),
    'debit',
    account2.id,
    category2.id,
    '21.999999',
    'operazione 4',
  );

  expenseStore.addExpense(
    new Date(),
    'debit',
    account2.id,
    category2.id,
    '30',
    'operazione 5',
  );
}

init();
