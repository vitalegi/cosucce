<template>
  <q-page>
    <div class="q-pa-md row">
      <div class="q-pa-xs col-12 row">
        <div class="text-h6">{{ board.name }}</div>
        <q-space />
        <div class="q-pa-xs q-gutter-sm">
          <q-btn round color="primary" icon="add" @click="addNewBoardEntry()" />
          <q-btn round icon="settings" @click="openBoardSettings()" />
        </div>
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
          <BoardEntriesComponent
            :entries="boardEntries"
            :users="members"
            @editEntry="editBoardEntry"
            @deleteEntry="showDialogDeleteBoardEntry"
          />
        </q-card>
      </div>
    </div>
    <q-dialog v-model="dialogDeleteBoardEntry" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="delete" color="primary" text-color="white" />
          <span class="q-ml-sm"> Vuoi eliminare questa spesa? </span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn
            flat
            label="Annulla"
            v-close-popup
            @click="resetDialogDeleteBoardEntry()"
          />
          <q-btn
            flat
            label="Procedi"
            color="primary"
            v-close-popup
            @click="deleteBoardEntry()"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import Board from 'src/budget/models/Board';
import BoardEntry from 'src/budget/models/BoardEntry';
import { useRouter } from 'vue-router';
import BoardMonthlyUsersAnalysisComponent from 'src/budget/components/analysis/BoardMonthlyUsersAnalysisComponent.vue';
import BoardEntriesComponent from 'src/budget/components/BoardEntriesComponent.vue';
import MonthlyUserAnalysis from 'src/budget/models/analysis/MonthlyUserAnalysis';
import BoardUser from 'src/budget/models/BoardUser';

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
  members.value = await boardService.getBoardUsers(boardId);
  boardEntries.value = await boardService.getBoardEntries(boardId);
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

const editBoardEntry = (boardEntryId: string): void => {
  router.push(`/board/${props.boardId}/edit/${boardEntryId}`);
};

const openBoardSettings = (): void => {
  router.push(`/board/${props.boardId}/settings`);
};

// delete board entry

const dialogDeleteBoardEntry = ref(false);
const deleteBoardEntryId = ref('');

const showDialogDeleteBoardEntry = (boardEntryId: string): void => {
  deleteBoardEntryId.value = boardEntryId;
  dialogDeleteBoardEntry.value = true;
};

const resetDialogDeleteBoardEntry = (): void => {
  deleteBoardEntryId.value = '';
  dialogDeleteBoardEntry.value = false;
};

const deleteBoardEntry = async (): Promise<void> => {
  const boardEntryId = deleteBoardEntryId.value;
  await boardService.deleteBoardEntry(props.boardId, boardEntryId);

  // cleanup data
  boardEntries.value = boardEntries.value.filter((e) => e.id !== boardEntryId);
  await reloadAnalysis(props.boardId);
  resetDialogDeleteBoardEntry();
};
</script>
