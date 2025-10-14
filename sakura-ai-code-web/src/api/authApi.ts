import { usePost } from '@/utils/request.ts'

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
