<template>
  <q-page class="row items-center justify-evenly">
    <q-card>
      <q-tabs
        v-model="tab"
        dense
        class="text-grey"
        active-color="primary"
        indicator-color="primary"
        align="justify"
        narrow-indicator
      >
        <q-tab name="login" label="Accedi" />
        <q-tab name="signup" label="Registrati" />
      </q-tabs>

      <q-separator />

      <q-tab-panels v-model="tab" animated>
        <q-tab-panel name="login">
          <SigninComponent />
        </q-tab-panel>

        <q-tab-panel name="signup">
          <SignupComponent />
        </q-tab-panel>
      </q-tab-panels>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import SigninComponent from 'components/auth/SigninComponent.vue';
import SignupComponent from 'components/auth/SignupComponent.vue';

const tab = ref<string>('login');
</script>
