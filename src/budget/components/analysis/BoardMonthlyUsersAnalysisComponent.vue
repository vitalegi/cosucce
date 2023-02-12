<template>
  <q-card>
    <q-card-section>
      <div class="text-subtitle2">Analisi per utente</div>
    </q-card-section>
    <q-separator />
    <q-tabs
      v-model="tab"
      dense
      class="text-grey"
      active-color="primary"
      indicator-color="primary"
      align="justify"
      narrow-indicator
    >
      <q-tab
        v-for="(user, index) in users"
        :key="user.user.id"
        :name="index"
        :label="user.user.username"
      />
    </q-tabs>
    <q-separator />
    <q-tab-panels v-model="tab" animated>
      <q-tab-panel
        v-for="(user, index) in users"
        :key="user.user.id"
        :name="index"
      >
        <BoardMonthlyUserAnalysisComponent :user="user.user" :entries="entries">
        </BoardMonthlyUserAnalysisComponent>
      </q-tab-panel>
    </q-tab-panels>
  </q-card>
</template>

<script setup lang="ts">
import MonthlyUserAnalysis from 'src/budget/models/analysis/MonthlyUserAnalysis';
import BoardMonthlyUserAnalysisComponent from 'src/budget/components/analysis/BoardMonthlyUserAnalysisComponent.vue';
import { PropType, ref } from 'vue';
import BoardUser from 'src/budget/models/BoardUser';

const props = defineProps({
  users: {
    type: Array as PropType<Array<BoardUser>>,
    required: true,
  },
  entries: {
    type: Array as PropType<Array<MonthlyUserAnalysis>>,
    required: true,
  },
});

const tab = ref(0);
</script>
