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
        <div class="user-login-status">
          <a-button type="primary">登录</a-button>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter, type RouteRecordRaw } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'
import routes from '@/router/routes'

const router = useRouter()
const route = useRoute()

// 当前选中菜单 keys
const selectedKeys = ref<string[]>([route.path])
watch(
  () => route.path,
  (p) => {
    selectedKeys.value = [p]
  },
  { immediate: true },
)

// 根据路由配置自动生成导航菜单（使用强类型 RouteMeta）
interface MenuItemOption {
  key: string
  label: string
  title: string
}

const menuItems = computed<MenuItemOption[]>(() => {
  const items: MenuItemOption[] = (routes as RouteRecordRaw[])
    .filter((r) => r.meta?.nav)
    .map((r) => ({
      key: r.path as string,
      label: r.meta!.title,
      title: r.meta!.title,
    }))
  return items
})

const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = String(e.key)
  selectedKeys.value = [key]
  if (key.startsWith('/')) {
    router.push(key)
  }
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
