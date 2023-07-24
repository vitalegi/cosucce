<template>
  <q-page>
    <div class="q-pa-md row">
      <div
        v-for="product in products"
        :key="product.id"
        class="q-pa-xs col-xs-12 col-sm-4 col-md-3"
      >
        <q-card @click="goToProduct(product.to)">
          <q-card-section>
            <div class="text-h6">{{ product.label }}</div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const products = computed(() => [
  {
    id: 'BUDGET',
    label: 'Budget',
    to: '/boards',
  },
  {
    id: 'SPANDO',
    label: 'Spando',
    to: '/spando',
  },
]);

const goToProduct = (to: string): void => {
  router.push(to);
};
</script>
