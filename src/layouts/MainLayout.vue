<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title class="title">
          <router-link to="/">Budget</router-link>
        </q-toolbar-title>
        <q-btn round flat icon="logout" @click="logout()" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item clickable tag="router-link" to="/profile">
          <q-item-section>
            <q-item-label> Il tuo profilo </q-item-label>
          </q-item-section>
        </q-item>
        <q-item-label header tag="router-link" to="/">
          Le tue board
        </q-item-label>

        <q-item
          v-for="board in boards"
          :key="board.id"
          clickable
          tag="router-link"
          :to="`/board/${board.id}`"
        >
          <q-item-section>
            <q-item-label>{{ board.name }}</q-item-label>
            <q-item-label caption>{{
              board.lastUpdate.toLocaleDateString()
            }}</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <div class="q-pa-md q-gutter-sm">
        <q-breadcrumbs>
          <q-breadcrumbs-el
            v-for="(breadcrumb, index) in breadcrumbs"
            :key="index"
            :label="breadcrumb.label"
            :to="breadcrumb.route"
          />
        </q-breadcrumbs>
      </div>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useBoardsStore } from 'src/budget/stores/boards-store';
import { useRouter } from 'vue-router';
import { useBreadcrumbsStore } from 'src/stores/breadcrumb-store';

const router = useRouter();

const boardsStore = useBoardsStore();
const boards = computed(() => boardsStore.boards);

const breadcrumbStore = useBreadcrumbsStore();
const breadcrumbs = computed(() => breadcrumbStore.breadcrumbs);

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

const logout = (): void => {
  router.push('/logout');
};
</script>

<style lang="scss" scoped>
.title {
  a {
    color: inherit;
  }

  a:link {
    text-decoration: none;
  }

  a:visited {
    text-decoration: none;
  }

  a:hover {
    text-decoration: none;
  }

  a:active {
    text-decoration: none;
  }
}
</style>
