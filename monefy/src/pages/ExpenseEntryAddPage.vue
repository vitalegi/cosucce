<template>
  <q-page class="row content-start justify-evenly">
    <ExpenseEntryEditor :type="type"></ExpenseEntryEditor>
  </q-page>
</template>
<script setup lang="ts">
import ExpenseEntryEditor from 'src/components/ExpenseEntryEditor.vue';
import { ExpenseType } from 'src/model/expense-type';

interface Props {
  type: ExpenseType;
}

defineProps<Props>();
</script>
