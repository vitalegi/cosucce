<template>
  <q-table
    :dense="true"
    :flat="true"
    :rows="entries"
    :columns="columns"
    row-key="name"
    :binary-state-sort="true"
    :pagination="pagination"
    :rows-per-page-options="[12, 24, 36, 0]"
  />
</template>

<script setup lang="ts">
import MonthlyUserAnalysis from 'src/models/budget/analysis/MonthlyUserAnalysis';
import UserAmount from 'src/models/budget/analysis/UserAmount';
import UserData from 'src/models/UserData';
import { formatYearMonth } from 'src/utils/DateUtil';
import { asDecimal } from 'src/utils/JsonUtil';
import NumberUtil from 'src/utils/NumberUtil';
import { computed, PropType } from 'vue';

const props = defineProps({
  user: {
    type: UserData,
    required: true,
  },
  entries: {
    type: Array as PropType<Array<MonthlyUserAnalysis>>,
    required: true,
  },
});

const columns = computed(() => {
  const cols = [
    {
      name: 'date',
      required: true,
      label: 'Data',
      field: (row: MonthlyUserAnalysis) => row.year * 12 + row.month,
      format: (val: string, row: MonthlyUserAnalysis) =>
        formatYearMonth(row.year, row.month - 1),
      align: 'left',
      sortable: true,
    },
    {
      name: 'actual',
      required: true,
      label: 'Attuale',
      field: (row: MonthlyUserAnalysis) => getUserActual(row),
      format: (val: string) => formatCurrency(val),
      sortable: true,
    },
    {
      name: 'expected',
      required: true,
      label: 'Attesa',
      field: (row: MonthlyUserAnalysis) => getUserExpected(row),
      format: (val: string) => formatCurrency(val),
      sortable: true,
    },
    {
      name: 'monthCredit',
      required: true,
      label: 'Credito mensile',
      field: (row: MonthlyUserAnalysis) =>
        getUserExpected(row) - getUserActual(row),
      format: (val: string) => formatCurrency(val),
      sortable: true,
    },
    {
      name: 'monthCreditCumulated',
      required: true,
      label: 'Credito cumulato',
      field: (row: MonthlyUserAnalysis) => getCumulatedCredit(row),
      format: (val: string) => formatCurrency(val),
      sortable: true,
    },
  ];
  return cols;
});

const pagination = {
  sortBy: 'date',
  descending: true,
};

const getUser = (row: MonthlyUserAnalysis): UserAmount => {
  return row.users.filter((u) => u.userId === props.user.id)[0];
};

const getUserActual = (row: MonthlyUserAnalysis): number => {
  const user = getUser(row);
  if (user) {
    return user.actual;
  }
  return 0;
};
const getUserExpected = (row: MonthlyUserAnalysis): number => {
  const user = getUser(row);
  if (user) {
    return user.expected;
  }
  return 0;
};

const getCumulatedCredit = (row: MonthlyUserAnalysis): number => {
  const user = getUser(row);
  if (user) {
    return user.cumulatedCredit;
  }
  return 0;
};

const formatCurrency = (value: string) => NumberUtil.formatCurrency(value);
</script>
