<template>
  <q-table
    title="Accounts"
    ref="table"
    class="col-12"
    style="max-width: 1200px"
    :rows="entries.items"
    :columns="columns"
    row-key="accountId"
    :binary-state-sort="true"
    :rows-per-page-options="[5, 10, 20, 50, 75, 100]"
    :pagination="pagination"
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
  </q-table>
</template>
<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref } from 'vue';
import { Subscription, liveQuery } from 'dexie';
import localDb from 'src/persistence/local-db';
import { QTableColumn } from 'quasar';
import DateUtil from 'src/utils/date-util';
import BoardAccount from 'src/budget/models/board-account';

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
    name: 'accountId',
    label: 'ID',
    field: 'accountId',
    sortable: false,
  },
  {
    name: 'label',
    label: 'Label',
    field: 'label',
    sortable: true,
  },
  {
    name: 'icon',
    label: 'Icon',
    field: 'icon',
    sortable: false,
  },
  {
    name: 'enabled',
    label: 'Enabled',
    field: 'enabled',
    sortable: true,
  },
  {
    name: 'creationDate',
    label: 'Created',
    style: 'width: 100px',
    field: (row: BoardAccount) => row.creationDate,
    sortable: true,
    sortOrder: 'da',
    format: (val: Date) => DateUtil.timeDiff(val),
  },
  {
    name: 'lastUpdate',
    label: 'Updated',
    style: 'width: 100px',
    field: (row: BoardAccount) => row.lastUpdate,
    sortable: true,
    sortOrder: 'da',
    format: (val: Date) => DateUtil.timeDiff(val),
  },
];
const entries = reactive({ items: new Array<BoardAccount>() });
let subscription: Subscription | undefined;

onMounted(() => {
  subscription = liveQuery(() =>
    localDb.boardAccounts.where('boardId').equals(props.boardId).toArray(),
  ).subscribe((elements) => (entries.items = elements));
});

onUnmounted(() => {
  subscription?.unsubscribe();
  subscription = undefined;
});
</script>
