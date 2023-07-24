import { asDecimal } from 'src/utils/JsonUtil';

export default class NumberUtil {
  public static formatInt(n: number): string {
    return n.toLocaleString(undefined, {
      maximumFractionDigits: 0,
    });
  }
  public static isNumber(s: string): boolean {
    return !isNaN(NumberUtil.parseAsInt(s));
  }
  public static parseAsInt(s: string): number {
    return parseInt(s, 10);
  }
  public static parseAsDecimal(s: string): number {
    return parseFloat(s);
  }

  public static parseHex(s: string): number {
    if (!s.startsWith('0x')) {
      throw Error(`Cannot parse ${s} to hex. expected format 0x123456`);
    }
    return parseInt(s.substring(2), 16);
  }

  public static min(
    numbers: number[],
    defaultValue = Number.MAX_SAFE_INTEGER
  ): number {
    if (numbers.length === 0) {
      return defaultValue;
    }
    return numbers.reduce((prev, curr) => Math.min(prev, curr));
  }
  public static max(numbers: number[], defaultValue = 0): number {
    if (numbers.length === 0) {
      return defaultValue;
    }
    return numbers.reduce((prev, curr) => Math.max(prev, curr));
  }
  public static sum(numbers: number[]): number {
    return numbers.reduce((prev, curr) => prev + curr, 0);
  }
  public static formatCurrency(value: number | string): string {
    let v;
    if (typeof value === 'number') {
      v = value;
    } else {
      v = asDecimal(value);
    }
    return new Intl.NumberFormat('it-IT', {
      style: 'currency',
      currency: 'EUR',
    }).format(v);
  }

  public static formatPercentage(value: number): string {
    return (
      new Intl.NumberFormat('it-IT', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2,
      }).format(value * 100) + '%'
    );
  }
}
