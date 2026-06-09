import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const nickName = ref(localStorage.getItem('nickName') || '')
  const phone = ref(localStorage.getItem('phone') || '')
  const roleId = ref(Number(localStorage.getItem('roleId') || 0))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => roleId.value === 1)

  function setUser(userData) {
    token.value = userData.token
    userId.value = userData.id
    nickName.value = userData.nickName
    phone.value = userData.phone
    roleId.value = userData.roleId
    localStorage.setItem('token', userData.token)
    localStorage.setItem('userId', userData.id)
    localStorage.setItem('nickName', userData.nickName)
    localStorage.setItem('phone', userData.phone)
    localStorage.setItem('roleId', userData.roleId)
  }

  function logout() {
    token.value = ''
    userId.value = ''
    nickName.value = ''
    phone.value = ''
    roleId.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('nickName')
    localStorage.removeItem('phone')
    localStorage.removeItem('roleId')
  }

  return { token, userId, nickName, phone, roleId, isLoggedIn, isAdmin, setUser, logout }
})
