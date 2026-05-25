<template>
  <div class="login-container">
    <h2>编码现在，计算未来！</h2>
    
    <el-input
      v-model="loginForm.username"
      placeholder="用户名"
      style="margin-bottom: 15px"
    />
    
    <el-input
      v-model="loginForm.password"
      type="password"
      placeholder="密码"
      style="margin-bottom: 15px"
    />
    
    <div class="captcha-row">
      <el-input
        v-model="loginForm.captchaAnswer"
        placeholder="验证码"
        style="width: 120px"
        @keyup.enter="handleLogin"
      />
      <div class="captcha-image" @click="loadCaptcha">
        <img :src="captchaImage" alt="验证码" v-if="captchaImage" />
        <span v-else>点击加载验证码</span>
      </div>
    </div>
    
    <el-button type="primary" @click="handleLogin" class="login-btn">
      登录
    </el-button>
    
    <el-button @click="showRegister" class="register-btn">
      注册
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/request'

const emit = defineEmits(['login-success', 'show-register'])

const loginForm = ref({
  username: '',
  password: '',
  captchaId: '',
  captchaAnswer: ''
})

const captchaImage = ref('')

const loadCaptcha = async () => {
  try {
    const res = await api.getCaptcha()
    loginForm.value.captchaId = res.data.captchaId
    captchaImage.value = res.data.image
  } catch (error) {
    ElMessage.error('获取验证码失败')
  }
}

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  if (!loginForm.value.captchaAnswer) {
    ElMessage.warning('请填写验证码')
    return
  }
  
  try {
    const res = await api.login({
      username: loginForm.value.username,
      password: loginForm.value.password,
      captchaId: loginForm.value.captchaId,
      captchaAnswer: loginForm.value.captchaAnswer
    })
    
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    
    emit('login-success', {
      token: res.data.token,
      username: res.data.username
    })
    ElMessage.success('登录成功')
  } catch (error) {
    ElMessage.error(error.response?.data || '登录失败')
    loadCaptcha()
  }
}

const showRegister = () => {
  emit('show-register')
}

// 初始化
loadCaptcha()
</script>

<style scoped>
.login-container {
  width: 400px;
  height: 550px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 100px 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  margin-left: -1200px;
}

.login-container h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.captcha-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 20px;
}

.captcha-image {
  cursor: pointer;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.captcha-image img {
  height: 40px;
  width: auto;
}

.captcha-image span {
  font-size: 12px;
  color: #999;
  padding: 0 10px;
}

.login-btn,
.register-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 0;
  margin-right: 30;
}

.login-btn {
  margin-bottom: 12px;
}

.register-btn {
  margin-top: 0;
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  color: #606266;
}

.register-btn:hover {
  background: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}
</style>