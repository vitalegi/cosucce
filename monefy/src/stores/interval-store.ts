import { defineStore } from 'pinia';
import TimeInterval from 'src/model/interval';
import DateUtil from 'src/utils/date-util';

interface State {
  interval: TimeInterval;
  from: Date;
  to: Date;
}

function firstDayOfYear(date: Date): Date {
  return new Date(date.getFullYear(), 0, 1);
}

function lastDayOfYear(date: Date): Date {
  return new Date(date.getFullYear() + 1, 0, 1);
}

function firstDayOfMonth(date: Date): Date {
  return new Date(date.getFullYear(), date.getMonth(), 1);
}

function lastDayOfMonth(date: Date): Date {
  return new Date(date.getFullYear(), date.getMonth() + 1, 0);
}

function startOfDay(date: Date): Date {
  return new Date(
    date.getFullYear(),
    date.getMonth(),
    date.getDay(),
    0,
    0,
    0,
    0,
  );
}

function endOfDay(date: Date): Date {
  return new Date(
    date.getFullYear(),
    date.getMonth(),
    date.getDay() + 1,
    0,
    0,
    0,
    0,
  );
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
      switch (interval) {
        case 'all':
          break;
        case 'yearly':
          this.from = firstDayOfYear(new Date());
          this.to = lastDayOfYear(new Date());
          break;
        case 'monthly':
          this.from = firstDayOfMonth(new Date());
          this.to = lastDayOfMonth(new Date());
          break;
        case 'weekly':
          break;
        case 'daily':
          this.from = startOfDay(new Date());
          this.to = endOfDay(new Date());
          break;
      }
    },
  },
});
