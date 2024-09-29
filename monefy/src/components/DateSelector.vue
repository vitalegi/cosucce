<template>
  <q-input
    outlined
    filled
    readonly
    v-model="model"
    hide-bottom-space
    :rules="[(val) => (val && val.trim().length !== 0) || 'Value is mandatory']"
  >
    <template v-slot:append>
      <q-icon name="event" class="cursor-pointer">
        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
          <q-date v-model="model" first-day-of-week="1" :mask="mask">
            <div class="row items-center justify-end">
              <q-btn v-close-popup label="Close" color="primary" flat />
            </div>
          </q-date>
        </q-popup-proxy>
      </q-icon>
    </template>
  </q-input>
</template>

<script setup lang="ts">
const model = defineModel<string>();

interface Props {
  mask: string;
}

defineProps<Props>();
</script>
