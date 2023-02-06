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
        <router-link to="/logout">Esci</router-link>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
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
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useBoardsStore } from 'src/budget/stores/boards-store';

const boardsStore = useBoardsStore();
const boards = computed(() => boardsStore.boards);


const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}
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
