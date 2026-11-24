<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'DmDoc',
  data() {
    return {
      url: 'http://doc.dm.vip/dm-vue'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>