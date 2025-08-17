export default interface LocalPersistence<E> {
  executeLocal(entity: E): Promise<void>;
}
