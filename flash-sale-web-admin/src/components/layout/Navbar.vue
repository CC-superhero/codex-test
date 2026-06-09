<template>
  <div class="navbar">
    <div class="navbar-left">
      <i
        :class="collapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
        class="hamburger"
        @click="toggleSidebar"
      />
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="{ path: item.path }">
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="navbar-right">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="user-dropdown">
          <i class="el-icon-user-solid" style="margin-right:6px;font-size:16px" />
          {{ username }}
          <i class="el-icon-arrow-down el-icon--right" />
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="logout" icon="el-icon-switch-button">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Navbar',
  computed: {
    collapsed() {
      return this.$store.getters.sidebarCollapsed
    },
    username() {
      return this.$store.getters.userInfo.username || '管理员'
    },
    breadcrumbs() {
      return this.$route.matched
        .filter(item => item.meta && item.meta.title)
        .map(item => ({
          path: item.redirect || item.path,
          title: item.meta.title
        }))
    }
  },
  methods: {
    toggleSidebar() {
      this.$store.commit('TOGGLE_SIDEBAR')
    },
    handleCommand(cmd) {
      if (cmd === 'logout') {
        this.$store.dispatch('logout')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  flex-shrink: 0;
}
.navbar-left {
  display: flex;
  align-items: center;
}
.hamburger {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
  color: #606266;
  transition: color 0.3s;
}
.hamburger:hover {
  color: #409EFF;
}
.navbar-right {
  display: flex;
  align-items: center;
}
.user-dropdown {
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}
</style>
