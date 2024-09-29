<template>
  <q-form @submit="onSubmit" class="q-gutter-y-md" style="max-width: 500px">
    <div>{{ date }} - {{ accountId }}</div>
    <DateSelector v-model="date" :mask="DateUtil.DATE_FORMAT" />
    <AccountSelector v-model="accountId" />
    <!--
    <q-select
      label="Categoria"
      outlined
      :options="categories"
      v-model="category"
      use-input
      input-debounce="0"
      new-value-mode="add-unique"
      :rules="[
        (val) => (val && val.trim().length !== 0) || 'Valore obbligatorio',
      ]"
    />
    <q-input outlined v-model="description" label="Descrizione" />
    <q-input
      outlined
      v-model="amount"
      :step="0.01"
      label="Importo"
      type="number"
      :rules="[(val) => (val && validateAmount(val)) || 'Valore obbligatorio']"
    />-->
    <q-btn label="Submit" type="submit" color="primary" />
  </q-form>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import DateSelector from './DateSelector.vue';
import { ExpenseType } from 'src/model/expense-type';
import DateUtil from 'src/utils/date-util';
import AccountSelector from './AccountSelector.vue';

interface Props {
  type: ExpenseType;
  expenseId?: string;
  oldDate?: Date;
  oldAccountId?: string;
}

const props = defineProps<Props>();

const date = ref<string>();
const accountId = ref<string>();

function onSubmit() {}

function initDate(newValue?: Date): void {
  if (newValue) {
    date.value = DateUtil.toQDateFormat(newValue, DateUtil.DATE_FORMAT);
  } else {
    date.value = DateUtil.toQDateFormat(new Date(), DateUtil.DATE_FORMAT);
  }
}

onMounted(() => {
  initDate(props.oldDate);
  if (props.oldAccountId) {
    accountId.value = props.oldAccountId;
  }
});

/*watch(
  () => props.oldDate,
  (newValue, oldValue) => {
    console.log('Change date', oldValue, newValue);
    initDate(newValue);
  },
);*/
</script>
