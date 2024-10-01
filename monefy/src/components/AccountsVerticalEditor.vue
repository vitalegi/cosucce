<template>
  <q-list>
    <q-expansion-item class="bg-teal-1">
      <template v-slot:header>
        <q-item-section avatar>
          <q-icon color="primary" name="add" />
        </q-item-section>
        <q-item-section>New</q-item-section>
      </template>
      <q-card>
        <q-card-section>
          <AccountEditor
            id=""
            currency="EUR"
            name=""
            :active="true"
            icon=""
            color=""
          />
        </q-card-section>
      </q-card>
    </q-expansion-item>
    <q-expansion-item
      v-for="element in elements"
      :key="element.id"
      expand-separator
      :label="element.name"
    >
      <template v-slot:header>
        <q-item-section avatar>
          <q-icon :color="element.color" :name="element.icon" />
        </q-item-section>
        <q-item-section> {{ element.name }}</q-item-section>
      </template>
      <q-card>
        <q-card-section>
          <AccountEditor
            :id="element.id"
            :currency="element.currency"
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
import AccountEditor from './AccountEditor.vue';

const expenseStore = useExpenseStore();

const elements = computed(() => expenseStore.accounts);
</script>
