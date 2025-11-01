import { useLoginUserStore } from '@/stores/loginUser'
import { message } from 'ant-design-vue'
import router from '@/router'
import { getToken } from '@/utils/tokenUtil.ts'

// 是否为首次获取登录用户
let firstFetchLoginUser = true

/**
 * 全局权限校验
 */
router.beforeEach(async (to, from, next) => {
  const toUrl = to.fullPath
  if (toUrl.startsWith('/login') || toUrl.startsWith('/register')) {
    next()
    return
  }
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  // 确保页面刷新，首次加载时，能够等后端返回用户信息后再校验权限
  const token = getToken()
  if (firstFetchLoginUser && token) {
    try {
      await loginUserStore.fetchLoginUser()
      loginUser = loginUserStore.loginUser
    } finally {
      firstFetchLoginUser = false
    }
  }

  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('没有权限')
      next(`/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
