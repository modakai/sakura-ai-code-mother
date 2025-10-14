import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginInfoApi } from '@/api/authApi.ts'

export const useLoginUserStore = defineStore('loginUser', () => {
  // 默认值
  const loginUser = ref<API.LoginUser>({
    id: NaN,
    userAccount: '',
    userAvatar: '',
    userPassword: '',
    userProfile: '',
    userRole: '',
    userName: '未登录',
  })

  // 获取登录用户信息
  async function fetchLoginUser() {
    const res = await getLoginInfoApi()
    if (res.code === 0 && res.data) {
      loginUser.value = res.data
    }
  }
  // 更新登录用户信息
  function setLoginUser(newLoginUser: API.LoginUser) {
    loginUser.value = newLoginUser
  }

  return { loginUser, setLoginUser, fetchLoginUser }
})
