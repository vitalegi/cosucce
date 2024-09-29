<template>
  <q-page class="row content-start justify-evenly">
    <ExpenseEntryEditor
      :type="expense.category.type"
      :expense-id="expense.id"
    ></ExpenseEntryEditor>
  </q-page>
</template>
<script setup lang="ts">
import ExpenseEntryEditor from 'src/components/ExpenseEntryEditor.vue';
import { useExpenseStore } from 'src/stores/expenses-store';
import { computed } from 'vue';

interface Props {
  expenseId: string;
}

const props = defineProps<Props>();

const expenseStore = useExpenseStore();

const expense = computed(() => expenseStore.expense(props.expenseId));
</script>
