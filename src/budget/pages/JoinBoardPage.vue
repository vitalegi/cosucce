<template>
  <q-page>
    <div class="q-pa-md row justify-center">
      <q-form
        @submit="join"
        class="col-12 q-gutter-y-md column"
        style="max-width: 500px"
      >
        <q-input
          outlined
          v-model="token"
          label="Token"
          :rules="[(val) => validToken(val) || 'Token non valido']"
        />

        <q-btn label="Unisciti" type="submit" color="primary" />
        <q-btn flat label="Annulla" v-close-popup @click="back()" />
      </q-form>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import boardService from 'src/budget/integrations/BoardService';
import { useRouter } from 'vue-router';
import BoardInvite from 'src/budget/models/BoardInvite';

const router = useRouter();

const token = ref('');

const decodeToken = (val: string): BoardInvite => BoardInvite.fromToken(val);

const validToken = (val: string): boolean => {
  try {
    BoardInvite.fromToken(val);
    return true;
  } catch (e) {
    return false;
  }
};

const join = (): void => {
  const decoded = decodeToken(token.value);
  boardService.useBoardInvite(decoded.boardId, decoded.id);
  router.push('/');
};

const back = (): void => {
  router.push('/');
};
</script>
