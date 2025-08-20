import { date } from 'quasar';

export default class DateUtil {
  public static readonly Q_DATE_FORMAT = 'dd/MM/yyyy';
  public static readonly Q_DATE_MASK = 'DD/MM/YYYY';

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

  public static compareDates = (d1: Date, d2: Date): number => {
    return d1.getTime() - d2.getTime();
  };

  public static timestamp() {
    return Date.now();
  }

  public static timeDiff(time: Date) {
    const minutes = date.getDateDiff(new Date(), time, 'minutes');
    if (minutes == 0) {
      return 'now';
    }
    if (minutes < 60) {
      return `${minutes}m ago`;
    }
    const hours = date.getDateDiff(new Date(), time, 'hours');
    if (hours === 0) {
      return '<1h ago';
    }
    if (hours < 24) {
      return `${hours}h ago`;
    }
    const days = date.getDateDiff(new Date(), time, 'days');
    return `${days}d ago`;
  }
}
