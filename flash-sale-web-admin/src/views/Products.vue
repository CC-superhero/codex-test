<template>
  <el-card>
    <div slot="header" class="card-header">
      <span>商品管理</span>
      <el-button type="primary" size="small" style="float:right" @click="$router.push('/product/form')">
        发布商品
      </el-button>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="remainStock" label="库存" width="120">
        <template slot-scope="{ row }">{{ row.remainStock }} / {{ row.totalStock }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag :type="row.status === 1 ? 'danger' : row.status === 0 ? 'warning' : 'info'" size="small">
            {{ row.status === 0 ? '未开始' : row.status === 1 ? '进行中' : '已结束' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="180" />
      <el-table-column prop="endTime" label="结束时间" width="180" />
      <el-table-column label="操作" width="200">
        <template slot-scope="{ row }">
          <el-button size="small" @click="$router.push('/product/form/' + row.id)">编辑</el-button>
          <el-popconfirm title="确定删除该商品吗？" @confirm="handleDelete(row.id)">
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
import { deleteProduct, getProductList } from '../api/admin'

export default {
  name: 'Products',
  data() {
    return {
      list: [],
      loading: false,
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
        const res = await getProductList({ page: this.page, size: this.size })
        this.list = (res.data && res.data.records) || []
        this.total = (res.data && res.data.total) || 0
      } finally {
        this.loading = false
      }
    },
    async handleDelete(id) {
      try {
        await deleteProduct(id)
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
</style>
