<template>
  <q-table
    :flat="true"
    :rows="users"
    :columns="columns"
    row-key="id"
    :binary-state-sort="true"
  >
    <template v-slot:body-cell-actions="props">
      <q-td :props="props">
        <div class="q-pa-xs q-gutter-sm">
          <q-btn round icon="edit" @click="editUser(getUserId(props.row))" />
          <q-btn
            round
            icon="delete"
            @click="deleteUser(getUserId(props.row))"
          />
        </div>
      </q-td>
    </template>
  </q-table>
</template>

<script setup lang="ts">
import { PropType } from 'vue';
import BoardUser from 'src/budget/models/BoardUser';

const props = defineProps({
  users: {
    type: Array as PropType<Array<BoardUser>>,
    required: true,
  },
});

const getUsername = (user: BoardUser): string => user.user.username;
const getUserId = (user: BoardUser): number => user.user.id;
const getUserRole = (user: BoardUser): string => user.role;

const columns = [
  {
    name: 'username',
    required: true,
    label: 'Nome',
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    field: (row: BoardUser) => getUsername(row),
    align: 'left',
    sortable: true,
  },
  {
    name: 'role',
    required: true,
    label: 'Ruolo',
    field: (row: BoardUser) => getUserRole(row),
    align: 'left',
    sortable: true,
  },
  {
    name: 'actions',
    required: true,
    label: '',
    field: (row: BoardUser) => getUserId(row),
    align: 'right',
    sortable: false,
  },
];

const editUser = (userId: number) => {
  console.log(`Edit user ${userId}`);
};

const deleteUser = (userId: number) => {
  console.log(`Delete user ${userId}`);
};
</script>
