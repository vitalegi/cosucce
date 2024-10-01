<template>
  <q-page class="row content-start justify-evenly">
    <div class="main-content col-12">
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
        <div style="max-width: 600px; width: 100%">
          <ExpensesByCategories></ExpensesByCategories>
        </div>
      </div>
    </div>
    <div class="fixed-bottom row justify-evenly">
      <div
        class="col-12 row items-center justify-evenly"
        style="max-width: 600px"
      >
        <q-btn
          size="40px"
          round
          outline
          icon="remove"
          class="big-button debit"
          @click="addDebit()"
        />
        <q-btn
          size="40px"
          round
          outline
          icon="add"
          class="big-button credit"
          @click="addCredit()"
        />
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { Notify } from 'quasar';
import ExpensesByCategories from 'components/ExpensesByCategories.vue';
import { useIntervalStore } from 'src/stores/interval-store';
import { useRouter } from 'vue-router';

defineOptions({
  name: 'MonefyHomePage',
});

const intervalStore = useIntervalStore();
const router = useRouter();

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

function addCredit() {
  router.push('/add/credit');
}

function addDebit() {
  router.push('/add/debit');
}
</script>

<style scoped lang="scss">
.main-content {
  max-height: calc(100vh - 175px);
  overflow: auto;
}
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
