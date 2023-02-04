<template>
  <q-page>
    <div class="q-pa-md row justify-center">
      <q-form
        @submit="onSubmit"
        class="col-12 q-gutter-y-md column"
        style="max-width: 500px"
      >
        <q-select
          label="Utente"
          outlined
          :options="members"
          v-model="user"
          :rules="[
            (val) => (val && val.value.length !== 0) || 'Valore obbligatorio',
          ]"
        />
        <q-input outlined v-model="fromYear" label="Da anno" hint="2022" />
        <q-select
          label="Da mese"
          outlined
          clearable
          :options="months"
          v-model="fromMonth"
        />
        <q-input outlined v-model="toYear" label="A anno" hint="2022" />
        <q-select
          label="A mese"
          outlined
          clearable
          :options="months"
          v-model="toMonth"
        />
        <q-input
          outlined
          v-model="percentage"
          :step="0.01"
          label="Percentuale"
          type="number"
          :rules="[
            (val) =>
              (val && val >= 0 && val <= 100) || 'Valore obbligatorio [0, 100]',
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
import SelectValue from 'src/models/SelectValue';
import { asInt } from 'src/utils/JsonUtil';
import { useRouter } from 'vue-router';
import BoardSplit from 'src/budget/models/BoardSplit';

const props = defineProps({
  boardId: {
    type: String,
    required: true,
  },
  boardSplitId: {
    type: String,
    required: false,
  },
});

const monthToSelect = (month: number): SelectValue => {
  const months = [
    'Gennaio',
    'Febbraio',
    'Marzo',
    'Aprile',
    'Maggio',
    'Giugno',
    'Luglio',
    'Agosto',
    'Settembre',
    'Ottobre',
    'Novembre',
    'Dicembre',
  ];
  return new SelectValue(months[month], `${month + 1}`);
};

const months = computed(() => {
  const months = new Array<SelectValue>();
  for (let i = 0; i < 12; i++) {
    months.push(monthToSelect(i));
  }
  return months;
});

const router = useRouter();

const members = ref(new Array<SelectValue>());

// form data
const user = ref(new SelectValue('', ''));
const fromYear = ref();
const toYear = ref();
const fromMonth = ref();
const toMonth = ref();
const percentage = ref(0);

const loadData = async (boardId: string): Promise<void> => {
  const users = await boardService.getBoardUsers(boardId);
  members.value = users.map(
    (u) => new SelectValue(u.user.username, `${u.user.id}`)
  );

  if (props.boardSplitId) {
    const entries = await boardService.getBoardSplits(props.boardId);
    const entry = entries.filter((e) => props.boardSplitId === e.id)[0];

    const entryUser = users.filter((u) => u.user.id === entry.userId)[0];
    user.value = new SelectValue(
      entryUser.user.username,
      `${entryUser.user.id}`
    );
    fromYear.value = entry.fromYear;
    toYear.value = entry.toYear;
    fromMonth.value = entry.fromMonth
      ? monthToSelect(entry.fromMonth - 1)
      : null;
    toMonth.value = entry.toMonth ? monthToSelect(entry.toMonth - 1) : null;
    percentage.value = entry.value1 * 100;
  }
};

loadData(props.boardId);

watch(
  () => props.boardId,
  (newBoardId) => {
    members.value = new Array<SelectValue>();
    loadData(newBoardId);
  }
);

const xnor = (v1: string, v2: string): boolean => {
  return (v1 !== '' && v2 !== '') || (v1 === '' && v2 === '');
};

const validFromMonth = (val: string): boolean | string => {
  if (val) {
    return true;
  }
  return xnor(val, fromYear.value);
};

const validFromYear = (val: string): boolean | string => {
  if (val) {
    return true;
  }
  return xnor(val, fromMonth.value);
};

const onSubmit = async (): Promise<void> => {
  const entry = new BoardSplit();
  entry.boardId = props.boardId;
  entry.userId = asInt(user.value.value);
  entry.fromYear = fromYear.value;
  entry.fromMonth = fromMonth.value ? fromMonth.value.value : null;
  entry.toYear = toYear.value;
  entry.toMonth = toMonth.value ? toMonth.value.value : null;
  entry.value1 = percentage.value / 100;

  if (props.boardSplitId) {
    entry.id = props.boardSplitId;
    await boardService.updateBoardSplit(entry);
  } else {
    await boardService.addBoardSplit(entry);
  }
  router.push(`/board/${props.boardId}/settings`);
};
</script>
