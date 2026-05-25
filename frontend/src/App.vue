<template>
  <div class="container">
    <!-- 登录页面 -->
    <div v-if="!loggedIn" class="login-container">
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
      
      <!-- 验证码区域 -->
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
      
      <el-button type="primary" @click="handleLogin" style="width: 100%">
        登录
      </el-button>
      
      <el-button @click="showRegister = true" style="width: 100%; margin-top: 10px">
        注册
      </el-button>
    </div>
    
    <!-- 注册弹窗 -->
    <el-dialog v-model="showRegister" title="注册" width="350px">
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
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
    
    <!-- 聊天界面 -->
    <div v-show="loggedIn" class="chat-container">
      <div class="chat-header">
        <span>AI 助手 - 欢迎 {{ username }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
      </div>
      
      <div class="message-list" ref="messageList">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']"
        >
          <div class="message-avatar">
            {{ msg.role === 'user' ? '👤' : '🤖' }}
          </div>
          <div class="message-content">
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createdAt) }}</div>
          </div>
        </div>
      </div>
      
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入消息... (Shift+Enter 换行，Enter 发送)"
          @keydown.enter.prevent="handleKeyDown"
        />
        <el-button type="primary" @click="sendMessage" :loading="sending">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 状态
const loggedIn = ref(false)
const username = ref('')
const token = ref('')
const messages = ref([])
const inputMessage = ref('')
const sending = ref(false)
const showRegister = ref(false)
const messageList = ref(null)

// 登录表单
const loginForm = ref({
  username: '',
  password: '',
  captchaId: '',
  captchaAnswer: ''
})

// 注册表单
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

// 验证码
const captchaImage = ref('')

// API 基础地址
//const API_BASE = 'http://localhost:8080'
const API_BASE = 'https://localhost:8443'
// 配置 axios
axios.defaults.baseURL = API_BASE

// 加载验证码
const loadCaptcha = async () => {
  try {
    const res = await axios.get('/api/captcha/get')
    loginForm.value.captchaId = res.data.captchaId
    captchaImage.value = res.data.image
  } catch (error) {
    ElMessage.error('获取验证码失败')
  }
}

// 登录
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
    const res = await axios.post('/api/auth/login', {
      username: loginForm.value.username,
      password: loginForm.value.password,
      captchaId: loginForm.value.captchaId,
      captchaAnswer: loginForm.value.captchaAnswer
    })
    
    token.value = res.data.token
    username.value = res.data.username
    localStorage.setItem('token', token.value)
    localStorage.setItem('username', username.value)
    
    // 设置 axios 默认 header
    axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
    
    loggedIn.value = true
    await loadHistory()
    ElMessage.success('登录成功')
  } catch (error) {
    ElMessage.error(error.response?.data || '登录失败')
    loadCaptcha()
  }
}

// 注册
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
    await axios.post('/api/auth/register', {
      username: registerForm.value.username,
      password: registerForm.value.password
    })
    ElMessage.success('注册成功，请登录')
    showRegister.value = false
    loginForm.value.username = registerForm.value.username
    loginForm.value.password = ''
  } catch (error) {
    ElMessage.error(error.response?.data || '注册失败')
  }
}

// 加载历史记录
const loadHistory = async () => {
  try {
    const res = await axios.get('/api/chat/history')
    messages.value = res.data
    scrollToBottom()
  } catch (error) {
    console.error('加载历史记录失败', error)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入消息')
    return
  }
  
  sending.value = true
  const userMessage = inputMessage.value.trim()
  
  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: userMessage,
    createdAt: new Date().toISOString()
  })
  scrollToBottom()
  
  inputMessage.value = ''
  
  try {
    const res = await axios.post('/api/chat/send', {
      message: userMessage
    })
    
    messages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: res.data.reply,
      createdAt: new Date().toISOString()
    })
    scrollToBottom()
  } catch (error) {
    ElMessage.error('发送失败：' + (error.response?.data || error.message))
  } finally {
    sending.value = false
  }
}

