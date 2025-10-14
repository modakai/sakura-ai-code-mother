import { useGet, usePost } from '@/utils/request.ts'

/**
 * 创建用户接口：创建一个新用户
 * @param data - 创建用户请求参数
 */
export function addApi(data: API.UserCreateRequest) {
  return usePost<API.UserCreateResponse, API.UserCreateRequest>('/user/add', data)
}

/**
 * 根据 id 获取用户（仅管理员）
 * @param params - 查询参数
 */
export function getApi(params: API.UserGetRequest) {
  const query = new URLSearchParams()
  if (params.id !== undefined) query.set('id', String(params.id))
  return useGet<API.User>(`/user/get?${query.toString()}`)
}

/**
 * 根据 id 获取用户封装类（UserVO）
 * @param params - 查询参数
 */
export function getVoApi(params: API.UserVOGetRequest) {
  const query = new URLSearchParams()
  if (params.id !== undefined) query.set('id', String(params.id))
  return useGet<API.UserVO>(`/user/get/vo?${query.toString()}`)
}

/**
 * 删除用户
 * @param data - 删除请求参数
 */
export function deleteApi(data: API.DeleteRequest) {
  return usePost<boolean, API.DeleteRequest>('/user/delete', data)
}

/**
 * 更新用户
 * @param data - 更新请求参数
 */
export function updateApi(data: API.UserUpdateRequest) {
  return usePost<boolean, API.UserUpdateRequest>('/user/update', data)
}

/**
 * 分页获取用户封装列表（仅管理员）
 * @param data - 查询请求参数
 */
export function listPageVoApi(data: API.UserQueryRequest) {
  return usePost<API.UserPageVOResponse, API.UserQueryRequest>('/user/list/page/vo', data)
}
