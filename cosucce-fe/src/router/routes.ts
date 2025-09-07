import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      { path: 'budget', component: () => import('/src/budget/pages/ViewBoardsPage.vue') },

      {
        path: 'budget/add-board',
        component: () => import('/src/budget/pages/AddBoardPage.vue'),
      },
      {
        path: 'budget/board/:boardId',
        component: () => import('/src/budget/pages/BoardPage.vue'),
      },
      {
        path: 'budget/board/:boardId/add-entry',
        component: () => import('/src/budget/pages/AddBoardEntryPage.vue'),
      },
      {
        path: 'budget/board/:boardId/settings/',
        children: [
          {
            path: 'accounts',
            component: () => import('/src/budget/pages/ViewAccountsPage.vue'),
          },
          {
            path: 'add-account',
            component: () => import('/src/budget/pages/AddBoardAccountPage.vue'),
          },
          {
            path: 'account/:accountId',
            component: () => import('/src/budget/pages/UpdateBoardAccountPage.vue'),
          },
          {
            path: 'categories',
            component: () => import('/src/budget/pages/ViewCategoriesPage.vue'),
          },
        ],
      },
    ],
  },
  {
    path: '/oidc/',
    component: () => import('layouts/SsoLayout.vue'),
    children: [
      {
        name: 'LoginFlow',
        path: 'login',
        component: () => import('pages/OidcLoginFlowPage.vue'),
      },
      {
        name: 'LogoutFlow',
        path: 'logout',
        component: () => import('pages/OidcLogoutFlowPage.vue'),
      },
    ],
  },
  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
