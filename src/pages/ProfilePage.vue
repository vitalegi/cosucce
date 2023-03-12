<template>
  <q-page>
    <div class="q-pa-md row">
      <ChangeUsernameComponent />
      <ConnectToTelegramComponent :user="user" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import userService from 'src/integrations/UserService';
import UserData from 'src/models/UserData';
import ConnectToTelegramComponent from 'src/profile/components/ConnectToTelegramComponent.vue';
import ChangeUsernameComponent from 'src/profile/components/ChangeUsernameComponent.vue';

const user = ref(new UserData());
userService.getUser().then((u) => {
  user.value = u;
});
</script>
