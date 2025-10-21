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
    path: '/app/chat/:id',
    name: 'AppChat',
    component: () => import('@/views/app/AppChatPage.vue'),
    meta: { title: '应用对话', nav: false },
  },
  {
    path: '/app/edit/:id',
    name: 'AppEdit',
    component: () => import('@/views/app/AppEditPage.vue'),
    meta: { title: '编辑应用', nav: false },
  },
  {
    path: '/admin',
    name: 'Admin',
    meta: {title: "系统管理", nav: true },
    children: [
      {
        path: '/admin/userManage',
        name: 'UserManage',
        component: () => import('@/views/admin/user/UserManageView.vue'),
        meta: { title: '用户管理', nav: true },
      },
      {
        path: '/admin/appManage',
        name: 'AppManage',
        component: () => import('@/views/admin/app/AppManageView.vue'),
        meta: { title: '应用管理', nav: true },
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', nav: false },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', nav: false },
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue'),
    meta: { title: '关于', nav: true },
  },
]

export default routes
