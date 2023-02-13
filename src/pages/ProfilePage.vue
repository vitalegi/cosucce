<template>
  <q-page>
    <div class="q-pa-md row">
      <div class="q-pa-xs col-12 row">
        <div class="text-h2 section">Modifica nome utente</div>
      </div>
      <div class="col-12 q-gutter-y-md column">
        <q-input outlined v-model="username" label="Nome utente">
          <template v-slot:prepend>
            <q-icon
              v-if="!done"
              color="primary"
              name="save"
              class="cursor-pointer"
              @click="updateUsername"
            />
            <q-icon
              v-if="done"
              name="check"
              class="cursor-pointer"
              style="color: green"
            />
          </template>
        </q-input>
      </div>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import userService from 'src/integrations/UserService';
import { ref } from 'vue';

const username = ref('');
userService.getUser().then((u) => (username.value = u.username));

const done = ref(false);

const updateUsername = async (): Promise<void> => {
  done.value = false;
  await userService.updateUsername(username.value);
  done.value = true;
  setTimeout(() => {
    done.value = false;
  }, 1300);
};
</script>
