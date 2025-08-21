import { defineStore, acceptHMRUpdate } from 'pinia';

export const useMainLayoutStore = defineStore('mainLayout', {
  state: () => ({
    budget: {
      enabled: false,
      boardId: '',
    },
  }),

  getters: {
    showRightMenu: (state) => state.budget.enabled,
    isBudgetView: (state) => state.budget.enabled,
    boardId: (state) => state.budget.boardId,
  },

  actions: {
    setBudget(boardId: string) {
      this.budget.enabled = true;
      this.budget.boardId = boardId;
    },
    disableBudget() {
      this.budget.enabled = false;
      this.budget.boardId = '';
    },
  },
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useMainLayoutStore, import.meta.hot));
}
