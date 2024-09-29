<template>
  <q-form
    @submit="onSubmit"
    class="col-12 q-pa-md q-gutter-y-md"
    style="max-width: 600px"
    greedy
  >
    <DateSelector v-model="date" :mask="DateUtil.DATE_FORMAT" />
    <AccountSelector v-model="accountId" />
    <CategorySelector v-model="categoryId" :type="type" />
    <q-input outlined v-model="description" label="Description" />
    <AmountSelector v-model="amount" />

    <q-btn
      label="Add"
      type="submit"
      color="primary"
      size="24px"
      style="width: 100%"
    />
  </q-form>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import DateSelector from './DateSelector.vue';
import { ExpenseType } from 'src/model/expense-type';
import DateUtil from 'src/utils/date-util';
import AccountSelector from './AccountSelector.vue';
import CategorySelector from './CategorySelector.vue';
import AmountSelector from './AmountSelector.vue';

interface Props {
  type: ExpenseType;
  expenseId?: string;
  oldDate?: Date;
  oldAccountId?: string;
  oldCategoryId?: string;
  oldDescription?: string;
  oldAmount?: string;
}

const props = defineProps<Props>();

const emit = defineEmits(['submit']);

const date = ref<string>('');
const accountId = ref<string>('');
const categoryId = ref<string>('');
const description = ref<string>('');
const amount = ref<string>('');

function onSubmit() {
  const mappedDate = DateUtil.fromQDateFormat(date.value, DateUtil.DATE_FORMAT);
  emit('submit', {
    date: mappedDate,
    accountId: accountId.value,
    categoryId: categoryId.value,
    description: description.value,
    amount: amount.value,
  });
}

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
  if (props.oldCategoryId) {
    categoryId.value = props.oldCategoryId;
  }
  if (props.oldDescription) {
    description.value = props.oldDescription;
  }
  if (props.oldAmount) {
    amount.value = props.oldAmount;
  }
});
</script>
