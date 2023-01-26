<template>
  <div class="col-12">
    <div class="q-pa-xs col-12 row">
      <div class="text-h2 section">Members</div>
      <q-space />
      <q-btn
        round
        color="primary"
        icon="add"
        @click="showDialogAddUser = true"
      />
    </div>
    <div class="q-pa-xs col-12">
      <BoardMembersList :users="members" />
    </div>
  </div>

  <q-dialog v-model="showDialogAddUser" persistent>
    <q-card>
      <q-card-section class="row items-center">
        <q-input outlined v-model="addUserId" label="ID utente" type="number" />
      </q-card-section>

      <q-card-actions align="right">
        <q-btn
          flat
          label="Annulla"
          v-close-popup
          @click="resetDialogAddUser()"
        />
        <q-btn
          flat
          label="Aggiungi"
          color="primary"
          v-close-popup
          @click="addUser()"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import BoardUser from 'src/budget/models/BoardUser';
import { ref, watch } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import BoardMembersList from 'src/budget/components/BoardMembersList.vue';

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

const showDialogAddUser = ref(false);
const addUserId = ref(0);

const resetDialogAddUser = (): void => {
  addUserId.value = 0;
  showDialogAddUser.value = false;
};

const addUser = async (): Promise<void> => {
  await boardService.addBoardUser(props.boardId, addUserId.value, 'MEMBER');
  resetDialogAddUser();
};
</script>
