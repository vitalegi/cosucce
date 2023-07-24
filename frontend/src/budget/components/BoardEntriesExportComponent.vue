<template>
  <div class="col-12">
    <div class="q-pa-xs col-12 row">
      <div class="text-h2 section">Export</div>
    </div>
    <q-input v-model="text" readonly filled type="textarea">
      <template v-slot:prepend>
        <q-icon
          v-if="!done"
          color="primary"
          name="content_copy"
          class="cursor-pointer"
          @click="exportText(text)"
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
</template>

<script setup lang="ts">
import BoardUser from 'src/budget/models/BoardUser';
import { ref } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import { toQDateFormat } from 'src/utils/DateUtil';
import spinner from 'src/utils/Spinner';
import BoardEntry from 'src/budget/models/BoardEntry';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

const text = ref('');

const mapBoardEntry = (entry: BoardEntry, members: BoardUser[]): string => {
  const username = members.filter((m) => m.user.id === entry.ownerId)[0].user
    .username;
  const date = toQDateFormat(entry.date);
  const category = entry.category;
  const description = entry.description;
  const amount = entry.amount;
  const id = entry.id;
  return `${date};${username};${category};${description};${amount};${id}`;
};

const formatBoardEntries = async (): Promise<string> => {
  return await spinner.sync(async () => {
    const entries = await boardService.getBoardEntries(props.boardId);
    const members = await boardService.getBoardUsers(props.boardId);
    return entries.map((e) => mapBoardEntry(e, members)).join('\n');
  });
};

const exportText = async (text: string): Promise<void> => {
  await navigator.clipboard.writeText(text);
  done.value = true;
  setTimeout(() => {
    done.value = false;
  }, 1300);
};

formatBoardEntries().then((value) => (text.value = value));

const done = ref(false);
</script>
