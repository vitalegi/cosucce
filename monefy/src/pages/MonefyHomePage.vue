<template>
  <q-page
    class="row content-start justify-evenly"
    v-touch-swipe.mouse.right.left="handleSwipe"
  >
    <div class="row col-12 justify-center">data</div>
    <div class="row col-12 justify-center">
      <q-btn>saldo</q-btn>
    </div>
    <div class="row col-12 justify-center">categorie</div>
    <div
      class="col-12 row items-center justify-evenly"
      style="max-width: 800px"
    >
      <q-btn size="xl" round outline icon="add" class="big-button credit" />
      <q-btn size="xl" round outline icon="remove" class="big-button debit" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { Notify } from 'quasar';

defineOptions({
  name: 'MonefyHomePage',
});

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function handleSwipe(e: any) {
  const direction = e.direction;
  if (direction === 'left') {
    Notify.create('Left!');
  }
  if (direction === 'right') {
    Notify.create('Right!');
  }
}
</script>

<style lang="scss">
.debit {
  color: $debit;
}
.credit {
  color: $credit;
}
</style>
