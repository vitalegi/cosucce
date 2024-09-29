<template>
  <q-expansion-item dense switch-toggle-side expand-separator>
    <template v-slot:header>
      <q-item-section avatar>
        <q-avatar icon="perm_identity" color="primary" text-color="white" />
      </q-item-section>

      <q-item-section>
        {{ category.name }}
      </q-item-section>

      <q-item-section side>
        <q-item-label>
          <ExpenseValue
            :amount="amount"
            currency=""
            :type="category.type"
          ></ExpenseValue>
        </q-item-label>
      </q-item-section>
    </template>

    <ExpenseItem
      v-for="expense in expensesWithCategory"
      :key="expense.id"
      :expense="expense"
      dense
    >
    </ExpenseItem>
  </q-expansion-item>
</template>

<script setup lang="ts">
import Category from 'src/model/category';
import { computed } from 'vue';
import ExpenseItem from './ExpenseItem.vue';
import ExpenseValue from './ExpenseValue.vue';
import Expense from 'src/model/expense';
import ExpenseUtil from 'src/utils/expense-util';

interface Props {
  category: Category;
  expenses: Expense[];
}

const props = defineProps<Props>();

const expensesWithCategory = computed(() => {
  return ExpenseUtil.sortExpensesByDate(
    ExpenseUtil.filterByCategory(props.expenses, props.category.id),
    false,
  );
});

const amount = computed(() =>
  ExpenseUtil.sum(expensesWithCategory.value).getValue(),
);
</script>
