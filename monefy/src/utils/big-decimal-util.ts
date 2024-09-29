import bigDecimal from 'js-big-decimal';

export default class BigDecimalUtil {
  public static readonly ZERO = new bigDecimal('0');

  public static format(n: bigDecimal): {
    integerPart: string;
    decimalPart: string;
  } {
    return {
      integerPart: BigDecimalUtil.getIntegerPart(n).getPrettyValue(3, '.'),
      decimalPart: BigDecimalUtil.getDecimalPart(n, 2).getValue().substring(2),
    };
  }

  public static round(val: bigDecimal, precision: number): bigDecimal {
    return val.round(precision, bigDecimal.RoundingModes.HALF_DOWN);
  }

  public static getIntegerPart(val: bigDecimal): bigDecimal {
    if (val.compareTo(BigDecimalUtil.ZERO) >= 0) {
      return val.round(0, bigDecimal.RoundingModes.DOWN);
    } else {
      return val.round(0, bigDecimal.RoundingModes.UP);
    }
  }

  public static getDecimalPart(val: bigDecimal, precision: number): bigDecimal {
    const abs = val.abs();
    const integerPart = BigDecimalUtil.getIntegerPart(abs);
    return abs
      .subtract(integerPart.floor())
      .round(precision, bigDecimal.RoundingModes.DOWN);
  }
}
