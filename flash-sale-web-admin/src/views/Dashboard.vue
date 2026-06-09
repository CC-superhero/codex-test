<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card stat-card--blue">
          <div class="stat-body">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
          <div class="stat-icon"><i class="el-icon-user" /></div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--green">
          <div class="stat-body">
            <div class="stat-value">{{ stats.productCount }}</div>
            <div class="stat-label">商品总数 / 进行中 {{ stats.activeProductCount }}</div>
          </div>
          <div class="stat-icon"><i class="el-icon-goods" /></div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--orange">
          <div class="stat-body">
            <div class="stat-value">{{ stats.orderCount }}</div>
            <div class="stat-label">今日完成订单</div>
          </div>
          <div class="stat-icon"><i class="el-icon-s-order" /></div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--purple">
          <div class="stat-body">
            <div class="stat-value">{{ stats.inventoryCount }}</div>
            <div class="stat-label">库存总量 / 在售 {{ stats.listedInventoryCount }}</div>
          </div>
          <div class="stat-icon"><i class="el-icon-s-grid" /></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getDashboardStats } from '../api/admin'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {
        userCount: 0,
        productCount: 0,
        activeProductCount: 0,
        orderCount: 0,
        inventoryCount: 0,
        listedInventoryCount: 0
      }
    }
  },
  created() {
    this.fetchStats()
  },
  methods: {
    async fetchStats() {
      try {
        const res = await getDashboardStats()
        this.stats = { ...this.stats, ...(res.data || {}) }
      } catch {
        this.$message.error('统计数据加载失败')
      }
    }
  }
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.stat-icon {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #fff;
}

.stat-card--blue .stat-icon {
  background: #409eff;
}

.stat-card--green .stat-icon {
  background: #67c23a;
}

.stat-card--orange .stat-icon {
  background: #e6a23c;
}

.stat-card--purple .stat-icon {
  background: #b37feb;
}
</style>
