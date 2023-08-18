<template>
  <div class="col-12">
    <div class="q-pa-xs col-12 row">
      <div class="text-h2 section">Import</div>
    </div>
    <div class="q-pa-xs col-12 row justify-center">
      <q-form @submit="onSubmit" class="col-12 q-gutter-y-md column">
        <q-input
          v-model="text"
          filled
          type="textarea"
          :hint="`Autori accettati: ${usernames}`"
        >
          <template v-slot:prepend>
            <q-icon
              v-if="!showDone"
              color="primary"
              name="save"
              class="cursor-pointer"
              @click="onSubmit"
              :disable="!isValid"
            />
            <q-icon
              v-if="showDone"
              name="check"
              class="cursor-pointer"
              style="color: green"
            />
          </template>
        </q-input>
      </q-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import BoardUser from 'src/budget/models/BoardUser';
import { computed, ref } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import BoardEntry from 'src/budget/models/BoardEntry';
import { asDecimal, asInt } from 'src/utils/JsonUtil';
import spinner from 'src/utils/Spinner';
import { doesNotReject } from 'assert';

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

const text = ref('06/01/2023	50,15 € 	Bollette	Gas 2023-01	foo.bar@gmail.com');

const parseDate = (date: string): Date => {
  const el = date.split('/');
  return new Date(Date.UTC(asInt(el[2]), asInt(el[1]) - 1, asInt(el[0])));
};

const getUserId = (username: string): number => {
  const v = members.value.filter((u) => u.user.username === username);
  if (v.length > 0) {
    return v[0].user.id;
  } else {
    throw new Error(`Username ${username} isn't a member of this board`);
  }
};

const usernames = computed(() =>
  members.value.map((u) => u.user.username).join(', '),
);

const parseAmount = (value: string): number => {
  const num = value.replace('.', '').replace(',', '.').trim().split(' ')[0];
  return asDecimal(num);
};

const toBoardEntry = (row: string): BoardEntry => {
  const cols = row.split('\t');

  const entry = new BoardEntry();
  entry.date = parseDate(cols[0]);
  entry.amount = parseAmount(cols[1]);
  entry.category = cols[2];
  entry.description = cols[3];
  entry.ownerId = getUserId(cols[4]);
  return entry;
};

const toBoardEntries = (text: string): BoardEntry[] => {
  return text
    .split('\n')
    .filter((t) => t.trim() !== '')
    .map(toBoardEntry);
};

const isValid = computed(() => {
  try {
    text.value
      .split('\n')
      .filter((t) => t.trim() !== '')
      .map(toBoardEntry);
    return true;
  } catch (e) {
    return false;
  }
});

const showDone = ref(false);

const onSubmit = async () => {
  await spinner.sync(async () => {
    boardService.addBoardEntries(props.boardId, toBoardEntries(text.value));
    text.value = '';
  });
  showDone.value = true;
  setTimeout(() => (showDone.value = false), 1300);
};
</script>
