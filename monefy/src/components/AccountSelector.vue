<template>
  <q-select
    label="Account"
    outlined
    :options="accounts"
    v-model="model"
    option-value="id"
    option-label="name"
    map-options
    emit-value
    :rules="[(val) => (val && val.trim().length !== 0) || 'Value is mandatory']"
  />
</template>

<script setup lang="ts">
import { useExpenseStore } from 'src/stores/expenses-store';
import { computed } from 'vue';

const model = defineModel<string>();

const expenseStore = useExpenseStore();

const accounts = computed(() => expenseStore.accounts);
</script>
