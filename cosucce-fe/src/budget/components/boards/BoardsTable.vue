<template>
  <q-table
    title="Boards"
    ref="table"
    class="col-12"
    style="max-width: 1200px"
    :rows="rows"
    :columns="columns"
    row-key="boardId"
    :binary-state-sort="true"
    :rows-per-page-options="[3, 5, 10, 20, 50, 75, 100]"
    :loading="loading"
    :grid="true"
    @request="onRequest"
    v-model:pagination="pagination"
    :filter="searchParams"
  >
    <template v-slot:top>
      <div class="q-gutter-md justify-between full-width">
        <q-input clearable autofocus outlined v-model="searchParams.freeText" label="Search">
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
import BoardCard from './BoardCard.vue';

type SortBy = 'name' | 'creationDate' | 'lastUpdate';

type Pagination = {
  sortBy: SortBy;
  descending?: boolean;
  page: number;
  rowsPerPage: number;
  rowsNumber: number;
};

const elements = reactive({ items: new Array<Board>() });
const rows = ref<Board[]>(new Array<Board>());
let elementsSubscription: Subscription | undefined;

const loading = ref(false);

type TableFilter = {
  freeText: string;
};
const searchParams = ref<TableFilter>({ freeText: '' });

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const table = ref<any>(null);

const pagination = ref<Pagination>({
  sortBy: 'name',
  descending: false,
  page: 1,
  rowsPerPage: 5,
  rowsNumber: 0,
});

defineEmits(['update', 'add', 'delete']);

const columns: QTableColumn[] = [
  {
    name: 'boardId',
    label: 'ID',
    field: 'boardId',
    sortable: false,
  },
  {
    name: 'name',
    label: 'Name',
    field: 'name',
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

function sort(sortBy: SortBy, descending: boolean | undefined): (a: Board, b: Board) => number {
  const direction = descending ? -1 : 1;
  switch (sortBy) {
    case 'name':
      return (a: Board, b: Board) => direction * (a.name > b.name ? 1 : -1);
    case 'creationDate':
      return (a: Board, b: Board) =>
        direction * DateUtil.compareDates(a.creationDate, b.creationDate);
    case 'lastUpdate':
      return (a: Board, b: Board) => direction * DateUtil.compareDates(a.lastUpdate, b.lastUpdate);
    default:
      throw new Error(`Sort by is not handled`);
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function onRequest(props: any): void {
  loading.value = true;
  updateData(props.pagination as Pagination);
  loading.value = false;
}

function updateData(newPagination: Pagination): void {
  const filtered = elements.items.filter((b: Board) => filterElement(b));
  const rowsNumber = filtered.length;
  const sorted = filtered.sort(sort(newPagination.sortBy, newPagination.descending));
  const paginated = sorted.slice(
    (newPagination.page - 1) * newPagination.rowsPerPage,
    newPagination.rowsPerPage,
  );
  rows.value = paginated;
  pagination.value.rowsNumber = rowsNumber;
  pagination.value.descending = newPagination.descending ? newPagination.descending : false;
  pagination.value.page = newPagination.page;
  pagination.value.rowsPerPage = newPagination.rowsPerPage;
  pagination.value.sortBy = newPagination.sortBy;
}

function filterElement(b: Board): boolean {
  const freeText = searchParams.value.freeText.trim().toUpperCase();
  if (freeText !== '') {
    if (b.name.toUpperCase().indexOf(freeText) === -1) {
      return false;
    }
  }
  return true;
}

onMounted(() => {
  loading.value = true;
  elementsSubscription = liveQuery(() => localDb.boards.toArray()).subscribe((e) => {
    elements.items = e;
    updateData(pagination.value);
    loading.value = false;
  });
});

onUnmounted(() => {
  elementsSubscription?.unsubscribe();
  elementsSubscription = undefined;
});
</script>
