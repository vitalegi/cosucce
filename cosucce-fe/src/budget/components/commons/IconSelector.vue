<template>
  <q-btn
    outline
    square
    v-if="selected"
    :icon="icon"
    :color="iconColor"
    size="xl"
    @click="showDialog = true"
    class="full-width"
  />
  <q-btn
    outline
    square
    v-else
    icon="pending"
    color="grey"
    size="xl"
    @click="showDialog = true"
    class="full-width"
  />
  <q-dialog v-model="showDialog" full-height full-width>
    <q-card class="q-px-sm q-pb-md">
      <q-card-actions :align="'right'">
        <q-btn icon="close" flat round dense v-close-popup />
      </q-card-actions>
      <q-card-section class="row q-col-gutter-xs">
        <div class="col-xs-4 col-md-3 col-lg-2 col-xl-1" v-for="entry in icons" :key="entry.icon">
          <q-btn
            :outline="entry.icon !== icon"
            square
            :icon="entry.icon"
            :color="entry.color"
            size="xl"
            class="full-width"
            @click="icon = entry.icon"
          ></q-btn>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>
<script setup lang="ts">
import IconUtil from 'src/budget/util/icon-util';
import { computed, ref } from 'vue';

const icon = defineModel<string>();

const showDialog = ref(false);

const selected = computed(() => icon.value !== undefined && icon.value !== '');

const icons = computed(() => IconUtil.allIcons());

const iconColor = computed(() => {
  const value = icon.value;
  if (value === undefined || value === '') {
    return '';
  }
  try {
    return IconUtil.getIcon(value).color;
  } catch {
    return '';
  }
});
</script>
