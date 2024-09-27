<template>
  <q-page class="row content-start justify-evenly">
    <div
      class="row col-12 justify-center"
      v-touch-swipe.mouse.right.left="handleSwipe"
    >
      {{ intervalStore.label }}
    </div>
    <div
      class="row col-12 justify-center"
      v-touch-swipe.mouse.right.left="handleSwipe"
    >
      <q-btn :draggable="false" class="balance credit" padding="sm xl"
        >saldo €</q-btn
      >
    </div>
    <div class="row col-12 justify-center">
      <q-btn>saldo</q-btn>
    </div>
    <div class="row col-12 justify-center">categorie</div>
    <div
      class="col-12 row items-center justify-evenly"
      style="max-width: 600px"
    >
      <q-btn size="40px" round outline icon="remove" class="big-button debit" />
      <q-btn size="40px" round outline icon="add" class="big-button credit" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { Notify } from 'quasar';
import { useIntervalStore } from 'src/stores/interval-store';

defineOptions({
  name: 'MonefyHomePage',
});

const intervalStore = useIntervalStore();

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
.balance {
  color: white;
}
.balance.debit {
  background: $debit;
}
.balance.credit {
  background: $credit;
}

.big-button.debit {
  color: $debit;
}
.big-button.credit {
  color: $credit;
}
</style>
