<template>
  <q-slide-item
    @left="previous"
    @right="next"
    right-color="secondary"
    left-color="secondary"
  >
    <template v-slot:left v-if="showSlider"> </template>
    <template v-slot:right v-if="showSlider"> </template>

    <q-item>
      <q-item-section class="absolute-center text-subtitle1 text-weight-medium">
        {{ label }}
      </q-item-section>
    </q-item>
  </q-slide-item>
</template>
<script setup lang="ts">
import { useIntervalStore } from 'src/stores/interval-store';
import { computed, onBeforeUnmount } from 'vue';

const intervalStore = useIntervalStore();

const label = computed(() => intervalStore.label);

let timer: NodeJS.Timeout;

const showSlider = computed(() => intervalStore.interval !== 'all');

function next(details: { reset: () => void }) {
  intervalStore.next();
  finalize(details.reset);
}

function previous(details: { reset: () => void }) {
  intervalStore.previous();
  finalize(details.reset);
}

function finalize(reset: () => void) {
  timer = setTimeout(() => {
    reset();
  }, 100);
}

onBeforeUnmount(() => {
  clearTimeout(timer);
});
</script>
