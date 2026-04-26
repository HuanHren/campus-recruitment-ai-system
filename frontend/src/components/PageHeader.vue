<template>
  <div class="page-head">
    <div>
      <h1>{{ title }}</h1>
      <p>{{ desc }}</p>
    </div>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  title: String,
  desc: String
})
</script>
