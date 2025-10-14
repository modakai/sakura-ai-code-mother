// Global augmentation for vue-router's RouteMeta
// Ensures route meta fields are strongly typed across the app
import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    // 菜单标题（用于导航显示）
    title: string
    // 是否显示在导航菜单
    nav?: boolean
  }
}

export {}
