import { leftPadding } from 'src/utils/StringUtil';

export default class DateUtil {
  public static readonly DATE_FORMAT = 'DD/MM/YYYY';

  public static toQDateFormat = (date: Date, format: 'DD/MM/YYYY'): string => {
    const month = leftPadding(`${date.getMonth() + 1}`, 2, '0');
    const day = leftPadding(`${date.getDate()}`, 2, '0');
    if (format === DateUtil.DATE_FORMAT) {
      return `${day}/${month}/${date.getFullYear()}`;
    }
    throw new Error('unsupported format ' + format);
  };

  public static fromQDateFormat = (str: string, format: 'DD/MM/YYYY'): Date => {
    if (format === DateUtil.DATE_FORMAT) {
      const values = str.split('/');
      const year = parseInt(values[0]);
      const month = parseInt(values[1]) - 1;
      const day = parseInt(values[2]) + 1;
      return new Date(year, month, day);
    }
    throw new Error('unsupported format ' + format);
  };

  public static formatYearMonth = (year: number, month: number): string => {
    return new Date(year, month, 1).toLocaleString('default', {
      year: '2-digit',
      month: 'short',
    });
  };

  public static formatElapsedTime = (date: Date, now: Date): string => {
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

  public static formatFullDateTime = (date: Date): string => {
    return new Intl.DateTimeFormat('it-IT', {
      dateStyle: 'medium',
      timeStyle: 'long',
    }).format(date);
  };

  public static formatFullDate = (date: Date): string => {
    return new Intl.DateTimeFormat('it-IT', {
      dateStyle: 'medium',
    }).format(date);
  };

  public static formatDayMonth = (date: Date): string => {
    return new Intl.DateTimeFormat('it-IT', {
      day: 'numeric',
      month: 'short',
    }).format(date);
  };

  public static compareDates = (d1: Date, d2: Date): number => {
    return d1.getTime() - d2.getTime();
  };
}
