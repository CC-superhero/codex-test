<template>
  <div class="page">
    <van-nav-bar title="登录" />
    <van-form @submit="onSubmit">
      <van-field
        v-model="nickname"
        label="昵称"
        placeholder="请输入昵称"
        :rules="[{ required: true, message: '请输入昵称' }]"
      />
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">登录</van-button>
      </div>
    </van-form>
    <div style="text-align:center">
      <van-button type="default" to="/register">没有账号？去注册</van-button>
    </div>
  </div>
</template>

<script>
import { login } from '../api/user'

export default {
  name: 'Login',
  data() {
    return {
      nickname: '',
      loading: false
    }
  },
  methods: {
    async onSubmit() {
      this.loading = true
      try {
        const res = await login({ nickname: this.nickname })
        this.$store.dispatch('login', { token: res.data.token, userInfo: res.data })
        this.$toast.success('登录成功')
        this.$router.push('/home')
      } catch {
        this.$toast.fail('登录失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
