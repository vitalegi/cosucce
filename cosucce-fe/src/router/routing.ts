import { NavigationFailure, Router } from 'vue-router';

function push(router: Router, path: string): Promise<NavigationFailure | void | undefined> {
  return router.push({
    path: path,
  });
}

export class BudgetRouting {
  public viewBoards(router: Router): Promise<NavigationFailure | void | undefined> {
    return push(router, `/budget`);
  }
  public addBoard(router: Router): Promise<NavigationFailure | void | undefined> {
    return push(router, `/budget/add-board`);
  }
  public viewBoard(router: Router, boardId: string): Promise<NavigationFailure | void | undefined> {
    return push(router, `/budget/board/${boardId}`);
  }
  public addBoardEntry(
    router: Router,
    boardId: string,
  ): Promise<NavigationFailure | void | undefined> {
    return push(router, `/budget/board/${boardId}/add-entry`);
  }
  public settingsViewAccounts(
    router: Router,
    boardId: string,
  ): Promise<NavigationFailure | void | undefined> {
    return push(router, `/budget/board/${boardId}/settings/accounts`);
  }
  public settingsAddAccount(
    router: Router,
    boardId: string,
  ): Promise<NavigationFailure | void | undefined> {
    return push(router, `/budget/board/${boardId}/settings/add-account`);
  }
  public settingsEditAccount(
    router: Router,
    boardId: string,
    accountId: string,
  ): Promise<NavigationFailure | void | undefined> {
    return push(router, `/budget/board/${boardId}/settings/account/${accountId}`);
  }
  public settingsViewCategories(
    router: Router,
    boardId: string,
  ): Promise<NavigationFailure | void | undefined> {
    return push(router, `/budget/board/${boardId}/settings/categories`);
  }
}

export class Routing {
  private _budgetRouting = new BudgetRouting();

  public budget(): BudgetRouting {
    return this._budgetRouting;
  }
}

const routing = new Routing();
export default routing;
