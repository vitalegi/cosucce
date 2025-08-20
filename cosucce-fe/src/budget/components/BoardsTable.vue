<template>
  <q-table
    title="Boards"
    ref="table"
    class="col-12"
    style="max-width: 1200px"
    :rows="boards.items"
    :columns="columns"
    row-key="boardId"
    :binary-state-sort="true"
    :rows-per-page-options="[5, 10, 20, 50, 75, 100]"
    v-model:pagination="pagination"
    :loading="loading"
    :grid="true"
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
import Board from '../models/board';
import { QTableColumn } from 'quasar';
import DateUtil from 'src/utils/date-util';
import BoardCard from './BoardCard.vue';

const boards = reactive({ items: new Array<Board>() });
let boardsSubscription: Subscription | undefined;

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

onMounted(() => {
  boardsSubscription = liveQuery(() => localDb.boards.toArray()).subscribe(
    (elements) => (boards.items = elements),
  );
});

onUnmounted(() => {
  boardsSubscription?.unsubscribe();
  boardsSubscription = undefined;
});
</script>
