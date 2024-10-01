<template>
  <q-list bordered separator>
    <q-expansion-item
      v-for="element in elements"
      :key="element.id"
      expand-separator
      :label="element.name"
      caption="John Doe"
    >
      <template v-slot:header>
        <q-item-section avatar>
          <q-icon :color="element.color" :name="element.icon" />
        </q-item-section>
        <q-item-section> {{ element.name }}</q-item-section>
      </template>
      <q-card>
        <q-card-section>
          <CategoryEditor
            :id="element.id"
            :type="element.type"
            :name="element.name"
            :active="element.active"
            :icon="element.icon"
            :color="element.color"
          />
        </q-card-section>
      </q-card>
    </q-expansion-item>
  </q-list>
</template>
<script setup lang="ts">
import { useExpenseStore } from 'src/stores/expenses-store';
import { computed } from 'vue';
import CategoryEditor from './CategoryEditor.vue';

const expenseStore = useExpenseStore();

const elements = computed(() => expenseStore.categories());
</script>
