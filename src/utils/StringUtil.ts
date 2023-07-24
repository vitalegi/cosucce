export const leftPadding = (str: string, len: number, char: string): string => {
  while (str.length < len) {
    str = char + str;
  }
  return str;
};

export const isNotNullOrEmpty = (str: string): boolean => {
  return str !== null && str !== undefined && str.trim() !== '';
};
