<template>
  <el-card>
    <div slot="header" class="card-header">
      <span>系统配置</span>
    </div>
    <el-table :data="configList" border>
      <el-table-column prop="configKey" label="配置键" width="220" />
      <el-table-column prop="configValue" label="当前值" width="150" />
      <el-table-column prop="remark" label="说明" />
      <el-table-column label="操作" width="120">
        <template slot-scope="{ row }">
          <el-button size="small" type="primary" @click="showEdit(row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="修改配置" :visible.sync="editVisible" width="420px">
      <el-form label-width="90px">
        <el-form-item label="配置键">
          <el-input :value="editForm.configKey" disabled />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input v-model="editForm.configValue" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script>
import { getConfig, updateConfig } from '../api/admin'

export default {
  name: 'Settings',
  data() {
    return {
      configList: [],
      editVisible: false,
      editForm: {
        id: null,
        configKey: '',
        configValue: ''
      }
    }
  },
  created() {
    this.fetchConfig()
  },
  methods: {
    async fetchConfig() {
      try {
        const res = await getConfig()
        this.configList = res.data || []
      } catch {
        this.$message.error('配置加载失败')
      }
    },
    showEdit(row) {
      this.editForm = {
        id: row.id,
        configKey: row.configKey,
        configValue: row.configValue
      }
      this.editVisible = true
    },
    async handleSave() {
      try {
        await updateConfig(this.editForm)
        this.$message.success('修改成功')
        this.editVisible = false
        this.fetchConfig()
      } catch {
        this.$message.error('修改失败')
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
