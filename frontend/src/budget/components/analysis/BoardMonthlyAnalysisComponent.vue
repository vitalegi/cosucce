<template>
  <q-card>
    <q-card-section>
      <div class="text-subtitle2">Analisi</div>
    </q-card-section>
    <q-separator />
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
  </q-card>
</template>

<script setup lang="ts">
import { PropType, computed } from 'vue';
import MonthlyAnalysis from 'src/budget/models/analysis/MonthlyAnalysis';
import { formatYearMonth } from 'src/utils/DateUtil';
import NumberUtil from 'src/utils/NumberUtil';

const props = defineProps({
  entries: {
    type: Array as PropType<Array<MonthlyAnalysis>>,
    required: true,
  },
});

const columns = computed(() => {
  const cols = [
    {
      name: 'date',
      required: true,
      label: 'Data',
      field: (row: MonthlyAnalysis) => row.year * 12 + row.month,
      format: (val: string, row: MonthlyAnalysis) =>
        formatYearMonth(row.year, row.month - 1),
      align: 'left',
      sortable: true,
    },
    {
      name: 'amount',
      required: true,
      label: 'Spesa',
      field: (row: MonthlyAnalysis) => row.amount,
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

const formatCurrency = (value: string) => NumberUtil.formatCurrency(value);
</script>
