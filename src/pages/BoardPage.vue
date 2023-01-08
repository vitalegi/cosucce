<template>
  <q-page>
    <div class="q-pa-md row">
      <div class="q-pa-xs col-12 row">
        <div class="text-h6">{{ board.name }}</div>
        <q-space />
        <q-btn round color="primary" icon="add" @click="addNewBoardEntry()" />
      </div>
      <div class="q-pa-xs col-12">
        <q-card>
          <q-card-section>
            <div class="text-subtitle2">Recap</div>
          </q-card-section>
          <q-separator />
          <q-card-section>
            Lorem ipsum dolor sit amet consectetur adipisicing elit.
          </q-card-section>
        </q-card>
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

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const router = useRouter();

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

const addNewBoardEntry = (): void => {
  router.push(`/board/${props.boardId}/add`);
};

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
