import { defineStore } from 'pinia';
import TimeInterval from 'src/model/interval';
import {
  add,
  endOfDay,
  endOfMonth,
  endOfWeek,
  endOfYear,
  format,
  startOfDay,
  startOfMonth,
  startOfWeek,
  startOfYear,
} from 'date-fns';
import { it } from 'date-fns/locale';

interface State {
  interval: TimeInterval;
  from: Date;
  to: Date;
}

function getLabel(interval: TimeInterval, from: Date, to: Date): string {
  const options = { locale: it };
  switch (interval) {
    case 'all':
      return 'All';
    case 'yearly':
      return format(from, 'yyyy');
    case 'monthly':
      return format(from, "LLL ''yy", options);
    case 'weekly':
      return (
        format(from, 'd LLL', options) +
        ' - ' +
        format(to, "d LLL ''yy", options)
      );
    case 'daily':
      return format(from, "EEEE d LLL ''yy", options);
  }
}

function getInterval(
  date: Date,
  interval: TimeInterval,
): { from: Date; to: Date } {
  switch (interval) {
    case 'all':
      return { from: new Date(0, 0, 0), to: new Date(9999, 0, 0) };
    case 'yearly':
      return { from: startOfYear(date), to: endOfYear(date) };
    case 'monthly':
      return { from: startOfMonth(date), to: endOfMonth(date) };
    case 'weekly':
      return {
        from: startOfWeek(date, { weekStartsOn: 1 }),
        to: endOfWeek(date, { weekStartsOn: 1 }),
      };
    case 'daily':
      return { from: startOfDay(date), to: endOfDay(date) };
  }
}

export const useIntervalStore = defineStore('interval', {
  state: (): State => {
    return {
      interval: 'monthly',
      from: startOfMonth(new Date()),
      to: endOfMonth(new Date()),
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
      const value = getInterval(new Date(), interval);
      this.from = value.from;
      this.to = value.to;
    },
    now() {
      const value = getInterval(new Date(), this.interval);
      this.from = value.from;
      this.to = value.to;
    },
    next() {
      const date = add(this.to, { hours: 1 });
      const value = getInterval(date, this.interval);
      this.from = value.from;
      this.to = value.to;
    },
    previous() {
      const date = add(this.from, { hours: -1 });
      const value = getInterval(date, this.interval);
      this.from = value.from;
      this.to = value.to;
    },
  },
});
