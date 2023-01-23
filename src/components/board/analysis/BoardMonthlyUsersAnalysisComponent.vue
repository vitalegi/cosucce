<template>
  <q-card>
    <q-card-section>
      <div class="text-subtitle2">Analisi</div>
    </q-card-section>
    <q-separator />
    <q-card-section v-for="user in users" :key="user.id">
      <BoardMonthlyUserAnalysisComponent :user="user.user" :entries="entries">
      </BoardMonthlyUserAnalysisComponent>
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import MonthlyUserAnalysis from 'src/models/budget/analysis/MonthlyUserAnalysis';
import BoardMonthlyUserAnalysisComponent from 'components/board/analysis/BoardMonthlyUserAnalysisComponent.vue';
import { PropType } from 'vue';
import BoardUser from 'src/models/budget/BoardUser';

defineProps({
  users: {
    type: Array as PropType<Array<BoardUser>>,
    required: true,
  },
  entries: {
    type: Array as PropType<Array<MonthlyUserAnalysis>>,
    required: true,
  },
});
</script>
