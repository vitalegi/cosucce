import { defineStore } from 'pinia';
import TimeInterval from 'src/model/interval';

interface State {
  interval: TimeInterval;
  from: Date;
  to: Date;
}

function firstDayOfMonth(date: Date): Date {
  return new Date(date.getFullYear(), date.getMonth(), 1);
}

function lastDayOfMonth(date: Date): Date {
  return new Date(date.getFullYear(), date.getMonth() + 1, 0);
}

export const useIntervalStore = defineStore('interval', {
  state: (): State => {
    return {
      interval: 'monthly',
      from: firstDayOfMonth(new Date()),
      to: lastDayOfMonth(new Date()),
    };
  },
  actions: {
    change(interval: TimeInterval) {
      this.interval = interval;
    },
  },
});
