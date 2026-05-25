<template>
  <div class="input-area">
    <el-input
      v-model="inputMessage"
      type="textarea"
      :rows="3"
      placeholder="输入消息... (Shift+Enter 换行，Enter 发送)"
      @keydown.enter.prevent="handleKeyDown"
    />
    <el-button type="primary" @click="send" :loading="sending">
      发送
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  sending: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send'])

const inputMessage = ref('')

const send = () => {
  if (!inputMessage.value.trim()) {
    return
  }
  emit('send', inputMessage.value.trim())
  inputMessage.value = ''
}

const handleKeyDown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    send()
  }
}
</script>

<style scoped>
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