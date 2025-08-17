<template>
  <q-form class="col-12 q-gutter-y-md" style="max-width: 600px" greedy @submit="submit()">
    <q-input outlined v-model="editor.id" label="Id" />
    <q-input outlined v-model="editor.name" label="Name" />
    <q-btn class="full-width" size="xl" type="submit" color="primary">Save</q-btn>
    <div>Available boards: {{ boards.items.length }}</div>
    <ul>
      <li v-for="(board, index) in boards.items" :key="index">
        {{ board.boardId }} / {{ board.name }} / {{ board.creationDate.toISOString() }}
      </li>
    </ul>
  </q-form>
</template>
<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue';
import { useBoardStore } from '../stores/board-store';
import { liveQuery, Subscription } from 'dexie';
import localDb from 'src/persistence/local-db';
import Board from '../models/board';
import UuidUtil from 'src/utils/uuid-util';

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

const addMode = computed(() => editor.value.id === '');

function submit(): void {
  if (addMode.value) {
    void boardStore.addBoard(UuidUtil.uuid(), editor.value.name.trim());
  } else {
    void boardStore.updateBoard(editor.value.id, editor.value.name.trim());
  }
}

const boards = reactive({ items: new Array<Board>() });
let boardsSubscription: Subscription | undefined;
onMounted(() => {
  if (props.id) {
    editor.value.id = props.id;
  }
  if (props.name) {
    editor.value.name = props.name;
  }
  boardsSubscription = liveQuery(() => localDb.boards.orderBy('creationDate').toArray()).subscribe(
    (elements) => (boards.items = elements),
  );
});

onUnmounted(() => {
  boardsSubscription?.unsubscribe();
});
</script>
