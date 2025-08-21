<template>
  <q-form class="col-12 q-gutter-y-md" style="max-width: 600px" greedy @submit="submit()">
    <q-input outlined v-model="editor.date" label="Date" />
    <q-input outlined v-model="editor.accountId" label="Account" />
    <q-input outlined v-model="editor.categoryId" label="Category" />
    <q-input outlined v-model="editor.description" label="Description" />
    <q-input outlined v-model="editor.amount" label="Amount" />

    <q-btn class="full-width" size="xl" type="submit" color="primary">{{ submitLabel }}</q-btn>
  </q-form>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import UuidUtil from 'src/utils/uuid-util';
import { useBudgetStore } from 'src/budget/stores/budget-store';

const emit = defineEmits(['save']);

interface Props {
  id?: string;
  boardId: string;
  date: string;
  accountId: string;
  categoryId: string;
  description: string;
  amount: string;
}

const props = withDefaults(defineProps<Props>(), { id: undefined });

const editor = ref<{
  date: string;
  accountId: string;
  categoryId: string;
  description: string;
  amount: string;
}>({
  date: '',
  accountId: '',
  categoryId: '',
  description: '',
  amount: '0',
});
const budgetStore = useBudgetStore();

const addMode = computed(() => props.id === undefined);

const submitLabel = computed(() => {
  if (addMode.value) {
    return 'Add';
  }
  return 'Update';
});

async function submit(): Promise<void> {
  if (addMode.value) {
    const id = UuidUtil.uuid();
    await budgetStore.addBoardEntry({
      entryId: id,
      boardId: props.boardId,
      date: editor.value.date,
      accountId: editor.value.accountId,
      categoryId: editor.value.categoryId,
      description: editor.value.description.trim(),
      amount: editor.value.amount,
    });
    emit('save', {
      id: id,
    });
  } else {
    if (props.id === undefined) {
      throw Error('id is undefined, code should be unreachable');
    }
    await budgetStore.updateBoardEntry({
      entryId: props.id,
      boardId: props.boardId,
      date: editor.value.date,
      accountId: editor.value.accountId,
      categoryId: editor.value.categoryId,
      description: editor.value.description.trim(),
      amount: editor.value.amount,
    });
    emit('save', {
      id: props.id,
    });
  }
}

onMounted(() => {
  editor.value.date = '';
  editor.value.accountId = '';
  editor.value.categoryId = '';
  editor.value.description = '';
  editor.value.amount = '0';

  if (props.accountId) {
    editor.value.accountId = props.accountId;
  }
  if (props.date) {
    editor.value.date = props.date;
  }
  if (props.categoryId) {
    editor.value.categoryId = props.categoryId;
  }
  if (props.accountId) {
    editor.value.description = props.description;
  }
  if (props.accountId) {
    editor.value.amount = props.amount;
  }
});
</script>
