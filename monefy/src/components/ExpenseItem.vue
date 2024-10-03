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
      <q-item-label class="q-mt-sm">
        {{ dateLabel }}
      </q-item-label>
    </q-item-section>
  </q-item>
</template>

<script setup lang="ts">
import Expense from 'src/model/expense';
import ExpenseValue from './ExpenseValue.vue';
import { computed } from 'vue';
import { format } from 'date-fns';

interface Props {
  expense: Expense;
}

const dateLabel = computed(() => format(props.expense.date, 'd LLL'));

const props = defineProps<Props>();
</script>
