<template>
  <q-page class="row items-center justify-evenly">
    <BoardEditor :id="id" name="bbb"></BoardEditor>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import BoardEditor from '../components/BoardEditor.vue';
import budgetSyncService from '../services/budget-sync';
import UuidUtil from 'src/utils/uuid-util';

const id = ref(UuidUtil.uuid());

void budgetSyncService.synchronize();
</script>
