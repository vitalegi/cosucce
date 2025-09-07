<template>
  <q-btn
    outline
    square
    :icon="data.icon"
    :color="data.color"
    :disable="!enabled"
    size="xl"
    class="full-width"
  />
</template>
<script setup lang="ts">
import IconUtil from 'src/budget/util/icon-util';
import { computed } from 'vue';

interface Props {
  icon: string;
  enabled: boolean;
}

const props = defineProps<Props>();

const data = computed(() => IconUtil.getIcon(props.icon));
</script>