// 处理键盘事件
const handleKeyDown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  delete axios.defaults.headers.common['Authorization']
  loggedIn.value = false
  messages.value = []
  loginForm.value = { username: '', password: '', captchaId: '', captchaAnswer: '' }
  loadCaptcha()
  ElMessage.info('已退出登录')
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messageList.value) {
    messageList.value.scrollTop = messageList.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  const savedToken = localStorage.getItem('token')
  const savedUsername = localStorage.getItem('username')
  
  if (savedToken && savedUsername) {
    token.value = savedToken
    username.value = savedUsername
    axios.defaults.headers.common['Authorization'] = `Bearer ${savedToken}`
    loggedIn.value = true
    loadHistory()
  } else {
    loadCaptcha()
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  width: 100vw;
  height: 100vh;
  background-image: url('https://news.hbut.edu.cn/virtual_attach_file.vsb?afc=xo7MJRMmVVUzMkM0mviUNQfnNMfUmrafL87iU8nVU4vYolU0gihFp2hmCIa0LShfLYh7L1y4oln2nmWkn7Can7VRL8LDMmM2UmLYM7rVn7nFLRUDU8LbL7QFMNU8LlMfgtXaQ4Oeo4xZ6s6ZgDTJQty0LzG4M1yPoz7ZLSbw62P8c&oid=2023193082&tid=1036&nid=19181&e=.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 验证码 */
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
/* 半透明遮罩 */
.container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  z-index: 0;
}

.login-container {
  position: relative;
  z-index: 1;
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

.captcha-expression {
  background: #f0f0f0;
  padding: 8px 15px;
  border-radius: 5px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  user-select: none;
}

.captcha-expression:hover {
  background: #e0e0e0;
}

.chat-container {
  position: relative;
  z-index: 1;
  width: 900px;
  height: 650px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.chat-header {
  padding: 15px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: white;
}

.chat-header .el-button--danger {
  background: rgba(255, 255, 255, 0.2);
  border: none;
}

.chat-header .el-button--danger:hover {
  background: rgba(255, 255, 255, 0.3);
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8f9fa;
}

.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: #e9ecef;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.message {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
  animation: fadeIn 0.3s ease;
  /* AI消息向左偏移 */
  margin-left: -15px;
}

.ai-message {
  justify-content: flex-start;
}

.user-message {
  flex-direction: row-reverse;
  /* 用户消息向右偏移 */
  margin-right: -15px;
  margin-left: 0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e0e0e0, #ccc);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* AI消息头像上移 */
.ai-message .message-avatar {
  position: relative;
  top: -5px;
}

.user-message .message-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
  /* 用户消息头像下移 */
  position: relative;
  top: 5px;
}

.message-content {
  max-width: 65%;
  transition: transform 0.2s ease;
}

.message-content:hover {
  transform: translateX(5px);
}

.user-message .message-content {
  text-align: right;
  transition: transform 0.2s ease;
}

.user-message .message-content:hover {
  transform: translateX(-5px);
}

.message-text {
  padding: 12px 18px;
  border-radius: 18px;
  background: white;
  word-wrap: break-word;
  white-space: pre-wrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  line-height: 1.5;
  position: relative;
}

/* AI消息气泡尾巴（左侧） */
.ai-message .message-text::before {
  content: '';
  position: absolute;
  left: -8px;
  top: 12px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 8px 8px 8px 0;
  border-color: transparent white transparent transparent;
}

/* 用户消息气泡尾巴（右侧） */
.user-message .message-text::before {
  content: '';
  position: absolute;
  right: -8px;
  top: 12px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 8px 0 8px 8px;
  border-color: transparent transparent transparent #667eea;
}

.user-message .message-text {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.input-area {
  padding: 15px;
  border-top: 1px solid #ddd;
  display: flex;
  gap: 10px;
  background: white;
}

.input-area .el-textarea {
  flex: 1;
}

.input-area .el-button {
  align-self: flex-end;
  height: 60px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  font-weight: 500;
}

.input-area .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}
</style>