<template>
  <div class="page">
    <van-nav-bar title="注册" />
    <van-form @submit="onSubmit">
      <van-field
        v-model="form.nickname"
        label="昵称"
        placeholder="请输入昵称（必填）"
        :rules="[{ required: true, message: '请输入昵称' }]"
      />
      <van-field
        v-model="form.inviteCode"
        label="邀请码"
        placeholder="请输入邀请码（必填）"
        :rules="[{ required: true, message: '请输入邀请码' }]"
      />
      <van-field v-model="form.realName" label="真实姓名" placeholder="选填" />
      <van-field v-model="form.phone" label="手机号" placeholder="选填" />
      <van-field v-model="form.address" label="地址" placeholder="选填" />
      <van-field v-model="form.bankCard" label="银行卡号" placeholder="选填" />
      <van-field v-model="form.idCard" label="身份证号" placeholder="选填" />
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">注册</van-button>
      </div>
    </van-form>
  </div>
</template>

<script>
import { register } from '../api/user'

export default {
  name: 'Register',
  data() {
    return {
      loading: false,
      form: {
        nickname: '',
        inviteCode: '',
        realName: '',
        phone: '',
        address: '',
        bankCard: '',
        idCard: ''
      }
    }
  },
  methods: {
    async onSubmit() {
      this.loading = true
      try {
        const res = await register(this.form)
        this.$store.dispatch('login', { token: res.data.token, userInfo: res.data })
        this.$toast.success('注册成功')
        this.$router.push('/home')
      } catch {
        this.$toast.fail('注册失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
