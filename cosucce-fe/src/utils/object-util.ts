import NumberUtil from './number-util';

export default class ObjectUtil {
  public static isNullOrUndefined(o: unknown): boolean {
    return o === null || o === undefined;
  }
  public static isNotNullOrUndefined(o: unknown): boolean {
    return o !== null && o !== undefined;
  }
  public static getProperty(o: unknown, prop: string): unknown {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    return (o as any)[prop];
  }
  /**
   *
   * @param value the object to be parsed
   * @param defaultValue a string if value is optional, false if mandatory
   * @returns
   */
  public static asString(value: unknown, defaultValue: string | false = false): string {
    if (ObjectUtil.isNullOrUndefined(value)) {
      if (defaultValue === false) {
        throw Error('Value is mandatory');
      }
      return defaultValue;
    }
    if (typeof value === 'string') {
      return value;
    }
    throw Error(`Value is not a string ${value}: ${typeof value}`);
  }

  public static asStringNullable(value: unknown): string | null {
    if (ObjectUtil.isNullOrUndefined(value)) {
      return null;
    }
    return ObjectUtil.asString(value);
  }

  public static propAsString(
    obj: unknown,
    key: string,
    defaultValue: string | false = false,
  ): string {
    const value = ObjectUtil.getProperty(obj, key);
    return ObjectUtil.asString(value, defaultValue);
  }

  public static asIntNullable(value: unknown): number | null {
    if (ObjectUtil.isNullOrUndefined(value)) {
      return null;
    }
    if (typeof value === 'number') {
      return value;
    }
    if (typeof value === 'string') {
      return NumberUtil.parseAsInt(value);
    }
    throw Error(`Value is not a number ${value}: ${typeof value}`);
  }

  /**
   *
   * @param value the object to be parsed
   * @param defaultValue a number if value is optional, false if mandatory
   * @returns
   */
  public static asInt(value: unknown, defaultValue: number | false = false): number {
    const v = ObjectUtil.asIntNullable(value);
    if (v === null) {
      if (defaultValue === false) {
        throw Error('Value is mandatory');
      }
      return defaultValue;
    }
    return v;
  }

  public static propAsInt(obj: unknown, key: string, defaultValue: number | false = false): number {
    const value = ObjectUtil.getProperty(obj, key);
    return ObjectUtil.asInt(value, defaultValue);
  }

  /**
   *
   * @param value the object to be parsed
   * @param defaultValue a number if value is optional, false if mandatory
   * @returns
   */
  public static asDecimal(value: unknown, defaultValue: number | false = false): number {
    if (ObjectUtil.isNullOrUndefined(value)) {
      if (defaultValue === false) {
        throw Error('Value is mandatory');
      }
      return defaultValue;
    }
    if (typeof value === 'number') {
      return value;
    }
    if (typeof value === 'string') {
      return NumberUtil.parseAsDecimal(value);
    }
    throw Error(`Value is not a number ${value}: ${typeof value}`);
  }

  public static asBoolean(value: unknown, defaultValue: boolean | undefined): boolean {
    if (ObjectUtil.isNullOrUndefined(value)) {
      if (defaultValue === undefined) {
        throw Error('Value is mandatory');
      }
      return defaultValue;
    }
    if (typeof value === 'boolean') {
      return value;
    }
    throw Error(`Value is not a boolean ${value}: ${typeof value}`);
  }

  public static propAsBoolean(
    obj: unknown,
    key: string,
    defaultValue: boolean | undefined = undefined,
  ): boolean {
    const value = ObjectUtil.getProperty(obj, key);
    return ObjectUtil.asBoolean(value, defaultValue);
  }

  public static asDateOptional(value: unknown, defaultValue?: Date): Date | null {
    if (ObjectUtil.isNullOrUndefined(value)) {
      if (defaultValue) {
        return defaultValue;
      }
      return null;
    }
    if (value instanceof Date) {
      return value;
    }
    if (typeof value === 'string') {
      return new Date(Date.parse(value));
    }
    throw Error(`Value is not a Date ${value}: ${typeof value}`);
  }

  public static asDate(value: unknown, defaultValue: Date | false = false): Date {
    const v = ObjectUtil.asDateOptional(value, undefined);
    if (v === null) {
      if (defaultValue === false) {
        throw Error('Value is mandatory');
      }
      return defaultValue;
    }
    return v;
  }

  public static propAsDate(obj: unknown, key: string, defaultValue: Date | false = false): Date {
    const value = ObjectUtil.getProperty(obj, key);
    return ObjectUtil.asDate(value, defaultValue);
  }
}
