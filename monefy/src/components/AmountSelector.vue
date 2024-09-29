<template>
  <q-input
    outlined
    v-model="model"
    :step="0.01"
    label="Amount"
    type="number"
    hide-bottom-space
    :rules="[
      (val) => (val && val.trim().length !== 0) || 'Value is mandatory',
      (val) => val > 0 || 'Value must be positive',
    ]"
  />
</template>
<script setup lang="ts">
const model = defineModel<string>();
</script>
