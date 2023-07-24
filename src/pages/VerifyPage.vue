<template>
  <q-page class="row items-center justify-evenly">
    <q-card class="q-pa-md" style="max-width: 400px">
      <q-card-section>
        <div class="text-h6">Mail non verificata</div>
      </q-card-section>
      <q-card-section>
        Ciao, la mail <b>{{ email }}</b> non è verificata. Controlla di aver
        ricevuto la mail di conferma.
      </q-card-section>
      <q-card-section class="row justify-center">
        <q-btn color="primary" label="Invia nuovamente" size="22px" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { getUser } from 'boot/firebase';
import { getAuth, sendEmailVerification } from 'firebase/auth';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const email = ref('');
getUser().then((u) => {
  if (u !== null && u.email !== null) {
    email.value = u.email;
  }
});

const sendMail = async (): Promise<void> => {
  console.log('send mail');
  const auth = await getAuth();
  if (auth !== null && auth.currentUser !== null) {
    console.log('send mail!');
    await sendEmailVerification(auth.currentUser);
  }
};
</script>
