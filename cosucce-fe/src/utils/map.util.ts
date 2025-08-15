export default class MapUtil {
  public static getOrDefault<K, E>(map: Map<K, E>, key: K, defaultValue: E): E {
    const v = map.get(key);
    if (v !== undefined) {
      return v;
    }
    return defaultValue;
  }
}
