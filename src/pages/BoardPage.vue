<template>
  <q-page>
    <q-table
      :dense="true"
      :title="board.name"
      :rows="boardEntries"
      :columns="boardEntriesColumns"
      row-key="name"
      :binary-state-sort="true"
    />
  </q-page>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import boardService from 'src/integrations/BoardService';
import Board from 'src/models/Board';
import BoardEntry from 'src/models/BoardEntry';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const board = ref(new Board());
const boardEntries = ref(new Array<BoardEntry>());

const loadData = async (boardId: string): Promise<void> => {
  board.value = await boardService.getBoard(boardId);
  boardEntries.value = await boardService.getBoardEntries(boardId);
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

const boardEntriesColumns = [
  {
    name: 'date',
    required: true,
    label: 'Data',
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
</script>
