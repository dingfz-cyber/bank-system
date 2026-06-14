import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const nickName = ref(localStorage.getItem('nickName') || '')
  const phone = ref(localStorage.getItem('phone') || '')
  const roleId = ref(Number(localStorage.getItem('roleId') || 0))
  const points = ref(Number(localStorage.getItem('points') || 0))

  const isLoggedIn = computed(() => !!token.value)
  // 5 层角色：1=运维 2=审计 3=业务 4=运营 5=用户
  const isSysAdmin = computed(() => roleId.value === 1)
  const isAuditor = computed(() => roleId.value === 2)
  const isBizAdmin = computed(() => roleId.value === 3)
  const isOperator = computed(() => roleId.value === 4)
  // 可访问后台的角色（运维/审计/业务/运营）
  const isAdmin = computed(() => [1, 2, 3, 4].includes(roleId.value))

  function setUser(userData) {
    token.value = userData.token
    userId.value = userData.id
    nickName.value = userData.nickName
    phone.value = userData.phone
    roleId.value = userData.roleId
    points.value = userData.points || 0
    localStorage.setItem('token', userData.token)
    localStorage.setItem('userId', userData.id)
    localStorage.setItem('nickName', userData.nickName)
    localStorage.setItem('phone', userData.phone)
    localStorage.setItem('roleId', userData.roleId)
    localStorage.setItem('points', userData.points || 0)
  }

  function updateNickName(name) {
    nickName.value = name
    localStorage.setItem('nickName', name)
  }

  function logout() {
    token.value = ''
    userId.value = ''
    nickName.value = ''
    phone.value = ''
    roleId.value = 0
    points.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('nickName')
    localStorage.removeItem('phone')
    localStorage.removeItem('roleId')
    localStorage.removeItem('points')
  }

  return { token, userId, nickName, phone, roleId, points, isLoggedIn, isAdmin, isSysAdmin, isAuditor, isBizAdmin, isOperator, setUser, updateNickName, logout }
})
