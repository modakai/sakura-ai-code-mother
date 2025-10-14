import type { RouteRecordRaw } from 'vue-router'

// 应用的路由配置，使用强类型的 RouteMeta
export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', nav: true },
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue'),
    meta: { title: '关于', nav: true },
  },
]

export default routes
