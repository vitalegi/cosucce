<template>
  <q-btn
    v-for="option in options"
    :key="option.interval"
    class="time-interval"
    color="primary"
    :outline="interval !== option.interval"
    :label="option.label"
    @click="change(option.interval)"
  />
</template>
<script setup lang="ts">
import TimeInterval from 'src/model/interval';
import { useIntervalStore } from 'src/stores/interval-store';
import { computed } from 'vue';

const intervalStore = useIntervalStore();

const interval = computed(() => intervalStore.interval);

const options: { label: string; interval: TimeInterval }[] = [
  { label: 'Daily', interval: 'daily' },
  { label: 'Weekly', interval: 'weekly' },
  { label: 'Monthly', interval: 'monthly' },
  { label: 'Yearly', interval: 'yearly' },
  { label: 'All', interval: 'all' },
];

function change(newValue: TimeInterval): void {
  intervalStore.change(newValue);
}
</script>

<style lang="scss" scoped>
.time-interval {
  min-height: 56px;
  width: 100%;
}
</style>
