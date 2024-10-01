<template>
  <q-form class="col-12 q-pa-md q-gutter-y-md" style="max-width: 600px" greedy>
    <q-btn-group spread>
      <q-btn
        :color="editor.type === 'debit' ? 'primary' : undefined"
        label="Debit"
        icon="remove"
        @click="
          editor.type = 'debit';
          save();
        "
      />
      <q-btn
        :color="editor.type === 'credit' ? 'primary' : undefined"
        label="Credit"
        icon="add"
        @click="
          editor.type = 'credit';
          save();
        "
      />
    </q-btn-group>

    <q-input
      outlined
      v-model="editor.name"
      label="Name"
      @update:model-value="save()"
      debounce="400"
    />
    <q-checkbox
      v-model="editor.active"
      label="Active"
      @update:model-value="save()"
    />
    <q-input
      outlined
      v-model="editor.icon"
      label="Icon"
      @update:model-value="save()"
      debounce="400"
    />
    <q-input
      outlined
      v-model="editor.color"
      label="Color"
      @update:model-value="save()"
      debounce="400"
    />
  </q-form>
</template>
<script setup lang="ts">
import { ExpenseType } from 'src/model/expense-type';
import { useExpenseStore } from 'src/stores/expenses-store';
import { onMounted, ref } from 'vue';

const expenseStore = useExpenseStore();

interface Props {
  id: string;
  type: ExpenseType;
  name: string;
  active: boolean;
  icon: string;
  color: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: '',
});

const editor = ref<{
  id: string;
  type: ExpenseType;
  name: string;
  active: boolean;
  icon: string;
  color: string;
}>({
  id: '',
  type: 'credit',
  name: '',
  active: true,
  icon: '',
  color: '',
});

onMounted(() => {
  if (props.id) {
    editor.value.id = props.id;
  }
  if (props.type) {
    editor.value.type = props.type;
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

function save(): void {
  expenseStore.updateCategory(
    editor.value.id,
    editor.value.name,
    editor.value.active,
    editor.value.type,
    editor.value.icon,
    editor.value.color,
  );
}
</script>
