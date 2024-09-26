<template>
  <q-layout view="hHh lpR fFf">
    <q-header reveal class="bg-primary text-white">
      <q-toolbar>
        <q-btn dense flat round icon="menu" @click="toggleLeftDrawer" />

        <q-toolbar-title> Monefy </q-toolbar-title>

        <q-btn dense flat round icon="menu" @click="toggleRightDrawer" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" side="left" overlay bordered>
      <div class="row items-start justify-start q-px-md">
        <q-select
          class="col-12 q-py-md"
          color="secondary"
          outlined
          v-model="account"
          :options="['Tutti gli account', 'CC 1', 'Prepagata', 'Contanti']"
          label="Outlined"
        />
        <div class="col-12 q-gutter-sm">
          <q-btn
            color="secondary"
            outline
            label="Giorno"
            class="time-interval"
          />
          <q-btn
            color="secondary"
            outline
            label="Settimana"
            class="time-interval"
          />
          <q-btn color="secondary" outline label="Mese" class="time-interval" />
          <q-btn color="secondary" outline label="Anno" class="time-interval" />
        </div>
      </div>
    </q-drawer>

    <q-drawer v-model="rightDrawerOpen" side="right" overlay bordered>
      <!-- drawer content -->
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const leftDrawerOpen = ref(false);
const rightDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

function toggleRightDrawer() {
  rightDrawerOpen.value = !rightDrawerOpen.value;
}

const account = ref('*');
</script>

<style lang="scss" scoped>
.time-interval {
  width: 100%;
  min-height: 56px;
}
</style>
