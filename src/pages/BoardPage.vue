<template>
  <q-page>
    <div class="q-pa-md row">
      <div class="q-pa-xs col-12 row">
        <div class="text-h6">{{ board.name }}</div>
        <q-space />
        <q-btn round color="primary" icon="add" @click="addNewBoardEntry()" />
      </div>
      <div class="q-pa-xs col-12">
        <BoardMonthlyUsersAnalysisComponent
          :users="members"
          :entries="monthlyUserAnalysis"
        >
        </BoardMonthlyUsersAnalysisComponent>
      </div>
      <div class="q-pa-xs col-12">
        <q-card>
          <q-card-section>
            <div class="text-subtitle2">Dati</div>
          </q-card-section>
          <q-separator />
          <q-table
            :dense="true"
            :flat="true"
            :rows="boardEntries"
            :columns="boardEntriesColumns"
            row-key="name"
            :binary-state-sort="true"
          />
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import boardService from 'src/integrations/BoardService';
import Board from 'src/models/Board';
import BoardEntry from 'src/models/BoardEntry';
import { useRouter } from 'vue-router';
import BoardMonthlyUsersAnalysisComponent from 'components/board/analysis/BoardMonthlyUsersAnalysisComponent.vue';
import UserData from 'src/models/UserData';
import MonthlyUserAnalysis from 'src/models/budget/analysis/MonthlyUserAnalysis';
import BoardUser from 'src/models/budget/BoardUser';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const router = useRouter();

const board = ref(new Board());
const boardEntries = ref(new Array<BoardEntry>());
const monthlyUserAnalysis = ref(new Array<MonthlyUserAnalysis>());
const members = ref(new Array<BoardUser>());

const reloadAnalysis = async (boardId: string): Promise<void> => {
  monthlyUserAnalysis.value = await boardService.getBoardAnalysisMonthUser(
    boardId
  );
};

const loadData = async (boardId: string): Promise<void> => {
  board.value = await boardService.getBoard(boardId);
  boardEntries.value = await boardService.getBoardEntries(boardId);
  members.value = await boardService.getBoardUsers(boardId);
  reloadAnalysis(boardId);
};

loadData(props.boardId);

watch(
  () => props.boardId,
  (newBoardId) => {
    board.value = new Board();
    boardEntries.value = new Array<BoardEntry>();
    loadData(newBoardId);
  }
);

const addNewBoardEntry = (): void => {
  router.push(`/board/${props.boardId}/add`);
};

const boardEntriesColumns = [
  {
    name: 'date',
    required: true,
    label: 'Data',
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    field: (row: any) => (row.date as Date).toLocaleDateString(),
    align: 'left',
    sortable: true,
    sort: (a: string, b: string, rowA: BoardEntry, rowB: BoardEntry) =>
      rowA.date.getTime() - rowB.date.getTime(),
  },
  {
    name: 'ownerId',
    required: true,
    label: 'Proprietario',
    field: 'ownerId',
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
    align: 'left',
    sortable: true,
  },
];

const users = new Array<UserData>();
users.push({ id: 0, username: 'Giorgio' });
users.push({ id: 1, username: 'Federica' });
</script>
