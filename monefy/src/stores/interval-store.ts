import { defineStore } from 'pinia';
import TimeInterval from 'src/model/interval';
import { formatFullDate } from 'src/utils/DateUtil';

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

function getLabel(interval: TimeInterval, from: Date, to: Date): string {
  return formatFullDate(from) + ' - ' + formatFullDate(to);
}

export const useIntervalStore = defineStore('interval', {
  state: (): State => {
    return {
      interval: 'monthly',
      from: firstDayOfMonth(new Date()),
      to: lastDayOfMonth(new Date()),
    };
  },
  getters: {
    label: (state) => {
      return getLabel(state.interval, state.from, state.to);
    },
  },
  actions: {
    change(interval: TimeInterval) {
      this.interval = interval;
    },
  },
});
