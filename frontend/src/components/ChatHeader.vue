<template>
  <div class="chat-header">
    <span>雨打梨花深闭门 - welcome, {{ username }}</span>
    <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
  </div>
</template>

<script setup>
const props = defineProps({
  username: String
})

const emit = defineEmits(['logout'])

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  emit('logout')
}
</script>

<style scoped>
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
</style>