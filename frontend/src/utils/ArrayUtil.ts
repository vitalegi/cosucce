export default class ArrayUtil {
  static distinct<E>(list: E[]): E[] {
    return [...new Set(list)];
  }
}
