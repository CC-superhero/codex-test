<template>
  <el-card>
    <div slot="header" class="card-header">
      <span>用户管理</span>
    </div>
    <el-row :gutter="10" class="search-bar">
      <el-col :span="6">
        <el-input
          v-model="keyword"
          placeholder="搜索昵称"
          clearable
          size="small"
          @clear="fetchList"
          @keyup.enter.native="fetchList"
        />
      </el-col>
      <el-col :span="4">
        <el-button type="primary" size="small" @click="fetchList">搜索</el-button>
      </el-col>
    </el-row>
    <el-table :data="list" border stripe v-loading="loading" style="margin-top:12px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="inviteCode" label="邀请码" width="120" />
      <el-table-column prop="points" label="积分" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
            {{ row.status === 0 ? '正常' : '封禁' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template slot-scope="{ row }">
          <el-button size="small" @click="$router.push('/user/' + row.id)">详情</el-button>
          <el-button
            size="small"
            :type="row.status === 0 ? 'danger' : 'success'"
            @click="handleBan(row)"
          >
            {{ row.status === 0 ? '封禁' : '解封' }}
          </el-button>
          <el-popconfirm title="确定删除该用户吗？" @confirm="handleDelete(row.id)">
            <el-button slot="reference" size="small" type="danger">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top:16px;text-align:right"
      background
      layout="prev,pager,next"
      :total="total"
      :page-size="size"
      :current-page.sync="page"
      @current-change="fetchList"
    />
  </el-card>
</template>

<script>
import { banUser, deleteUser, getUserList } from '../api/admin'

export default {
  name: 'Users',
  data() {
    return {
      list: [],
      loading: false,
      keyword: '',
      page: 1,
      size: 20,
      total: 0
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await getUserList({ page: this.page, size: this.size, keyword: this.keyword })
        this.list = (res.data && res.data.records) || []
        this.total = (res.data && res.data.total) || 0
      } finally {
        this.loading = false
      }
    },
    async handleBan(row) {
      try {
        const newStatus = row.status === 0 ? 1 : 0
        await banUser(row.id, newStatus)
        this.$message.success(newStatus === 1 ? '已封禁' : '已解封')
        this.fetchList()
      } catch {
        this.$message.error('操作失败')
      }
    },
    async handleDelete(id) {
      try {
        await deleteUser(id)
        this.$message.success('删除成功')
        this.fetchList()
      } catch {
        this.$message.error('删除失败')
      }
    }
  }
}
</script>

<style scoped>
.card-header {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.search-bar {
  margin-bottom: 0;
}
</style>
