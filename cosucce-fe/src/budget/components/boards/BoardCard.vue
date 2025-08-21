<template>
  <q-card class="row justify-between" @click="openBoard()" style="height: 160px">
    <div class="row justify-start q-gutter-xs full-width">
      <div class="text-weight-medium single-line-nowrap-text">
        {{ board.name }}
      </div>
    </div>
  </q-card>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import Board from 'src/budget/models/board';

const router = useRouter();

interface Props {
  board: Board;
}

const props = withDefaults(defineProps<Props>(), {});

function openBoard() {
  void router.push({
    path: `budget/board/${props.board.boardId}`,
  });
}
</script>

<style lang="sass" scoped></style>
