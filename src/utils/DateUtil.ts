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
