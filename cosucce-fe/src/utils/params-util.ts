import type { LocationQueryValue } from 'vue-router';

export function castParamToString(
  param: string[] | string | undefined,
): string {
  if (param === undefined) {
    return '';
  }
  if (typeof param === 'string') {
    return param;
  } else {
    return param[0] ? param[0] : '';
  }
}

export function castQueryParamToStrings(
  param: LocationQueryValue | LocationQueryValue[],
): string | string[] {
  if (typeof param === 'string') {
    return param;
  }
  if (Array.isArray(param) && param.length > 0) {
    if (typeof param[0] === 'string') {
      return param.filter((p) => typeof p === 'string').map((p) => p);
    }
  }
  throw Error('Param is missing');
}

export function castQueryParamToString(
  param: LocationQueryValue | LocationQueryValue[],
): string {
  const value = castQueryParamToStrings(param);
  if (typeof value === 'string') {
    return value;
  }
  if (value.length > 0) {
    return value[0] ? value[0] : '';
  }
  throw Error('Empty value');
}
