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
          :rules="[
            (val) => (val && validateAmount(val)) || 'Valore obbligatorio',
          ]"
        />
        <q-btn label="Submit" type="submit" color="primary" />
      </q-form>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import BoardEntry from 'src/budget/models/BoardEntry';
import BoardUser from 'src/budget/models/BoardUser';
import { fromQDateFormat, toQDateFormat } from 'src/utils/DateUtil';
import userService from 'src/integrations/UserService';
import UserData from 'src/models/UserData';
import SelectValue from 'src/models/SelectValue';
import { asInt } from 'src/utils/JsonUtil';
import NumberUtil from 'src/utils/NumberUtil';
import { useRouter } from 'vue-router';
import spinner from 'src/utils/Spinner';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
  boardEntryId: {
    type: String,
    required: false,
  },
});

const router = useRouter();

// form data
const date = ref(toQDateFormat(new Date()));
const author = ref(new SelectValue('', ''));
const category = ref('');
const description = ref('');
const amount = ref(0);

const user = ref(new UserData());
const categories = ref(new Array<string>());
const users = ref(new Array<BoardUser>());
const authorEntries = computed(() =>
  users.value.map((u) => new SelectValue(u.user.username, `${u.user.id}`))
);

const loadData = async (boardId: string): Promise<void> => {
  await spinner.sync(async () => {
    user.value = await userService.getUser();
    users.value = await boardService.getBoardUsers(boardId);
    categories.value = await boardService.getBoardCategories(boardId);
    author.value = new SelectValue(user.value.username, `${user.value.id}`);

    if (props.boardEntryId) {
      const entry = await boardService.getBoardEntry(
        props.boardId,
        props.boardEntryId
      );
      date.value = toQDateFormat(entry.date);
      const user = users.value.filter((u) => u.user.id === entry.ownerId)[0];
      author.value = new SelectValue(user.user.username, `${user.user.id}`);
      category.value = entry.category;
      description.value = entry.description;
      amount.value = entry.amount;
    }
  });
};

loadData(props.boardId);

watch(
  () => props.boardId,
  (newBoardId) => {
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
  await spinner.sync(async () => {
    if (props.boardEntryId) {
      entry.id = props.boardEntryId;
      await boardService.updateBoardEntry(props.boardId, entry);
    } else {
      await boardService.addBoardEntry(props.boardId, entry);
    }
  });
  router.push(`/board/${props.boardId}`);
};

const validateAmount = (value: string): boolean => {
  return NumberUtil.parseAsDecimal(value) !== 0;
};
</script>
