<template>
  <span class="balance-entry-value" :class="amountType">
    <span class="text-weight-medium"> {{ formatAmountIntPart(amount) }}</span>
    <span class="decimal-part"
      >,{{ formatAmountDecimalPart(amount) }} {{ currency }}
    </span>
  </span>
</template>

<script setup lang="ts">
import bigDecimal from 'js-big-decimal';
import { ExpenseType } from 'src/model/expense-type';
import BigDecimalUtil from 'src/utils/big-decimal-util';
import { computed } from 'vue';

interface Props {
  amount: string;
  currency: string;
  type: ExpenseType;
}

const props = defineProps<Props>();

const amountType = computed(() =>
  props.type === 'credit' ? 'credit' : 'debit',
);

function formatAmountIntPart(amount: string): string {
  const val = new bigDecimal(amount);
  const parts = BigDecimalUtil.format(val);
  return parts.integerPart;
}

function formatAmountDecimalPart(amount: string): string {
  const val = new bigDecimal(amount);
  const parts = BigDecimalUtil.format(val);
  return parts.decimalPart;
}
</script>

<style lang="scss">
.decimal-part {
  font-size: smaller;
}
.balance-entry-value.debit {
  color: $debit;
}
.balance-entry-value.credit {
  color: $credit;
}
</style>
