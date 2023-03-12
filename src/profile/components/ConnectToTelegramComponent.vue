<template>
  <div class="col-12">
    <div class="q-pa-xs col-12 row">
      <div class="text-h2 section">Notifiche Telegram</div>
      <div class="col-12" v-if="isConnected">
        L'account risulta già connesso a Telegram.
      </div>
      <div class="col-12">
        Collega il profilo a Telegram per essere notificato in tempo reale:
        <ol>
          <li>Copia il codice d'accesso (OTP)</li>
          <li>
            Clicca sul seguente link per collegati al bot telegram
            <a href="https://t.me/spesucce_notify_bot" target="_blank">
              @Notify
            </a>
          </li>
          <li>Incolla il codice d'accesso nella chat @Notify</li>
          <li>Il bot ti notificherà in base all'esito.</li>
        </ol>
      </div>
    </div>
    <div class="col-12 q-gutter-y-md column">
      <q-input outlined v-model="otp" readonly filled label="OTP">
        <template v-slot:prepend>
          <q-icon
            v-if="!done"
            color="primary"
            name="content_copy"
            class="cursor-pointer"
            @click="copyToClipboard(otp)"
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
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

import userService from 'src/integrations/UserService';
import UserData from 'src/models/UserData';
import ObjectUtil from 'src/utils/ObjectUtil';

const props = defineProps({ user: UserData });

const otp = ref('');

const createOtp = async (): Promise<void> => {
  const entry = await userService.addOtp();
  otp.value = entry.otp;
};

createOtp();
setInterval(() => {
  createOtp();
}, 120000);

const done = ref(false);

const copyToClipboard = async (text: string): Promise<void> => {
  await navigator.clipboard.writeText(text);
  done.value = true;
  setTimeout(() => {
    done.value = false;
  }, 1300);
};

const isConnected = computed(() =>
  ObjectUtil.isNotNullOrUndefined(props.user?.telegramUserId)
);
</script>
