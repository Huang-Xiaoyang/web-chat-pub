import axios from 'axios'
import { ElMessage } from 'element-plus'

// API 基础地址
const API_BASE = 'https://localhost:8443'

// 创建 axios 实例
const request = axios.create({
  baseURL: API_BASE,
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      window.location.href = '/'
      ElMessage.error('登录已过期，请重新登录')
    }
    return Promise.reject(error)
  }
)

// 生成随机 Nonce
export const generateNonce = () => {
  const bytes = new Uint8Array(32)
  crypto.getRandomValues(bytes)
  return btoa(String.fromCharCode(...bytes))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '')
}

// API 方法
export const api = {
  // 验证码
  getCaptcha: () => request.get('/api/captcha/get'),
  
  // 认证
  register: (data) => request.post('/api/auth/register', data),
  login: (data) => request.post('/api/auth/login', data),
  
  // 聊天
  sendMessage: (message) => {
    const nonce = generateNonce()
    return request.post('/api/chat/send', { message }, {
      headers: { 'X-Nonce': nonce }
    })
  },
  getHistory: () => request.get('/api/chat/history')
}

export default request