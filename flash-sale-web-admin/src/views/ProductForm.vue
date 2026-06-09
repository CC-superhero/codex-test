<template>
  <el-card>
    <div slot="header" class="card-header">
      <span>{{ isEdit ? '编辑商品' : '发布商品' }}</span>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width:600px">
      <el-form-item label="商品名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input v-model="form.price" type="number" />
      </el-form-item>
      <el-form-item label="商品图片">
        <el-input v-model="form.image" placeholder="图片 URL" />
      </el-form-item>
      <el-form-item label="总库存" prop="totalStock">
        <el-input v-model="form.totalStock" type="number" />
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="form.startTime"
          type="datetime"
          placeholder="选择时间"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="form.endTime"
          type="datetime"
          placeholder="选择时间"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="onSubmit">{{ isEdit ? '保存' : '发布' }}</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { getProductDetail, publishProduct, updateProduct } from '../api/admin'

export default {
  name: 'ProductForm',
  data() {
    return {
      loading: false,
      form: {
        name: '',
        price: '',
        image: '',
        totalStock: '',
        startTime: '',
        endTime: ''
      },
      rules: {
        name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
        totalStock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
      }
    }
  },
  computed: {
    isEdit() {
      return !!this.$route.params.id
    }
  },
  created() {
    if (this.isEdit) this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      try {
        const res = await getProductDetail(this.$route.params.id)
        const product = res.data || {}
        this.form = {
          name: product.name || '',
          price: product.price != null ? String(product.price) : '',
          image: product.image || '',
          totalStock: product.totalStock != null ? String(product.totalStock) : '',
          startTime: product.startTime || '',
          endTime: product.endTime || ''
        }
      } catch {
        this.$message.error('商品详情加载失败')
      }
    },
    onSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        try {
          const data = {
            ...this.form,
            price: Number(this.form.price),
            totalStock: Number(this.form.totalStock)
          }
          if (this.isEdit) {
            await updateProduct(this.$route.params.id, data)
            this.$message.success('修改成功')
          } else {
            await publishProduct(data)
            this.$message.success('发布成功')
          }
          this.$router.push('/products')
        } catch {
          this.$message.error('操作失败')
        } finally {
          this.loading = false
        }
      })
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
