<template>
  <div class="container">
    <!-- 登录页面 -->
    <Login 
      v-if="!loggedIn" 
      @login-success="handleLoginSuccess"
      @show-register="showRegisterDialog = true"
    />
    
    <!-- 注册弹窗 -->
    <RegisterDialog 
      v-if="showRegisterDialog"
      @register-success="handleRegisterSuccess"
    />
    
    <!-- 聊天界面 -->
    <div v-show="loggedIn" class="chat-container">
      <ChatHeader 
        :username="username"
        @logout="handleLogout"
      />
      
      <MessageList :messages="messages" />
      
      <MessageInput 
        :sending="sending"
        @send="sendMessage"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from './api/request'
import Login from './components/Login.vue'
import RegisterDialog from './components/RegisterDialog.vue'
import ChatHeader from './components/ChatHeader.vue'
import MessageList from './components/MessageList.vue'
import MessageInput from './components/MessageInput.vue'

// 状态
const loggedIn = ref(false)
const username = ref('')
const messages = ref([])
const sending = ref(false)
const showRegisterDialog = ref(false)

// 登录成功
const handleLoginSuccess = ({ token, username: name }) => {
  loggedIn.value = true
  username.value = name
  loadHistory()
}

// 注册成功
const handleRegisterSuccess = (registeredUsername) => {
  showRegisterDialog.value = false
  if (registeredUsername) {
    // 可以自动填充用户名，但需要用户手动输入密码
    ElMessage.info(`用户 ${registeredUsername} 注册成功，请登录`)
  }
}

// 加载历史记录
const loadHistory = async () => {
  try {
    const res = await api.getHistory()
    messages.value = res.data
  } catch (error) {
    console.error('加载历史记录失败', error)
  }
}

// 发送消息
const sendMessage = async (userMessage) => {
  sending.value = true
  
  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: userMessage,
    createdAt: new Date().toISOString()
  })
  
  try {
    const res = await api.sendMessage(userMessage)
    
    messages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: res.data.reply,
      createdAt: new Date().toISOString()
    })
  } catch (error) {
    if (error.response?.status === 409) {
      ElMessage.error('检测到重复请求，请稍后再试')
    } else {
      ElMessage.error('发送失败：' + (error.response?.data || error.message))
    }
  } finally {
    sending.value = false
  }
}

// 退出登录
const handleLogout = () => {
  loggedIn.value = false
  messages.value = []
  ElMessage.info('已退出登录')
}

// 初始化
onMounted(() => {
  const savedToken = localStorage.getItem('token')
  const savedUsername = localStorage.getItem('username')
  
  if (savedToken && savedUsername) {
    loggedIn.value = true
    username.value = savedUsername
    loadHistory()
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
</style>