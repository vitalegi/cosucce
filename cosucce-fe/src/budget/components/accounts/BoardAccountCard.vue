<template>
  <q-card class="row justify-between" @click="edit()" style="height: 120px">
    <div class="row justify-start q-gutter-xs full-width">
      <div class="text-weight-medium single-line-nowrap-text">
        {{ element.label }}
      </div>
      <IconDisplay :icon="element.icon" :enabled="element.enabled" />
    </div>
  </q-card>
</template>

<script setup lang="ts">
import BoardAccount from 'src/budget/models/board-account';
import routing from 'src/router/routing';
import { useRouter } from 'vue-router';
import IconDisplay from '../commons/IconDisplay.vue';

interface Props {
  element: BoardAccount;
}

const router = useRouter();
const props = withDefaults(defineProps<Props>(), {});

function edit() {
  void routing.budget().settingsEditAccount(router, props.element.boardId, props.element.accountId);
}
</script>

<style lang="sass" scoped></style>
