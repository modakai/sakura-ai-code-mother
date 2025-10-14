<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">Sakura应用生成</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status" v-if="loginUserStore.loginUser.id">
          <a-dropdown>
            <a-space>
              <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              {{ loginUserStore.loginUser.userName ?? '无名' }}
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else class="user-login-status">
          <a-button type="primary" @click="goLogin()">登录</a-button>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { type RouteRecordRaw, useRoute, useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import routes from '@/router/routes'
import { getLoginInfoApi, logoutApi } from '@/api/authApi'

import { LogoutOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'

const router = useRouter()
const route = useRoute()
const loginUserStore = useLoginUserStore()

// 登录与权限状态（基于登录用户状态）
const isLogin = computed(() => Boolean(loginUserStore.loginUser?.id))
const isAdmin = computed(() => loginUserStore.loginUser?.userRole === 'admin')

// 当前选中菜单 keys（包含父子路径，保证父菜单高亮/展开）
const selectedKeys = ref<string[]>([route.path])
watch(
  () => route.matched.map((m) => m.path),
  (matched) => {
    selectedKeys.value = matched
  },
  { immediate: true },
)

// 菜单项定义，支持嵌套 children
interface MenuItemOption {
  key: string
  label: string
  title: string
  children?: MenuItemOption[]
}

// 权限过滤：/admin 开头仅在已登录且为 admin 时显示
function allowRoute(path: string): boolean {
  if (path.startsWith('/admin')) {
    return isLogin.value && isAdmin.value
  }
  return true
}

// 递归构建菜单（过滤 meta.nav === true）
function buildMenu(rs: RouteRecordRaw[]): MenuItemOption[] {
  const list: MenuItemOption[] = []
  rs.forEach((r) => {
    const path = String(r.path)
    const show = r.meta?.nav === true && allowRoute(path)
    const children = Array.isArray(r.children)
      ? buildMenu(r.children as RouteRecordRaw[])
      : undefined

    // 如果父不显示但子有可显示项，可仅展示子（保持层级化）
    if (!show && (!children || children.length === 0)) {
      return
    }

    if (show) {
      const item: MenuItemOption = {
        key: path,
        label: r.meta!.title as string,
        title: r.meta!.title as string,
      }
      if (children && children.length) item.children = children
      list.push(item)
    } else if (children && children.length) {
      // 父不显示但有可显示的子路由：将子提升到同级
      list.push(...children)
    }
  })
  return list
}

const menuItems = computed<MenuItemOption[]>(() => buildMenu(routes as RouteRecordRaw[]))

const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = String(e.key)
  // 点击子路由时，选中包含父和子
  const matched = router.resolve(key).matched.map((m) => m.path)
  selectedKeys.value = matched
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 用户注销
const doLogout = async () => {
  const res = await logoutApi()
  if (res.code === 0) {
    loginUserStore.setLoginUser({
      id: NaN,
      userAccount: '',
      userAvatar: '',
      userPassword: '',
      userProfile: '',
      userRole: '',
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.message)
  }
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.header {
  background: #fff;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 48px;
  width: 48px;
}

.site-title {
  margin: 0;
  font-size: 18px;
  color: #1890ff;
}

.ant-menu-horizontal {
  border-bottom: none !important;
}
</style>
