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
        <q-btn :draggable="false" color="grey-4" outline padding="sm xl">
          <ExpenseValue
            :amount="amount"
            currency=""
            :type="amountType"
          ></ExpenseValue>
        </q-btn>
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
import { computed } from 'vue';
import ExpenseUtil from 'src/utils/expense-util';
import { useExpenseStore } from 'src/stores/expenses-store';
import ExpenseValue from 'src/components/ExpenseValue.vue';
import BigDecimalUtil from 'src/utils/big-decimal-util';

defineOptions({
  name: 'MonefyHomePage',
});

const intervalStore = useIntervalStore();
const expenseStore = useExpenseStore();
const router = useRouter();

const amountRaw = computed(() => {
  const credit = ExpenseUtil.sum(
    expenses.value.filter((e) => e.category.type === 'credit'),
  );
  const debit = ExpenseUtil.sum(
    expenses.value.filter((e) => e.category.type === 'debit'),
  );
  return credit.subtract(debit);
});

const amount = computed(() => {
  return amountRaw.value.getValue();
});

const amountType = computed(() =>
  amountRaw.value.compareTo(BigDecimalUtil.ZERO) >= 0 ? 'credit' : 'debit',
);

const expenses = computed(() =>
  expenseStore.expensesInInterval(intervalStore.from, intervalStore.to),
);

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

.big-button.debit {
  color: $debit;
}
.big-button.credit {
  color: $credit;
}
</style>
