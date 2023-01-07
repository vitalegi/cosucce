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

        <q-toolbar-title>
          Quasar App <router-link to="/">home</router-link> |
          <router-link to="/auth">auth</router-link> |
          <router-link to="/tmp">tmp</router-link>
        </q-toolbar-title>
        <div>Quasar v{{ $q.version }}</div>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header> Le tue board </q-item-label>

        <q-item
          v-for="board in boards"
          :key="board.id"
          clickable
          tag="router-link"
          :to="board.id"
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
import { ref } from 'vue';
import boardService from 'src/integrations/BoardService';
import Board from 'src/models/Board';

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

const boards = ref(new Array<Board>());
boardService.getBoards().then((b) => boards.value.push(...b));
</script>
