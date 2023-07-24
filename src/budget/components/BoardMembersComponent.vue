<template>
  <div class="col-12">
    <div class="q-pa-xs col-12 row">
      <div class="text-h2 section">Members</div>
      <q-space />
      <q-btn round color="primary" icon="share" @click="getInvite()" />
    </div>
    <div class="q-pa-xs col-12">
      <BoardMembersList :users="members" />
    </div>
  </div>

  <q-dialog v-model="showDialogShare" persistent>
    <q-card>
      <q-card-section>
        <div class="text-h6">Condividi board</div>
      </q-card-section>
      <q-card-section class="row items-center">
        <div class="col-12">
          Condividi il codice seguente per permettere ad altre persone di unirsi
          a questa board:
        </div>
        <q-input
          class="col-12"
          outlined
          v-model="token"
          label="Token"
          :readonly="true"
        >
          <template v-slot:append>
            <q-icon
              v-if="!done"
              name="content_copy"
              @click.stop.prevent="copy()"
              class="cursor-pointer"
            />
            <q-icon
              v-if="done"
              name="check"
              class="cursor-pointer"
              style="color: green"
            />
          </template>
        </q-input>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn flat label="Chiudi" v-close-popup @click="resetDialogShare()" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import BoardUser from 'src/budget/models/BoardUser';
import { computed, ref, watch } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import BoardMembersList from 'src/budget/components/BoardMembersList.vue';
import BoardInvite from 'src/budget/models/BoardInvite';
import spinner from 'src/utils/Spinner';

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

const showDialogShare = ref(false);
const invite = ref(new BoardInvite());

const resetDialogShare = (): void => {
  invite.value = new BoardInvite();
  showDialogShare.value = false;
};

const getInvite = async (): Promise<void> => {
  await spinner.sync(async () => {
    invite.value = await boardService.addBoardInvite(props.boardId);
    showDialogShare.value = true;
  });
};

const token = computed(() => invite.value.encodeToken());

const done = ref(false);
const copy = async (): Promise<void> => {
  await navigator.clipboard.writeText(token.value);
  done.value = true;
  setTimeout(() => {
    done.value = false;
  }, 1300);
};
</script>
