<template>
  <q-form class="col-12 q-gutter-y-md" style="max-width: 600px" greedy @submit="submit()">
    <q-input outlined v-model="editor.name" label="Name" />
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
  name: string;
}

const props = withDefaults(defineProps<Props>(), { id: undefined });

const editor = ref<{
  name: string;
}>({
  name: '',
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
    await budgetStore.addBoard({ boardId: id, name: editor.value.name.trim() });
    emit('save', {
      id: id,
    });
  } else {
    if (props.id === undefined) {
      throw Error('id is undefined, code should be unreachable');
    }
    await budgetStore.updateBoard({ boardId: props.id, name: editor.value.name.trim() });
    emit('save', {
      id: props.id,
    });
  }
}

onMounted(() => {
  if (props.name) {
    editor.value.name = props.name;
  }
});
</script>
