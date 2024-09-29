<template>
  <q-page class="row content-start justify-evenly">
    <ExpenseEntryEditor :type="type" @submit="add"></ExpenseEntryEditor>
  </q-page>
</template>
<script setup lang="ts">
import { Notify } from 'quasar';
import ExpenseEntryEditor from 'src/components/ExpenseEntryEditor.vue';
import { ExpenseType } from 'src/model/expense-type';
import { useExpenseStore } from 'src/stores/expenses-store';
import { useRouter } from 'vue-router';

interface Props {
  type: ExpenseType;
}

const props = defineProps<Props>();

const expenseStore = useExpenseStore();

const router = useRouter();

async function add(evt: {
  date: Date;
  accountId: string;
  categoryId: string;
  description: string;
  amount: string;
}): Promise<void> {
  try {
    await expenseStore.addExpense(
      evt.date,
      props.type,
      evt.accountId,
      evt.categoryId,
      evt.amount,
      evt.description,
    );
    router.push('/');
  } catch (e) {
    Notify.create('Error: ' + e);
  }
}
</script>
