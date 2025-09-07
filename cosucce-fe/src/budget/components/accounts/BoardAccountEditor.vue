<template>
  <q-form class="col-12 q-gutter-y-md" style="max-width: 600px" greedy @submit="submit()">
    <q-input outlined v-model="editor.label" label="Label" />
    <IconSelector v-model="editor.icon" />
    <q-checkbox outlined v-model="editor.enabled" label="Enabled" />

    <q-btn class="full-width" size="xl" type="submit" color="primary">{{ submitLabel }}</q-btn>
  </q-form>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import UuidUtil from 'src/utils/uuid-util';
import { useBudgetStore } from 'src/budget/stores/budget-store';
import IconSelector from 'src/budget/components/commons/IconSelector.vue';

const emit = defineEmits(['save']);

interface Props {
  id?: string;
  boardId: string;
  label: string;
  icon: string;
  enabled: boolean;
}

const props = withDefaults(defineProps<Props>(), { id: undefined });

const editor = ref<{
  label: string;
  icon: string;
  enabled: boolean;
}>({
  label: '',
  icon: '',
  enabled: true,
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
    await budgetStore.addBoardAccount({
      accountId: id,
      boardId: props.boardId,
      label: editor.value.label.trim(),
      icon: editor.value.icon,
      enabled: editor.value.enabled,
    });
    emit('save', {
      id: id,
    });
  } else {
    if (props.id === undefined) {
      throw Error('id is undefined, code should be unreachable');
    }
    await budgetStore.updateBoardAccount({
      accountId: props.id,
      boardId: props.boardId,
      label: editor.value.label.trim(),
      icon: editor.value.icon,
      enabled: editor.value.enabled,
    });
    emit('save', {
      id: props.id,
    });
  }
}

onMounted(() => {
  editor.value.label = '';
  editor.value.icon = '';
  editor.value.enabled = true;

  if (props.label) {
    editor.value.label = props.label;
  }
  if (props.icon) {
    editor.value.icon = props.icon;
  }
  if (props.enabled) {
    editor.value.enabled = props.enabled;
  }
});
</script>
