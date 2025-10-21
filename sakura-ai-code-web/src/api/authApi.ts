import { useGet, usePost } from '@/utils/request.ts'

/**
 * 登录请求
 * @param data
 */
export function loginApi(data: API.LoginRequest) {
  return usePost<API.LoginUser, API.LoginRequest>("/auth/login", data)
}

/**
 * 注册请求
 * @param data
 */
export function registerApi(data: API.RegisterRequest) {
  return usePost<API.LoginUser, API.LoginRequest>("/auth/register", data)
}

/**
 * 获取登录信息请求
 */
export function getLoginInfoApi() {
  return useGet<API.LoginUser>("/auth/get/login")
}

/**
 * 退出登录
 */
export function logoutApi() {
  return usePost<API.LoginUser>("/auth/logout")
}


