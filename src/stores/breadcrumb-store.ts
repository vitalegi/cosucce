import { defineStore } from 'pinia';
import { RouteLocationNormalized } from 'vue-router';

export class Breadcrumb {
  label = '';
  icon = '';
  route = '';

  constructor(label: string, icon: string, route: string) {
    this.label = label;
    this.icon = icon;
    this.route = route;
  }
  equals = (other: Breadcrumb): boolean => {
    return (
      this.label === other.label &&
      this.icon === other.icon &&
      this.route === other.route
    );
  };
}

const home = (): Breadcrumb => new Breadcrumb('Home', '', '/');

const getBoardId = (route: RouteLocationNormalized): string => {
  if (typeof route.params.boardId === 'string') {
    return route.params.boardId;
  }
  return route.params.boardId[0];
};

const board = (route: RouteLocationNormalized): Breadcrumb =>
  new Breadcrumb('Board', '', `/board/${getBoardId(route)}`);

const boardSettings = (route: RouteLocationNormalized): Breadcrumb =>
  new Breadcrumb('Settings', '', `/board/${getBoardId(route)}/settings`);

export const getBreadcrumbs = (
  route: RouteLocationNormalized
): Breadcrumb[] => {
  if (!route.meta.breadcrumb) {
    return [home()];
  }
  const type = route.meta.breadcrumb;
  if (type === 'BOARD') {
    return [home(), board(route)];
  }
  if (type === 'BOARD_JOIN') {
    return [home(), new Breadcrumb('Join', '', '/board/join')];
  }
  if (type === 'BOARD_SETTINGS') {
    return [home(), board(route), boardSettings(route)];
  }
  if (type === 'BOARD_ENTRY_ADD') {
    return [
      home(),
      board(route),
      new Breadcrumb('Nuova spesa', '', `/board/${getBoardId(route)}/add`),
    ];
  }
  if (type === 'BOARD_ENTRY_EDIT') {
    return [
      home(),
      board(route),
      new Breadcrumb('Modifica spesa', '', `/board/${getBoardId(route)}/edit`),
    ];
  }
  if (type === 'BOARD_SPLIT_ADD') {
    return [
      home(),
      board(route),
      boardSettings(route),
      new Breadcrumb(
        'Nuovo split',
        '',
        `/board/${getBoardId(route)}/settings/split/edit`
      ),
    ];
  }
  if (type === 'BOARD_SPLIT_EDIT') {
    return [
      home(),
      board(route),
      boardSettings(route),
      new Breadcrumb(
        'Modifica split',
        '',
        `/board/${getBoardId(route)}/settings/split/edit`
      ),
    ];
  }
  return [home()];
};

export const useBreadcrumbsStore = defineStore('breadcrumb', {
  state: () => ({
    breadcrumbs: [home()],
  }),
  getters: {
    getBreadcrumbs: (state) => state.breadcrumbs,
  },
  actions: {
    add(entry: Breadcrumb) {
      this.breadcrumbs.push(entry);
    },
    removeFrom(entry: Breadcrumb) {
      const index = this.breadcrumbs.findIndex((b) => b.equals(entry));
      if (index !== -1) {
        this.breadcrumbs.splice(index);
      }
    },
    refresh(entries: Breadcrumb[]) {
      this.breadcrumbs = entries;
    },
  },
});
