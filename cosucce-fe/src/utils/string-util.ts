export default class StringUtil {
  public static leftPadding = (str: string, len: number, char: string): string => {
    while (str.length < len) {
      str = char + str;
    }
    return str;
  };

  public static isNotNullOrEmpty = (str: string): boolean => {
    return str !== null && str !== undefined && str.trim() !== '';
  };
}
