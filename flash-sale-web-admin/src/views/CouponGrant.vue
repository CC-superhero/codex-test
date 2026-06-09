<template>
  <el-card>
    <div slot="header" class="card-header">
      <span>优惠券发放</span>
    </div>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" style="max-width:500px">
      <el-form-item label="目标用户" prop="userId">
        <UserSelect v-model="form.userId" placeholder="请选择目标用户" />
      </el-form-item>
      <el-form-item label="券名称" prop="name">
        <el-input v-model="form.name" placeholder="如：新人优惠券" />
      </el-form-item>
      <el-form-item label="面值" prop="faceValue">
        <el-input v-model="form.faceValue" type="number" placeholder="金额" />
      </el-form-item>
      <el-form-item label="有效期" prop="expireTime">
        <el-date-picker v-model="form.expireTime" type="datetime" placeholder="选择时间" value-format="yyyy-MM-dd HH:mm:ss" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit" :loading="loading">发放</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { grantCoupon } from '../api/admin'
import UserSelect from '../components/UserSelect'

export default {
  name: 'CouponGrant',
  components: { UserSelect },
  data() {
    return {
      loading: false,
      form: { userId: '', name: '', faceValue: '', expireTime: '' },
      rules: {
        userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
        name: [{ required: true, message: '请输入券名称', trigger: 'blur' }],
        faceValue: [{ required: true, message: '请输入面值', trigger: 'blur' }],
        expireTime: [{ required: true, message: '请选择有效期', trigger: 'change' }]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await grantCoupon({ ...this.form, userId: Number(this.form.userId), faceValue: Number(this.form.faceValue) })
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
