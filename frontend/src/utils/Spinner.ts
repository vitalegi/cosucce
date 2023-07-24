import {
  Loading,

  // optional!, for example below
  // with custom spinner
  QSpinnerGears,
} from 'quasar';

class Spinner {
  show = (): void => {
    Loading.show({
      spinner: QSpinnerGears,
      delay: 200,
      // other props
    });
  };
  hide = (): void => {
    Loading.hide();
  };
  sync = async <E>(fn: () => Promise<E>): Promise<E> => {
    try {
      this.show();
      return await fn();
    } finally {
      this.hide();
    }
  };
}
const spinner = new Spinner();

export default spinner;
