<template>
  <div :class="['message', isUser ? 'user-message' : 'ai-message']">
    <div class="message-avatar">
      {{ isUser ? '👤' : '🤖' }}
    </div>
    <div class="message-content">
      <div class="message-text">{{ message.content }}</div>
      <div class="message-time">{{ formatTime }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const isUser = computed(() => props.message.role === 'user')

const formatTime = computed(() => {
  if (!props.message.createdAt) return ''
  const date = new Date(props.message.createdAt)
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
})
</script>

<style scoped>
.message {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
  animation: fadeIn 0.3s ease;
  margin-left: -15px;
}

.ai-message {
  justify-content: flex-start;
}

.user-message {
  flex-direction: row-reverse;
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

.ai-message .message-avatar {
  position: relative;
  top: -5px;
}

.user-message .message-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
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
</style>