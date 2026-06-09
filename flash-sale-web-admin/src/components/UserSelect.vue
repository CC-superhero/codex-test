<template>
  <div class="user-select">
    <el-input :value="displayText" :placeholder="placeholder" readonly @focus="openDialog">
      <el-button slot="append" icon="el-icon-search" @click="openDialog">选择用户</el-button>
    </el-input>

    <el-dialog title="选择用户" :visible.sync="dialogVisible" width="700px" @opened="onDialogOpened">
      <el-row :gutter="10" style="margin-bottom:12px">
        <el-col :span="16">
          <el-input
            v-model="keyword"
            placeholder="搜索用户昵称"
            clearable
            @clear="fetchUsers"
            @keyup.enter.native="fetchUsers"
          />
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="fetchUsers">搜索</el-button>
        </el-col>
      </el-row>

      <el-table
        :data="userList"
        border
        highlight-current-row
        v-loading="loading"
        max-height="400"
        @current-change="handleCurrentChange"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="inviteCode" label="邀请码" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '正常' : '封禁' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top:12px;text-align:right"
        background
        layout="prev,pager,next"
        :total="total"
        :page-size="pageSize"
        :current-page.sync="page"
        @current-change="fetchUsers"
      />

      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedUser" @click="confirmSelection">确认选择</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList } from '../api/admin'

export default {
  name: 'UserSelect',
  props: {
    value: { type: [Number, String], default: '' },
    placeholder: { type: String, default: '请选择用户' }
  },
  data() {
    return {
      dialogVisible: false,
      keyword: '',
      userList: [],
      loading: false,
      page: 1,
      pageSize: 15,
      total: 0,
      selectedUser: null
    }
  },
  computed: {
    displayText() {
      if (this.selectedUser && this.selectedUser.id === Number(this.value)) {
        return `${this.selectedUser.nickname} (ID: ${this.selectedUser.id})`
      }
      return ''
    }
  },
  watch: {
    value(val) {
      if (!val) this.selectedUser = null
    }
  },
  methods: {
    openDialog() {
      this.dialogVisible = true
    },
    onDialogOpened() {
      this.selectedUser = null
      this.fetchUsers()
    },
    async fetchUsers() {
      this.loading = true
      try {
        const res = await getUserList({ page: this.page, size: this.pageSize, keyword: this.keyword })
        this.userList = (res.data && res.data.records) || []
        this.total = (res.data && res.data.total) || 0
      } finally {
        this.loading = false
      }
    },
    handleCurrentChange(row) {
      this.selectedUser = row
    },
    confirmSelection() {
      if (!this.selectedUser) return
      this.$emit('input', this.selectedUser.id)
      this.$emit('change', this.selectedUser)
      this.dialogVisible = false
    }
  }
}
</script>
