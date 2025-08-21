<template>
  <q-table
    title="Entries"
    ref="table"
    class="col-12"
    style="max-width: 1200px"
    :rows="entries.items"
    :columns="columns"
    row-key="entryId"
    :binary-state-sort="true"
    :rows-per-page-options="[5, 10, 20, 50, 75, 100]"
    v-model:pagination="pagination"
    :loading="loading"
    :grid="false"
  >
    <template v-slot:top>
      <div class="q-gutter-md justify-between full-width">
        <q-input clearable autofocus outlined v-model="search" label="Search">
          <template v-slot:after>
            <q-btn round color="primary" icon="add" @click="$emit('add')" />
          </template>
        </q-input>
      </div>
    </template>
    <template v-slot:item="props">
      <div
        class="q-py-xs q-px-xs col-xs-6 col-sm-4 col-md-3 col-lg-2 col-xl-2 grid-style-transition"
        :style="props.selected ? 'transform: scale(0.95);' : ''"
      >
        <BoardCard :board="props.row" />
      </div>
    </template>
  </q-table>
</template>
<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref } from 'vue';
import { Subscription, liveQuery } from 'dexie';
import localDb from 'src/persistence/local-db';
import Board from 'src/budget/models/board';
import { QTableColumn } from 'quasar';
import DateUtil from 'src/utils/date-util';
import BoardCard from 'src/budget/components/boards/BoardCard.vue';
import BoardEntry from 'src/budget/models/board-entry';

interface Props {
  boardId: string;
}

const props = withDefaults(defineProps<Props>(), {});

const loading = ref(false);
const search = ref('');
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const table = ref<any>(null);

const pagination = {
  sortBy: 'desc',
  descending: false,
  page: 0,
  rowsPerPage: 5,
};

defineEmits(['update', 'add', 'delete']);

const columns: QTableColumn[] = [
  {
    name: 'entryId',
    label: 'ID',
    field: 'entryId',
    sortable: false,
  },
  {
    name: 'accountId',
    label: 'Account',
    field: 'accountId',
    sortable: true,
  },
  {
    name: 'categoryId',
    label: 'Category',
    field: 'categoryId',
    sortable: true,
  },
  {
    name: 'description',
    label: 'Description',
    field: 'description',
    sortable: false,
  },
  {
    name: 'amount',
    label: 'Amount',
    field: 'amount',
    sortable: true,
  },
  {
    name: 'creationDate',
    label: 'Created',
    style: 'width: 100px',
    field: (row: Board) => row.creationDate,
    sortable: true,
    sortOrder: 'da',
    format: (val: Date) => DateUtil.timeDiff(val),
  },
  {
    name: 'lastUpdate',
    label: 'Updated',
    style: 'width: 100px',
    field: (row: Board) => row.lastUpdate,
    sortable: true,
    sortOrder: 'da',
    format: (val: Date) => DateUtil.timeDiff(val),
  },
];
const entries = reactive({ items: new Array<BoardEntry>() });
let subscription: Subscription | undefined;

onMounted(() => {
  subscription = liveQuery(() =>
    localDb.boardEntries.where('boardId').equals(props.boardId).toArray(),
  ).subscribe((elements) => (entries.items = elements));
});

onUnmounted(() => {
  subscription?.unsubscribe();
  subscription = undefined;
});
</script>
