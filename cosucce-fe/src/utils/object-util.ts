export default class ObjectUtil {
  public static isNullOrUndefined(o: unknown): boolean {
    return o === null || o === undefined;
  }
  public static isNotNullOrUndefined(o: unknown): boolean {
    return o !== null && o !== undefined;
  }
}
