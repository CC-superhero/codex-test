<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-header">
        <i class="el-icon-s-platform" style="font-size:56px;color:#409EFF" />
        <h2>后台管理系统</h2>
        <p>FlashSale Admin</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="el-icon-user" size="medium" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" size="medium" @keyup.enter.native="onSubmit" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="onSubmit" :loading="loading" size="medium">登 录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <div class="login-footer">Copyright &copy; 2024 FlashSale</div>
  </div>
</template>

<script>
import { adminLogin } from '../api/admin'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      form: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await adminLogin(this.form)
          this.$store.dispatch('login', { token: res.data.token, userInfo: res.data })
          this.$message.success('登录成功')
          this.$router.push('/dashboard')
        } catch {
          this.$message.error('用户名或密码错误')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: radial-gradient(ellipse at center, #2c3e50 0%, #1a252f 100%);
}
.login-card {
  width: 420px;
  border-radius: 6px;
  box-shadow: 0 2px 30px rgba(0,0,0,0.3);
}
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.login-header h2 {
  margin: 12px 0 4px;
  color: #303133;
  font-size: 22px;
}
.login-header p {
  margin: 0;
  color: #909399;
  font-size: 13px;
}
.login-footer {
  position: fixed;
  bottom: 24px;
  color: rgba(255,255,255,0.5);
  font-size: 12px;
}
</style>
