<template>
  <q-form class="col-12 q-gutter-y-md" style="max-width: 600px" greedy @submit="submit()">
    <q-input outlined v-model="editor.name" label="Name" />
    <q-btn class="full-width" size="xl" type="submit" color="primary">{{ submitLabel }}</q-btn>
  </q-form>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useBoardStore } from 'src/budget/stores/board-store';
import UuidUtil from 'src/utils/uuid-util';

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
const boardStore = useBoardStore();

const addMode = computed(() => props.id === undefined);

const submitLabel = computed(() => {
  if (addMode.value) {
    return 'Add';
  }
  return 'Update';
});

async function submit(): Promise<void> {
  if (addMode.value) {
    await boardStore.addBoard(UuidUtil.uuid(), editor.value.name.trim());
    emit('save', { name: editor.value.name.trim() });
  } else {
    emit('save', { id: props.id, name: editor.value.name.trim() });
  }
}

onMounted(() => {
  if (props.name) {
    editor.value.name = props.name;
  }
});
</script>
