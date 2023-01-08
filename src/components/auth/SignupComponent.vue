<template>
  <div class="q-pa-md" style="max-width: 400px">
    <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
      <q-input
        outlined
        v-model="email"
        label="E-Mail"
        lazy-rules
        autocomplete="email"
        :rules="[(val) => (val && val.length > 0) || 'Please type something']"
      />
      <q-input
        outlined
        v-model="password"
        type="password"
        label="Password"
        lazy-rules
        autocomplete="new-password"
        :rules="[
          (val) =>
            (val && val.length >= 8) ||
            'Minimum length of password is 8 characters',
        ]"
      />
      <div v-if="error !== ''">{{ errorMessage }}</div>
      <div>
        <q-btn label="Submit" type="submit" color="primary" />
        <q-btn
          label="Reset"
          type="reset"
          color="primary"
          flat
          class="q-ml-sm"
        />
      </div>
    </q-form>
  </div>
</template>

<script setup lang="ts">
import {
  getAuth,
  createUserWithEmailAndPassword,
  sendEmailVerification,
} from 'firebase/auth';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import userService from 'src/integrations/UserService';

const firebaseErrors = new Map<string, string>();
firebaseErrors.set('auth/email-already-in-use', 'E-Mail già in uso');
firebaseErrors.set('auth/invalid-email', 'E-Mail non valida');

const email = ref('');
const password = ref('');
const error = ref('');
const router = useRouter();

async function onSubmit() {
  const auth = getAuth();
  try {
    const userCredential = await createUserWithEmailAndPassword(
      auth,
      email.value,
      password.value
    );
    const user = userCredential.user;
    await userService.getUser();
    console.log(`Created user ${user.email}`);
    await sendEmailVerification(user);
    console.log('Email verification sent');
    router.push('/');
  } catch (e: any) {
    console.error(`Failed with error ${e.code}`);
    error.value = e.code;
  }
}
function onReset() {
  email.value = '';
  password.value = '';
  error.value = '';
}
const errorMessage = computed(() => {
  if (firebaseErrors.has(error.value)) {
    return firebaseErrors.get(error.value);
  }
  return error.value;
});
</script>
