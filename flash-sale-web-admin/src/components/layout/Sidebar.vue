<template>
  <div class="sidebar-container" :class="{ collapsed: collapsed }">
    <div class="logo">
      <i class="el-icon-s-platform logo-icon" />
      <span v-if="!collapsed" class="logo-title">FlashSale</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      background-color="#001529"
      text-color="rgba(255,255,255,0.7)"
      active-text-color="#409EFF"
      router
    >
      <el-menu-item index="/dashboard">
        <i class="el-icon-s-home" /><span slot="title">仪表盘</span>
      </el-menu-item>
      <el-menu-item index="/users">
        <i class="el-icon-user" /><span slot="title">用户管理</span>
      </el-menu-item>
      <el-menu-item index="/products">
        <i class="el-icon-goods" /><span slot="title">商品管理</span>
      </el-menu-item>
      <el-menu-item index="/coupon-grant">
        <i class="el-icon-present" /><span slot="title">优惠券发放</span>
      </el-menu-item>
      <el-menu-item index="/points-grant">
        <i class="el-icon-money" /><span slot="title">积分发放</span>
      </el-menu-item>
      <el-menu-item index="/settings">
        <i class="el-icon-setting" /><span slot="title">系统配置</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'Sidebar',
  computed: {
    activeMenu() {
      const meta = this.$route.meta
      if (meta && meta.activeMenu) return meta.activeMenu
      return this.$route.path
    },
    collapsed() {
      return this.$store.getters.sidebarCollapsed
    }
  }
}
</script>

<style scoped>
.sidebar-container {
  transition: width 0.28s;
  width: 210px;
  height: 100%;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1001;
  overflow: hidden;
  background-color: #001529;
}
.sidebar-container.collapsed {
  width: 64px;
}
.sidebar-container >>> .el-menu {
  border-right: none;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.05);
}
.logo-title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  margin-left: 8px;
}
.logo-icon {
  font-size: 22px;
  color: #409EFF;
  flex-shrink: 0;
}
</style>
