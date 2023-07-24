<template>
  <div class="col-12">
    <div class="q-pa-xs col-12 row">
      <div class="text-h2 section">Rinomina board</div>
    </div>
    <div class="col-12 q-gutter-y-md column">
      <q-input outlined v-model="name" label="Nuovo nome">
        <template v-slot:prepend>
          <q-icon
            v-if="!done"
            color="primary"
            name="save"
            class="cursor-pointer"
            @click="updateName"
          />
          <q-icon
            v-if="done"
            name="check"
            class="cursor-pointer"
            style="color: green"
          />
        </template>
      </q-input>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import boardService from 'src/budget/integrations/BoardService';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const name = ref('');
boardService.getBoard(props.boardId).then((b) => {
  name.value = b.name;
});

const done = ref(false);

const updateName = async (): Promise<void> => {
  done.value = false;
  const board = await boardService.updateBoardName(props.boardId, name.value);
  name.value = board.name;
  done.value = true;
  setTimeout(() => {
    done.value = false;
  }, 1300);
};
</script>
