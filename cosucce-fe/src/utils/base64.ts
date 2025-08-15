export default class Base64Utils {
  static encode(str: string): string {
    return btoa(str);
  }
  static encodeUrl(str: string): string {
    return encodeURIComponent(btoa(str));
  }
  static decode(str: string): string {
    return atob(str);
  }
  static decodeUrl(str: string): string {
    return atob(decodeURIComponent(str));
  }
}
