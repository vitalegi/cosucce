import NumberUtil from './NumberUtil';
import ObjectUtil from './ObjectUtil';

export const asIntNullable = (value: unknown): number | null => {
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
};

/**
 *
 * @param value the object to be parsed
 * @param defaultValue a number if value is optional, false if mandatory
 * @returns
 */
export const asInt = (
  value: unknown,
  defaultValue: number | false = false,
): number => {
  const v = asIntNullable(value);
  if (v === null) {
    if (defaultValue === false) {
      throw Error('Value is mandatory');
    }
    return defaultValue;
  }
  return v;
};

/**
 *
 * @param value the object to be parsed
 * @param defaultValue a number if value is optional, false if mandatory
 * @returns
 */
export const asDecimal = (
  value: unknown,
  defaultValue: number | false = false,
): number => {
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
};

/**
 *
 * @param value the object to be parsed
 * @param defaultValue a string if value is optional, false if mandatory
 * @returns
 */
export const asString = (
  value: unknown,
  defaultValue: string | false = false,
): string => {
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
};

export const asBoolean = (value: unknown, defaultValue: boolean): boolean => {
  if (ObjectUtil.isNullOrUndefined(value)) {
    return defaultValue;
  }
  if (typeof value === 'boolean') {
    return value;
  }
  throw Error(`Value is not a boolean ${value}: ${typeof value}`);
};

export const asDateOptional = (
  value: unknown,
  defaultValue?: Date,
): Date | undefined => {
  if (ObjectUtil.isNullOrUndefined(value)) {
    if (defaultValue) {
      return defaultValue;
    }
    return undefined;
  }
  if (value instanceof Date) {
    return value;
  }
  if (typeof value === 'string') {
    return new Date(Date.parse(value));
  }
  throw Error(`Value is not a Date ${value}: ${typeof value}`);
};

export const asDate = (value: unknown): Date => {
  const out = asDateOptional(value, undefined);
  if (out === undefined) {
    throw Error('Value is undefined');
  }
  return out;
};
