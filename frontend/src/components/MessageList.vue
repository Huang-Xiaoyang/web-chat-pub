<template>
  <div class="message-list" ref="messageListRef">
    <MessageItem
      v-for="msg in messages"
      :key="msg.id"
      :message="msg"
    />
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import MessageItem from './MessageItem.vue'

const props = defineProps({
  messages: {
    type: Array,
    required: true
  }
})

const messageListRef = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

// 监听消息变化，自动滚动到底部
watch(() => props.messages.length, () => {
  scrollToBottom()
}, { deep: true })

defineExpose({ scrollToBottom })
</script>

<style scoped>
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
</style>