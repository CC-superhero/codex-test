<template>
  <div id="app">
    <div v-if="isLogin" class="app-wrapper">
      <Sidebar />
      <div class="main-container" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
        <Navbar />
        <TagsView />
        <div class="app-main">
          <router-view />
        </div>
      </div>
    </div>
    <router-view v-else />
  </div>
</template>

<script>
import Sidebar from './components/layout/Sidebar'
import Navbar from './components/layout/Navbar'
import TagsView from './components/layout/TagsView'

export default {
  name: 'App',
  components: { Sidebar, Navbar, TagsView },
  computed: {
    isLogin() {
      return !!this.$store.getters.token
    },
    sidebarCollapsed() {
      return this.$store.getters.sidebarCollapsed
    }
  }
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; }
.app-wrapper {
  display: flex;
  width: 100%;
  height: 100%;
}
.main-container {
  min-height: 100%;
  transition: margin-left 0.28s;
  margin-left: 210px;
  position: relative;
  display: flex;
  flex-direction: column;
  flex: 1;
}
.main-container.sidebar-collapsed {
  margin-left: 64px;
}
.app-main {
  flex: 1;
  padding: 20px;
  background: #f0f2f5;
  overflow-y: auto;
}
</style>
