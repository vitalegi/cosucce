import Dexie, { EntityTable } from 'dexie';
import { defineStore } from 'pinia';
import Account from 'src/model/account';
import Category from 'src/model/category';
import Expense, { ExpenseDto } from 'src/model/expense';
import { ExpenseType } from 'src/model/expense-type';
import DateUtil from 'src/utils/date-util';
import { v4 as uuidv4 } from 'uuid';

const db = new Dexie('db') as Dexie & {
  accounts: EntityTable<Account, 'id'>;
  categories: EntityTable<Category, 'id'>;
  expenses: EntityTable<ExpenseDto, 'id'>;
};
db.version(1).stores({
  accounts: '++id, name, currency, active, icon, color',
  categories: '++id, type, name, active, icon, color',
  expenses:
    '++id, date, accountId, categoryId, amount, description, creationDate, lastUpdate',
});
/*
const categoryDb = new Dexie('categories') as Dexie & {
  categories: EntityTable<Category, 'id'>;
};
categoryDb.version(1).stores({ categories: '++id, type, name, active' });

const expenseDb = new Dexie('expenses') as Dexie & {
  expenses: EntityTable<ExpenseDto, 'id'>;
};
expenseDb
  .version(1)
  .stores({
    expenses:
      '++id, date, accountId, categoryId, amount, description, creationDate, lastUpdate',
  });
*/
class AccountUtil {
  public static create(
    id: string | null,
    name: string,
    currency: string,
    active: boolean,
    icon: string,
    color: string,
  ): Account {
    const account = new Account();
    if (id === null) {
      account.id = uuidv4().toString();
    } else {
      account.id = id;
    }
    account.name = name;
    account.active = active;
    account.currency = currency;
    account.icon = icon;
    account.color = color;
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
  public static create(
    id: string | null,
    name: string,
    active: boolean,
    type: ExpenseType,
    icon: string,
    color: string,
  ): Category {
    const category = new Category();
    if (id === null) {
      category.id = uuidv4().toString();
    } else {
      category.id = id;
    }
    category.name = name;
    category.type = type;
    category.active = active;
    category.icon = icon;
    category.color = color;
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
    out.id = uuidv4().toString();
    out.date = date;
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
    expense(state) {
      return (id: string): Expense => {
        const match = state.structuredEntries.filter((e) => e.id === id);
        if (match.length === 0) {
          throw Error(`Expense ${id} not found`);
        }
        return match[0];
      };
    },
    accounts(state): Account[] {
      return Array.from(state.accountsMap.values()).sort((a1, a2) =>
        a1.name.toUpperCase() < a2.name.toUpperCase() ? -1 : 1,
      );
    },
    categories(state) {
      return (type?: ExpenseType): Category[] => {
        let entries = Array.from(state.categoriesMap.values());
        if (type) {
          entries = entries.filter((c) => c.type === type);
        }
        return entries.sort((c1, c2) =>
          c1.name.toUpperCase() < c2.name.toUpperCase() ? -1 : 1,
        );
      };
    },
    expensesInInterval(state) {
      return (from: Date, to: Date) => {
        return state.structuredEntries
          .filter((e) => ExpenseUtil.inInterval(e.date, from, to))
          .sort((e1: Expense, e2: Expense) => {
            const c1 = DateUtil.compareDates(e1.date, e2.date);
            if (c1 === 0) {
              return c1;
            }
            return DateUtil.compareDates(e1.lastUpdate, e2.lastUpdate);
          });
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
    async addCategory(
      name: string,
      active: boolean,
      type: ExpenseType,
      icon: string,
      color: string,
    ): Promise<Category> {
      const entry = CategoryUtil.create(null, name, active, type, icon, color);
      this.categoriesMap.set(entry.id, entry);
      await db.categories.add(entry);
      return entry;
    },
    async updateCategory(
      id: string,
      name: string,
      active: boolean,
      type: ExpenseType,
      icon: string,
      color: string,
    ): Promise<Category> {
      if (!this.categoriesMap.has(id)) {
        throw new Error(`Can't find category with id ${id}`);
      }
      const entry = CategoryUtil.create(id, name, active, type, icon, color);
      this.categoriesMap.set(entry.id, entry);
      await db.categories.update(entry.id, entry);
      return entry;
    },
    async addAccount(
      name: string,
      currency: string,
      active: boolean,
      icon: string,
      color: string,
    ): Promise<Account> {
      const entry = AccountUtil.create(
        null,
        name,
        currency,
        active,
        icon,
        color,
      );
      this.accountsMap.set(entry.id, entry);
      await db.accounts.add(entry);
      return entry;
    },
    async updateAccount(
      id: string,
      name: string,
      currency: string,
      active: boolean,
      icon: string,
      color: string,
    ): Promise<Account> {
      const entry = AccountUtil.create(id, name, currency, active, icon, color);
      this.accountsMap.set(entry.id, entry);
      await db.accounts.update(entry.id, entry);
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
      await db.expenses.add(dto);
      this.expensesList.push(dto);
      this.structuredEntries.push(out);
      return out;
    },

    async loadCategories(entries: Category[]): Promise<void> {
      entries.forEach((entry) => this.categoriesMap.set(entry.id, entry));
    },
    async loadAccounts(entries: Account[]): Promise<void> {
      entries.forEach((entry) => this.accountsMap.set(entry.id, entry));
    },
    async loadExpenses(entries: ExpenseDto[]): Promise<void> {
      const out = entries.map((e) =>
        ExpenseUtil.build(e, this.categoriesMap, this.accountsMap),
      );
      this.expensesList.push(...entries);
      this.structuredEntries.push(...out);
    },
  },
});

const expenseStore = useExpenseStore();

async function loadCategories(): Promise<void> {
  const startTime = DateUtil.timestamp();
  const entries = await db.categories.toArray();
  const mapped = entries.map(Category.fromJson);
  await expenseStore.loadCategories(mapped);
  console.log(`loaded categories in ${DateUtil.timestamp() - startTime}ms`);
}

async function loadAccounts(): Promise<void> {
  const startTime = DateUtil.timestamp();
  const entries = await db.accounts.toArray();
  const mapped = entries.map(Account.fromJson);
  await expenseStore.loadAccounts(mapped);
  console.log(`loaded accounts in ${DateUtil.timestamp() - startTime}ms`);
}

async function loadExpenses(): Promise<void> {
  const startTime = DateUtil.timestamp();
  const entries = await db.expenses.toArray();
  const mapped = entries.map(ExpenseDto.fromJson);
  await expenseStore.loadExpenses(mapped);
  console.log(`loaded expenses in ${DateUtil.timestamp() - startTime}ms`);
}

async function loadData() {
  await loadCategories();
  await loadAccounts();
  await loadExpenses();
}

loadData();
