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
import Board from 'src/budget/models/board';
import routing from 'src/router/routing';
import { useRouter } from 'vue-router';

interface Props {
  board: Board;
}

const router = useRouter();
const props = withDefaults(defineProps<Props>(), {});

function openBoard() {
  void routing.budget().viewBoard(router, props.board.boardId);
}
</script>

<style lang="sass" scoped></style>
