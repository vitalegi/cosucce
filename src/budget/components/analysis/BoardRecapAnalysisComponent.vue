<template>
  <q-card>
    <q-card-section>
      <div class="text-subtitle2">Riepilogo</div>
    </q-card-section>
    <q-separator />

    <q-card-section v-if="loaded">
      <div v-if="isCredit">
        Gli altri membri ti devono un totale di
        {{ formatCurrency(abs(credit)) }}.
      </div>
      <div v-if="isDebit">
        Devi agli altri membri {{ formatCurrency(abs(credit)) }}.
      </div>
      <div v-if="isEven">Sei in pari.</div>
    </q-card-section>

    <q-card-section v-else>
      <q-skeleton type="rect" />
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
import { computed, PropType } from 'vue';
import NumberUtil from 'src/utils/NumberUtil';
import MonthlyUserAnalysis from 'src/budget/models/analysis/MonthlyUserAnalysis';
import UserData from 'src/models/UserData';
import UserAmount from 'src/budget/models/analysis/UserAmount';
const props = defineProps({
  entries: {
    type: Array as PropType<Array<MonthlyUserAnalysis>>,
    required: true,
  },
  user: {
    type: UserData,
    required: true,
  },
});

const getLastAnalysis = (): MonthlyUserAnalysis | null => {
  if (props.entries.length === 0) {
    return null;
  }
  return props.entries.map((e) => e).sort(MonthlyUserAnalysis.sort)[
    props.entries.length - 1
  ];
};

const getUser = (entry: MonthlyUserAnalysis): UserAmount | null => {
  const users = entry.users.filter((u) => u.userId === props.user.id);
  if (users.length === 0) {
    return null;
  }
  return users[0];
};

const loaded = computed(() => {
  return props.entries.length > 0 && getUser(props.entries[0]) !== null;
});

const credit = computed((): number => {
  const lastAnalysis = getLastAnalysis();
  if (lastAnalysis !== null) {
    const user = getUser(lastAnalysis);
    if (user) {
      return user.cumulatedCredit;
    }
  }
  return 0;
});

const isCredit = computed(() => credit.value > 0.01);
const isDebit = computed(() => credit.value < -0.01);
const isEven = computed(() => credit.value <= 0.01 && credit.value >= -0.01);

const abs = (value: number) => Math.abs(value);
const formatCurrency = (value: number) => NumberUtil.formatCurrency(value);
</script>
