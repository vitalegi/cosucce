<template>
  <q-form class="col-12 q-pa-md q-gutter-y-md" style="max-width: 600px" greedy>
    <q-input
      outlined
      v-model="editor.name"
      label="Name"
      @update:model-value="update()"
      debounce="400"
    />
    <q-input
      outlined
      v-model="editor.currency"
      label="Currency"
      @update:model-value="update()"
      debounce="400"
    />
    <q-checkbox
      v-model="editor.active"
      label="Active"
      @update:model-value="update()"
    />
    <q-input
      outlined
      v-model="editor.icon"
      label="Icon"
      @update:model-value="update()"
      debounce="400"
    />
    <q-input
      outlined
      v-model="editor.color"
      label="Color"
      @update:model-value="update()"
      debounce="400"
    />
    <q-btn
      v-if="addMode"
      @click="save()"
      class="full-width"
      size="xl"
      color="primary"
      >Add</q-btn
    >
  </q-form>
</template>
<script setup lang="ts">
import { useExpenseStore } from 'src/stores/expenses-store';
import { computed, onMounted, ref } from 'vue';

const expenseStore = useExpenseStore();

interface Props {
  id: string;
  name: string;
  currency: string;
  active: boolean;
  icon: string;
  color: string;
}

const props = defineProps<Props>();

const editor = ref<{
  id: string;
  name: string;
  currency: string;
  active: boolean;
  icon: string;
  color: string;
}>({
  id: '',
  currency: 'EUR',
  name: '',
  active: true,
  icon: '',
  color: '',
});
const addMode = computed(() => props.id === '');

function update(): void {
  if (addMode.value) {
    return;
  }
  save();
}

function save(): void {
  if (props.id !== '') {
    expenseStore.updateAccount(
      editor.value.id,
      editor.value.name,
      editor.value.currency,
      editor.value.active,
      editor.value.icon,
      editor.value.color,
    );
  } else {
    expenseStore.addAccount(
      editor.value.name,
      editor.value.currency,
      editor.value.active,
      editor.value.icon,
      editor.value.color,
    );
  }
}

onMounted(() => {
  if (props.id) {
    editor.value.id = props.id;
  }
  if (props.currency) {
    editor.value.currency = props.currency;
  }
  if (props.name) {
    editor.value.name = props.name;
  }
  if (props.active) {
    editor.value.active = props.active;
  }
  if (props.icon) {
    editor.value.icon = props.icon;
  }
  if (props.color) {
    editor.value.color = props.color;
  }
});
</script>
