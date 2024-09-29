import { leftPadding } from 'src/utils/StringUtil';

export const toQDateFormat = (date: Date): string => {
  const month = leftPadding(`${date.getMonth() + 1}`, 2, '0');
  const day = leftPadding(`${date.getDate()}`, 2, '0');
  return `${date.getFullYear()}/${month}/${day}`;
};

export const fromQDateFormat = (str: string): Date => {
  const values = str.split('/');
  const year = parseInt(values[0]);
  const month = parseInt(values[1]) - 1;
  const day = parseInt(values[2]) + 1;
  return new Date(year, month, day);
};

export const formatYearMonth = (year: number, month: number): string => {
  return new Date(year, month, 1).toLocaleString('default', {
    year: '2-digit',
    month: 'short',
  });
};

export const formatElapsedTime = (date: Date, now: Date): string => {
  const diff = now.getTime() - date.getTime();
  const seconds = diff / 1000;
  if (seconds < 5) {
    return 'now';
  }
  if (seconds < 60) {
    return '<1m';
  }
  const minutes = seconds / 60;
  if (minutes < 60) {
    return `${Math.round(minutes)}m`;
  }
  const hours = minutes / 60;
  if (hours < 24) {
    return `${Math.round(hours)}h`;
  }
  const days = hours / 24;
  if (days <= 7) {
    return `${Math.round(days)}d`;
  }
  return '>7d';
};

export const formatFullDateTime = (date: Date): string => {
  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'medium',
    timeStyle: 'long',
  }).format(date);
};

export const formatFullDate = (date: Date): string => {
  return new Intl.DateTimeFormat('it-IT', {
    dateStyle: 'medium',
  }).format(date);
};

export const formatDayMonth = (date: Date): string => {
  return new Intl.DateTimeFormat('it-IT', {
    day: 'numeric',
    month: 'short',
  }).format(date);
};

export const compareDates = (d1: Date, d2: Date): number => {
  return d1.getTime() - d2.getTime();
};
