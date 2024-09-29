import { defineStore } from 'pinia';
import TimeInterval from 'src/model/interval';
import DateUtil from 'src/utils/date-util';

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
  return DateUtil.formatFullDate(from) + ' - ' + DateUtil.formatFullDate(to);
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
