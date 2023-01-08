export const leftPadding = (str: string, len: number, char: string): string => {
  while (str.length < len) {
    str = char + str;
  }
  return str;
};
