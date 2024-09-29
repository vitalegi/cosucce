<template>
  <q-list bordered class="rounded-borders">
    <q-expansion-item
      switch-toggle-side
      expand-separator
      v-for="category in categories"
      :key="category.id"
      icon="perm_identity"
      :label="category.name"
    >
      <q-item
        v-for="expense in expensesWithCategory(category.id)"
        :key="expense.id"
      >
        <q-item-section avatar top> </q-item-section>

        <q-item-section top>
          <q-item-label>
            <span class="text-weight-medium">
              {{ formatAmountIntPart(expense.amount) }}</span
            >
            <span class="text-grey-8"
              >,{{ formatAmountDecimalPart(expense.amount) }}
              {{ expense.account.currency }}</span
            >
            &nbsp; &nbsp; &nbsp;
            <span class="text-grey-8"> {{ expense.description }}</span>
          </q-item-label>
        </q-item-section>

        <q-item-section top side>
          <q-item-label class="q-mt-sm">{{
            formatDayMonth(expense.date)
          }}</q-item-label>
        </q-item-section>
      </q-item>
    </q-expansion-item>
  </q-list>
</template>

<script setup lang="ts">
//import bigDecimal from 'js-big-decimal';
import bigDecimal from 'js-big-decimal';
import Expense from 'src/model/expense';
import { useExpenseStore } from 'src/stores/expenses-store';
import { useIntervalStore } from 'src/stores/interval-store';
import BigDecimalUtil from 'src/utils/big-decimal-util';
import { formatDayMonth } from 'src/utils/DateUtil';
import { computed } from 'vue';

const intervalStore = useIntervalStore();
const expenseStore = useExpenseStore();

const expenses = computed(() =>
  expenseStore.expensesInInterval(intervalStore.from, intervalStore.to),
);

const categories = computed(() => expenseStore.categories(expenses.value));

function expensesWithCategory(categoryId: string): Expense[] {
  return expenses.value.filter((e) => e.category.id === categoryId);
}

function formatAmountIntPart(amount: string): string {
  const val = new bigDecimal(amount);
  const parts = BigDecimalUtil.format(val);
  return parts.integerPart;
}

function formatAmountDecimalPart(amount: string): string {
  const val = new bigDecimal(amount);
  const parts = BigDecimalUtil.format(val);
  return parts.decimalPart;
}
</script>
