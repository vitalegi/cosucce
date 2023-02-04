<template>
  <div class="col-12">
    <div class="q-pa-xs col-12 row">
      <div class="text-h2 section">Splits</div>
      <q-space />
      <q-btn round color="primary" icon="add" @click="addBoardSplit()" />
    </div>
    <div class="q-pa-xs col-12">
      <q-table
        :flat="true"
        :rows="boardSplits"
        :columns="columns"
        row-key="id"
        :binary-state-sort="true"
      >
        <template v-slot:body-cell-actions="props">
          <q-td :props="props">
            <div class="q-pa-xs q-gutter-sm">
              <q-btn round icon="edit" @click="editBoardSplit(props.row)" />
              <q-btn
                round
                icon="delete"
                @click="showDialogDeleteBoardEntry(props.row)"
              />
            </div>
          </q-td>
        </template>
      </q-table>
    </div>
  </div>

  <q-dialog v-model="dialogDeleteBoardSplit" persistent>
    <q-card>
      <q-card-section class="row items-center">
        <q-avatar icon="delete" color="primary" text-color="white" />
        <span class="q-ml-sm"> Vuoi eliminare questa configurazione? </span>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn
          flat
          label="Annulla"
          v-close-popup
          @click="resetDialogDeleteBoardSplit()"
        />
        <q-btn
          flat
          label="Procedi"
          color="primary"
          v-close-popup
          @click="deleteBoardSplit()"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import BoardUser from 'src/budget/models/BoardUser';
import BoardSplit from 'src/budget/models/BoardSplit';
import { ref } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import { formatYearMonth } from 'src/utils/DateUtil';
import NumberUtil from 'src/utils/NumberUtil';
import { useRouter } from 'vue-router';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const members = ref(new Array<BoardUser>());
boardService
  .getBoardUsers(props.boardId)
  .then((users) => (members.value = users));

const boardSplits = ref(new Array<BoardSplit>());

boardService
  .getBoardSplits(props.boardId)
  .then((splits) => (boardSplits.value = splits));

const getUsername = (userId: number): string => {
  const v = members.value.filter((u) => u.user.id === userId);
  if (v.length > 0) {
    return v[0].user.username;
  } else {
    return 'N/A';
  }
};

const formatDate = (year: number | null, month: number | null): string => {
  if (year === null || month === null) {
    return '-';
  }
  return formatYearMonth(year, month - 1);
};

const getPercentageAsText = (split: BoardSplit): string => {
  return NumberUtil.formatPercentage(split.value1);
};

const columns = [
  {
    name: 'fromYear',
    required: true,
    label: 'Da',
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    field: (row: BoardSplit) => formatDate(row.fromYear, row.fromMonth),
    align: 'left',
    sortable: true,
  },
  {
    name: 'toYear',
    required: true,
    label: 'A',
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    field: (row: BoardSplit) => formatDate(row.toYear, row.toMonth),
    align: 'left',
    sortable: true,
  },
  {
    name: 'Username',
    required: true,
    label: 'Username',
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    field: (row: BoardSplit) => getUsername(row.userId),
    align: 'left',
    sortable: true,
  },
  {
    name: '%',
    required: true,
    label: '%',
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    field: (row: BoardSplit) => getPercentageAsText(row),
    align: 'left',
    sortable: true,
  },
  {
    name: 'actions',
    required: true,
    label: '',
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    field: (row: BoardSplit) => row.id,
    align: 'right',
    sortable: false,
  },
];

const dialogDeleteBoardSplit = ref(false);
const deleteBoardSplitId = ref('');

const resetDialogDeleteBoardSplit = (): void => {
  deleteBoardSplitId.value = '';
  dialogDeleteBoardSplit.value = false;
};

const deleteBoardSplit = (): void => {
  boardService.deleteBoardSplit(props.boardId, deleteBoardSplitId.value);
};

const showDialogDeleteBoardEntry = (split: BoardSplit): void => {
  deleteBoardSplitId.value = split.id;
  dialogDeleteBoardSplit.value = true;
};

const router = useRouter();

const addBoardSplit = (): void => {
  router.push(`/board/${props.boardId}/split/add`);
};

const editBoardSplit = (split: BoardSplit): void => {
  router.push(`/board/${props.boardId}/split/${split.id}/edit`);
};
</script>
