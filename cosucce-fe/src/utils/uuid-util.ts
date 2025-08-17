import { v4 as uuidv4 } from 'uuid';

export default class UuidUtil {
  public static uuid(): string {
    return uuidv4();
  }
}
