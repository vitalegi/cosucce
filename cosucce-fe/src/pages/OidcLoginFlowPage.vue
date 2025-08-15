<template>
  <q-page class="q-gutter-xs items-center justify-evenly"> </q-page>
</template>

<script setup lang="ts">
import authService from 'src/auth/auth-service';
import { useRoute, useRouter } from 'vue-router';
import { castQueryParamToString } from 'src/utils/params-util';
import Base64Utils from 'src/utils/base64';

const route = useRoute();
const router = useRouter();

function state2path(state: string): string {
  console.log(`state: ${state}`);
  if (state.indexOf('?') === -1) {
    console.log('no query params', state);
    return state;
  }
  const out = state.substring(0, state.indexOf('?'));
  console.log('with query params', out);
  return out;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function state2query(state: string): Record<string, any> {
  const out: Record<string, string> = {};
  if (state.indexOf('?') === -1) {
    return out;
  }
  const queryParams = state.substring(state.indexOf('?') + 1);
  const params = new URLSearchParams(queryParams);
  params.forEach((value, key) => (out[key] = value));
  return out;
}

async function completeLogin(code: string, state: string) {
  console.log(`Do login code.length=${code.length}, state=${state}`);
  await authService.tokenAuthorization(
    code,
    `${process.env.SELF_URL}/oidc/login`,
  );
  const path = state2path(Base64Utils.decodeUrl(state));
  const query = state2query(Base64Utils.decodeUrl(state));
  console.log(`Forward to path: ${path}, query params:`, query);
  void router.push({ path: path, query: query });
}

const code = route.query['code'];
const state = route.query['state'];
if (code && state) {
  void completeLogin(
    castQueryParamToString(code),
    castQueryParamToString(state),
  );
}
</script>
