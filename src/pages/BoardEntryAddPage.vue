<template>
  <q-page>
    <div class="q-pa-md row justify-center">
      <q-form
        @submit="onSubmit"
        class="col-12 q-gutter-y-md column"
        style="max-width: 500px"
      >
        <q-date class="self-center" v-model="date" />
        <q-select
          label="Autore"
          outlined
          :options="authorEntries"
          v-model="author"
          :rules="[
            (val) => (val && val.value.length !== 0) || 'Valore obbligatorio',
          ]"
        />
        <q-select
          label="Categoria"
          outlined
          :options="categories"
          v-model="category"
          use-input
          input-debounce="0"
          new-value-mode="add-unique"
          :rules="[
            (val) => (val && val.trim().length !== 0) || 'Valore obbligatorio',
          ]"
        />
        <q-input outlined v-model="description" label="Descrizione" />
        <q-input
          outlined
          v-model="amount"
          :step="0.01"
          label="Importo"
          type="number"
          :rules="[(val) => (val && val.length > 0) || 'Valore obbligatorio']"
        />
        <q-btn label="Submit" type="submit" color="primary" />
      </q-form>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import boardService from 'src/integrations/BoardService';
import BoardEntry from 'src/models/BoardEntry';
import BoardUser from 'src/models/BoardUser';
import { fromQDateFormat, toQDateFormat } from 'src/utils/DateUtil';
import userService from 'src/integrations/UserService';
import UserData from 'src/models/UserData';
import { asInt } from 'src/utils/JsonUtil';

class SelectValue {
  label = '';
  value = '';

  constructor(label: string, value: string) {
    this.label = label;
    this.value = value;
  }
}

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
});

// form data
const date = ref(toQDateFormat(new Date()));
const author = ref(new SelectValue('', ''));

const category = ref('');
const description = ref('');
const amount = ref(0);

const user = ref(new UserData());
const entries = ref(new Array<BoardEntry>());
const categories = ref(new Array<string>());
const users = ref(new Array<BoardUser>());
const authorEntries = computed(() =>
  users.value.map((u) => new SelectValue(u.username, `${u.userId}`))
);

const loadData = async (boardId: string): Promise<void> => {
  user.value = await userService.getUser();
  entries.value = await boardService.getBoardEntries(boardId);
  users.value = await boardService.getBoardUsers(boardId);
  categories.value = await boardService.getBoardCategories(boardId);
  categories.value.push('Casa', 'Bollette');
  author.value = new SelectValue(user.value.username, `${user.value.id}`);
};

loadData(props.boardId);

watch(
  () => props.boardId,
  (newBoardId) => {
    entries.value = new Array<BoardEntry>();
    users.value = new Array<BoardUser>();
    categories.value = new Array<string>();
    loadData(newBoardId);
  }
);

const onSubmit = async (): Promise<void> => {
  const entry = new BoardEntry();
  entry.boardId = props.boardId;
  entry.ownerId = asInt(author.value.value);
  entry.date = fromQDateFormat(date.value);
  entry.category = category.value.trim();
  const existingEqualsCategories = categories.value.filter(
    (c) => c.toLowerCase() === category.value.trim()
  );
  if (existingEqualsCategories.length !== 0) {
    entry.category = existingEqualsCategories[0];
  }
  entry.description = description.value;
  entry.amount = amount.value;
  await boardService.addBoardEntry(props.boardId, entry);
};
</script>
