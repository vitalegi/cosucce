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
          @click="showDialogDelete = true"
        />
      </q-card-section>
      <q-separator />
    </q-card>
  </div>

  <q-dialog v-model="showDialogDelete" persistent>
    <q-card>
      <q-card-section>
        <div class="text-h6">Conferma eliminazione board</div>
      </q-card-section>
      <q-card-section>
        Quando elimini una board, l'azione è permanente. Please be certain.
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat label="Annulla" v-close-popup @click="resetDialogShare()" />
        <q-btn flat label="Conferma" v-close-popup @click="deleteBoard()" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import BoardInvite from 'src/budget/models/BoardInvite';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const showDialogDelete = ref(false);

const resetDialogShare = (): void => {
  showDialogDelete.value = false;
};

const deleteBoard = async (): Promise<void> => {
  boardService.deleteBoard(props.boardId);
  resetDialogShare();
};
</script>
