<template>
  <div class="col-12">
    <div class="q-pa-xs col-12 row">
      <div class="text-h2 section">Danger zone</div>
    </div>
    <q-card>
      <q-card-section class="row">
        <div>
          <div class="text-h6">Elimina board</div>
          <div>
            Quando elimini una board, l'azione è permanente. Please be certain.
          </div>
        </div>
        <q-space />
        <q-btn
          outline
          color="negative"
          label="Elimina"
          @click="showDialogDeleteBoard = true"
        />
      </q-card-section>
      <q-separator />
      <q-card-section class="row">
        <div>
          <div class="text-h6">Elimina tutte le spese</div>
          <div>
            Quando elimini le entry, l'azione è permanente. Please be certain.
          </div>
        </div>
        <q-space />
        <q-btn
          outline
          color="negative"
          label="Elimina"
          @click="showDialogDeleteEntries = true"
        />
      </q-card-section>
    </q-card>
  </div>

  <q-dialog v-model="showDialogDeleteBoard" persistent>
    <q-card>
      <q-card-section>
        <div class="text-h6">Conferma eliminazione board</div>
      </q-card-section>
      <q-card-section>
        Quando elimini una board, l'azione è permanente. Please be certain.
      </q-card-section>

      <q-card-actions align="right">
        <q-btn
          flat
          label="Annulla"
          v-close-popup
          @click="resetDialogDeleteBoard()"
        />
        <q-btn flat label="Conferma" v-close-popup @click="deleteBoard()" />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <q-dialog v-model="showDialogDeleteEntries" persistent>
    <q-card>
      <q-card-section>
        <div class="text-h6">Conferma eliminazione di tutte le spese</div>
      </q-card-section>
      <q-card-section>
        Quando elimini le spese, l'azione è permanente. Please be certain.
      </q-card-section>

      <q-card-actions align="right">
        <q-btn
          flat
          label="Annulla"
          v-close-popup
          @click="resetDialogDeleteEntries()"
        />
        <q-btn
          flat
          label="Conferma"
          v-close-popup
          @click="deleteBoardEntries()"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import { useBoardsStore } from 'src/budget/stores/boards-store';
import spinner from 'src/utils/Spinner';

const boardsStore = useBoardsStore();

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const showDialogDeleteBoard = ref(false);

const resetDialogDeleteBoard = (): void => {
  showDialogDeleteBoard.value = false;
};

const deleteBoard = async (): Promise<void> => {
  await spinner.sync(async () => {
    await boardService.deleteBoard(props.boardId);
    await boardsStore.update();
    resetDialogDeleteBoard();
  });
};

const showDialogDeleteEntries = ref(false);

const resetDialogDeleteEntries = (): void => {
  showDialogDeleteEntries.value = false;
};

const deleteBoardEntries = async (): Promise<void> => {
  await spinner.sync(async () => {
    const entries = await boardService.getBoardEntries(props.boardId);
    for (let i = 0; i < entries.length; i++) {
      await boardService.deleteBoardEntry(props.boardId, entries[i].id);
    }
    resetDialogDeleteEntries();
  });
};
</script>
