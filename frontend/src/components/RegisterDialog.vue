<template>
  <el-dialog v-model="visible" title="注册" width="350px" @close="handleClose">
    <el-input
      v-model="registerForm.username"
      placeholder="用户名"
      style="margin-bottom: 15px"
    />
    <el-input
      v-model="registerForm.password"
      type="password"
      placeholder="密码（至少6位）"
      style="margin-bottom: 15px"
    />
    <el-input
      v-model="registerForm.confirmPassword"
      type="password"
      placeholder="确认密码"
    />
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleRegister">注册</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/request'

const emit = defineEmits(['register-success'])

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

const visible = computed({
  get: () => true,
  set: (val) => {
    if (!val) handleClose()
  }
})

const handleClose = () => {
  registerForm.value = { username: '', password: '', confirmPassword: '' }
  emit('register-success', null)
}

const handleRegister = async () => {
  if (!registerForm.value.username || !registerForm.value.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  if (registerForm.value.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  try {
    await api.register({
      username: registerForm.value.username,
      password: registerForm.value.password
    })
    ElMessage.success('注册成功，请登录')
    emit('register-success', registerForm.value.username)
    handleClose()
  } catch (error) {
    ElMessage.error(error.response?.data || '注册失败')
  }
}

defineExpose({ visible })
</script>