<template>
  <el-card>
    <div slot="header" class="card-header">
      <span>积分发放</span>
    </div>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" style="max-width:500px">
      <el-form-item label="目标用户" prop="userId">
        <UserSelect v-model="form.userId" placeholder="请选择目标用户" />
      </el-form-item>
      <el-form-item label="积分数" prop="amount">
        <el-input v-model="form.amount" type="number" placeholder="数量" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" placeholder="发放原因" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit" :loading="loading">发放</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { grantPoints } from '../api/admin'
import UserSelect from '../components/UserSelect'

export default {
  name: 'PointsGrant',
  components: { UserSelect },
  data() {
    return {
      loading: false,
      form: { userId: '', amount: '', remark: '' },
      rules: {
        userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
        amount: [{ required: true, message: '请输入积分数', trigger: 'blur' }],
        remark: [{ required: true, message: '请输入备注', trigger: 'blur' }]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await grantPoints({ ...this.form, userId: Number(this.form.userId), amount: Number(this.form.amount) })
          this.$message.success('发放成功')
          this.$refs.formRef.resetFields()
        } catch {
          this.$message.error('发放失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.card-header { font-size: 15px; font-weight: 600; color: #303133; }
</style>
