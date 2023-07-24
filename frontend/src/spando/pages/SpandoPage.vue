<template>
  <q-page>
    <div class="q-pa-md row justify-center">
      <q-date
        multiple
        mask="YYYY-MM-DD"
        v-model="spandos"
        @update:model-value="update"
        :events="estimateEvents"
        event-color="red"
        :first-day-of-week="1"
      />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import spandoService from 'src/integrations/SpandoService';
import SpandoDays from 'src/spando/models/SpandoDays';

const spandos = ref(new Array<{ from: string; to: string } | string>());
const estimates = ref(new Array<SpandoDays>());

const estimateEvents = (date: string): boolean => {
  const split = date.split('/');
  const formatted = formatDate({
    year: parseInt(split[0], 10),
    month: parseInt(split[1], 10),
    day: parseInt(split[2], 10),
  });
  const matching = estimates.value
    .filter((e) => e.from.localeCompare(formatted) <= 0)
    .filter((e) => e.to.localeCompare(formatted) >= 0);

  return matching.length > 0;
};

const loadData = async (): Promise<void> => {
  const entries = await spandoService.getSpandos();
  const estimatedEntries = await spandoService.getEstimates();
  spandos.value = entries.map((e) => e.getCalendarEntry());
  estimates.value = estimatedEntries;
};

loadData();

type Details = {
  year: number;
  month: number;
  day: number;
  from?: { year: number; month: number; day: number } | undefined;
  to?: { year: number; month: number; day: number };
};

const formatDate = (details: {
  year: number;
  month: number;
  day: number;
}): string => {
  const mm = (details.month < 10 ? '0' : '') + details.month;
  const dd = (details.day < 10 ? '0' : '') + details.day;
  return `${details.year}-${mm}-${dd}`;
};

const update = async (
  value: any,
  reason:
    | 'mask'
    | 'add-day'
    | 'remove-day'
    | 'add-range'
    | 'remove-range'
    | 'locale'
    | 'year'
    | 'month',
  details: Details
): Promise<void> => {
  if (reason === 'add-day' || reason === 'remove-day') {
    await spandoService.changeSpandoEntry(formatDate(details));
    loadData();
  } else {
    console.log(`Non managed reason ${reason}`);
  }
};
</script>
