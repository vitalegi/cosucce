<template>
  <q-table
    :dense="true"
    :flat="true"
    :rows="entries"
    :columns="boardEntriesColumns"
    row-key="name"
    :binary-state-sort="true"
  >
    <template v-slot:body-cell-actions="props">
      <q-td :props="props">
        <div class="q-pt-xs q-gutter-sm">
          <q-btn round icon="edit" size="xs" @click="editEntry(props.row.id)" />
          <q-btn
            round
            icon="delete"
            size="xs"
            @click="deleteEntry(props.row.id)"
          />
        </div>
      </q-td>
    </template>
  </q-table>
</template>

<script setup lang="ts">
import { PropType } from 'vue';
import BoardEntry from 'src/budget/models/BoardEntry';
import NumberUtil from 'src/utils/NumberUtil';
import { formatFullDate, formatElapsedTime } from 'src/utils/DateUtil';
import BoardUser from 'src/budget/models/BoardUser';

const props = defineProps({
  entries: {
    type: Array as PropType<Array<BoardEntry>>,
    required: true,
  },
  users: {
    type: Array as PropType<Array<BoardUser>>,
    required: true,
  },
});

const formatCurrency = (value: string) => NumberUtil.formatCurrency(value);

const now = new Date();

const getUsername = (userId: number) => {
  const users = props.users.filter((u) => u.user.id === userId);
  if (users.length > 0) {
    return users[0].user.username;
  }
  return 'N/A';
};

const boardEntriesColumns = [
  {
    name: 'date',
    required: true,
    label: 'Data',
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    field: (row: any) => formatFullDate(row.date),
    align: 'left',
    sortable: true,
    sort: (a: string, b: string, rowA: BoardEntry, rowB: BoardEntry) =>
      rowA.date.getTime() - rowB.date.getTime(),
  },
  {
    name: 'ownerId',
    required: true,
    label: 'Proprietario',
    field: (row: any) => getUsername(row.ownerId),
    align: 'left',
    sortable: true,
  },
  {
    name: 'category',
    required: true,
    label: 'Categoria',
    field: 'category',
    align: 'left',
    sortable: true,
  },
  {
    name: 'description',
    required: false,
    label: 'Descrizione',
    field: 'description',
    align: 'left',
    sortable: true,
  },
  {
    name: 'amount',
    required: true,
    label: 'Importo',
    field: 'amount',
    format: (value: string) => formatCurrency(value),
    align: 'right',
    sortable: true,
  },
  {
    name: 'lastUpdate',
    required: true,
    label: 'Ultimo aggiornamento',
    field: 'lastUpdate',
    format: (value: string, row: BoardEntry) =>
      formatElapsedTime(row.lastUpdate, now),
    align: 'right',
    sortable: true,
  },
  {
    name: 'actions',
    required: true,
    label: '',
    field: 'id',
    align: 'right',
    sortable: false,
  },
];

const emit = defineEmits(['editEntry', 'deleteEntry']);

const editEntry = (boardEntryId: string): void => {
  emit('editEntry', boardEntryId);
};

const deleteEntry = (boardEntryId: string): void => {
  emit('deleteEntry', boardEntryId);
};
</script>
