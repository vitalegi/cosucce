<template>
  <q-item>
    <q-item-section avatar top> </q-item-section>

    <q-item-section top>
      <q-item-label>
        <ExpenseValue
          :amount="expense.amount"
          :currency="expense.account.currency"
          :type="expense.category.type"
        ></ExpenseValue>
        &nbsp; &nbsp; &nbsp;
        <span class="text-grey-8"> {{ expense.description }}</span>
      </q-item-label>
    </q-item-section>

    <q-item-section top side>
      <q-item-label class="q-mt-sm">{{
        DateUtil.formatDayMonth(expense.date)
      }}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script setup lang="ts">
import Expense from 'src/model/expense';
import DateUtil from 'src/utils/date-util';
import ExpenseValue from './ExpenseValue.vue';

interface Props {
  expense: Expense;
}

defineProps<Props>();
</script>
