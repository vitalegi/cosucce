<template>
  <q-form class="col-12 q-gutter-y-md" style="max-width: 600px" greedy>
    <q-input
      outlined
      v-model="editor.name"
      label="Name"
      @update:model-value="update()"
      debounce="400"
    />
    <q-btn v-if="addMode" @click="save()" class="full-width" size="xl" color="primary">Add</q-btn>
  </q-form>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useBoardStore } from '../stores/board-store';

const boardStore = useBoardStore();

interface Props {
  id: string;
  name: string;
}

const props = defineProps<Props>();

const editor = ref<{
  id: string;
  name: string;
}>({
  id: '',
  name: '',
});

const addMode = computed(() => props.id === '');
const updateMode = computed(() => props.id !== '');

function update(): void {
  if (updateMode.value) {
    save();
  }
}

function save(): void {
  if (addMode.value) {
    void boardStore.updateBoard(editor.value.id, editor.value.name.trim());
  } else {
    void boardStore.addBoard(editor.value.name.trim());
  }
}

onMounted(() => {
  if (props.id) {
    editor.value.id = props.id;
  }
  if (props.name) {
    editor.value.name = props.name;
  }
});
</script>
