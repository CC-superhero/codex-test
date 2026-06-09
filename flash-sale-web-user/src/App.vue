<template>
  <div id="app">
    <router-view />
    <van-tabbar v-if="showTabbar" route>
      <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/inventory" icon="bag-o">仓库</van-tabbar-item>
      <van-tabbar-item to="/mine" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    showTabbar() {
      const hideRoutes = ['/login', '/register']
      return !hideRoutes.includes(this.$route.path)
    }
  }
}
</script>
