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
        formatYearMonth(row.year, row.month),
      align: 'left',
      sortable: true,
    },
    {
      name: 'actual',
      required: true,
      label: 'Attuale',
      field: (row: MonthlyUserAnalysis) => getUser(row).actual,
      format: (val: string) => formatCurrency(val),
      sortable: true,
    },
    {
      name: 'expected',
      required: true,
      label: 'Attesa',
      field: (row: MonthlyUserAnalysis) => getUser(row).expected,
      format: (val: string) => formatCurrency(val),
      sortable: true,
    },
    {
      name: 'monthCredit',
      required: true,
      label: 'Credito mensile',
      field: (row: MonthlyUserAnalysis) => {
        const entry = getUser(row);
        return entry.expected - entry.actual;
      },
      format: (val: string) => formatCurrency(val),
      sortable: true,
    },
    {
      name: 'monthCreditCumulated',
      required: true,
      label: 'Credito cumulato',
      field: (row: MonthlyUserAnalysis) => getUser(row).cumulatedCredit,
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

const formatCurrency = (value: string) =>
  NumberUtil.formatCurrency(asDecimal(value));
</script>
