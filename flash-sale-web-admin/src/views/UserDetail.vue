<template>
  <el-card>
    <div slot="header" class="card-header">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/users' }">用户管理</el-breadcrumb-item>
        <el-breadcrumb-item>用户详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="info">
        <el-descriptions v-if="tree.user" :column="2" border>
          <el-descriptions-item label="ID">{{ tree.user.id }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ tree.user.nickname }}</el-descriptions-item>
          <el-descriptions-item label="邀请码">{{ tree.user.inviteCode }}</el-descriptions-item>
          <el-descriptions-item label="积分">{{ tree.user.points }}</el-descriptions-item>
          <el-descriptions-item label="上级用户">{{ tree.parent ? tree.parent.nickname : '无' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="tree.user.status === 0 ? 'success' : 'danger'">
              {{ tree.user.status === 0 ? '正常' : '封禁' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
      <el-tab-pane label="邀请关系" name="invite">
        <el-tree :data="inviteTreeData" node-key="id" default-expand-all>
          <span slot-scope="{ data }" style="font-size:14px">
            {{ data.nickname }}
            <el-tag size="mini" type="warning">{{ data.inviteCode }}</el-tag>
          </span>
        </el-tree>
      </el-tab-pane>
      <el-tab-pane label="库存管理" name="inventory">
        <el-table :data="inventory" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="productName" label="商品名" />
          <el-table-column prop="price" label="价格" width="100" />
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="{ row }">
              <el-tag>{{ row.status === 1 ? '仓库' : '在售' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="{ row }">
              <el-button size="small" type="primary" @click="showTransfer(row)">调拨</el-button>
              <el-popconfirm title="确定删除该库存吗？" @confirm="handleDelInv(row.id)">
                <el-button slot="reference" size="small" type="danger">删除</el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="库存调拨" :visible.sync="transferVisible" width="450px">
      <el-form>
        <el-form-item label="目标用户">
          <UserSelect v-model="targetUserId" placeholder="请选择接收用户" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!targetUserId" @click="handleTransfer">确认调拨</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script>
import { deleteInventory, getUserInventory, getUserInviteTree, transferInventory } from '../api/admin'
import UserSelect from '../components/UserSelect'

export default {
  name: 'UserDetail',
  components: { UserSelect },
  data() {
    return {
      activeTab: 'info',
      tree: {},
      inventory: [],
      transferVisible: false,
      targetUserId: '',
      currentInvId: null
    }
  },
  computed: {
    inviteTreeData() {
      if (!this.tree.tree) return []
      const buildNode = (user) => ({
        id: user.id,
        nickname: user.nickname,
        inviteCode: user.inviteCode,
        children: []
      })
      return this.tree.tree.map((item) => {
        const node = buildNode(item.user)
        if (item.children && item.children.length) {
          node.children = item.children.map(buildNode)
        }
        return node
      })
    }
  },
  created() {
    this.fetchTree()
    this.fetchInventory()
  },
  methods: {
    async fetchTree() {
      try {
        const res = await getUserInviteTree(this.$route.params.id)
        this.tree = res.data || {}
      } catch {
        this.$message.error('邀请关系加载失败')
      }
    },
    async fetchInventory() {
      try {
        const res = await getUserInventory(this.$route.params.id)
        this.inventory = res.data || []
      } catch {
        this.$message.error('库存加载失败')
      }
    },
    showTransfer(row) {
      this.currentInvId = row.id
      this.targetUserId = ''
      this.transferVisible = true
    },
    async handleTransfer() {
      try {
        await transferInventory({
          inventoryId: this.currentInvId,
          targetUserId: Number(this.targetUserId)
        })
        this.$message.success('调拨成功')
        this.transferVisible = false
        this.fetchInventory()
      } catch {
        this.$message.error('调拨失败')
      }
    },
    async handleDelInv(id) {
      try {
        await deleteInventory(id)
        this.$message.success('删除成功')
        this.fetchInventory()
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
