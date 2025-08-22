<template>
  <q-layout view="hHh lpR lFr">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />

        <q-toolbar-title> Quasar App </q-toolbar-title>

        <q-btn
          dense
          flat
          round
          icon="settings"
          @click="toggleRightDrawer"
          v-if="mainLayoutStore.showRightMenu"
        />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header> Essential Links </q-item-label>

        <EssentialLink v-for="link in linksList" :key="link.title" v-bind="link" />
      </q-list>
    </q-drawer>

    <q-drawer
      v-model="rightDrawerOpen"
      side="right"
      overlay
      bordered
      v-if="mainLayoutStore.isBudgetView"
    >
      <div class="row items-start justify-start">
        <q-list bordered class="col-12">
          <q-item clickable v-ripple>
            <q-item-section @click="settingsAccounts()"> Accounts </q-item-section>
          </q-item>
          <q-item clickable v-ripple>
            <q-item-section @click="settingsCategories()"> Categories </q-item-section>
          </q-item>
        </q-list>
      </div>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import EssentialLink, { type EssentialLinkProps } from 'components/EssentialLink.vue';
import { authService } from 'src/services/backend-service';
import { useMainLayoutStore } from 'src/stores/main-layout-store';
import routing from 'src/router/routing';
import { useRouter } from 'vue-router';

const mainLayoutStore = useMainLayoutStore();

const router = useRouter();

const linksList: EssentialLinkProps[] = [
  {
    title: 'Docs',
    caption: 'quasar.dev',
    icon: 'school',
    link: 'https://quasar.dev',
  },
  {
    title: 'Github',
    caption: 'github.com/quasarframework',
    icon: 'code',
    link: 'https://github.com/quasarframework',
  },
  {
    title: 'Discord Chat Channel',
    caption: 'chat.quasar.dev',
    icon: 'chat',
    link: 'https://chat.quasar.dev',
  },
  {
    title: 'Forum',
    caption: 'forum.quasar.dev',
    icon: 'record_voice_over',
    link: 'https://forum.quasar.dev',
  },
  {
    title: 'Twitter',
    caption: '@quasarframework',
    icon: 'rss_feed',
    link: 'https://twitter.quasar.dev',
  },
  {
    title: 'Facebook',
    caption: '@QuasarFramework',
    icon: 'public',
    link: 'https://facebook.quasar.dev',
  },
  {
    title: 'Quasar Awesome',
    caption: 'Community Quasar projects',
    icon: 'favorite',
    link: 'https://awesome.quasar.dev',
  },
];

const leftDrawerOpen = ref(false);
const rightDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}
function toggleRightDrawer() {
  rightDrawerOpen.value = !rightDrawerOpen.value;
}

function settingsAccounts(): Promise<unknown> {
  return routing.budget().settingsViewAccounts(router, mainLayoutStore.boardId);
}
function settingsCategories(): Promise<unknown> {
  return routing.budget().settingsViewCategories(router, mainLayoutStore.boardId);
}

void authService.tokenRefresh();
</script>
