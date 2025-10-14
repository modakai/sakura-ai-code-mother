import { createRouter, createWebHistory, type Router, type RouteRecordRaw } from 'vue-router'
import routes from './routes'

const typedRoutes: RouteRecordRaw[] = routes

const router: Router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: typedRoutes,
})

export default router
