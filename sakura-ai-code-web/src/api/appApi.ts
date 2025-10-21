import { useGet, usePost } from '@/utils/request.ts'

const PREFIX = '/app'

/**
 * 部署应用
 * @param data
 */
export function deployAppApi(data: API.AppDeployRequest) {
  return usePost<string, API.AppDeployRequest>(PREFIX + '/deploy', data)
}

/**
 * 创建应用接口：创建一个新应用
 * @param data - 创建应用请求参数
 */
export function addAppApi(data: API.AppAddRequest) {
  return usePost<number, API.AppAddRequest>(PREFIX + '/add', data)
}

/**
 * 根据 id 获取应用（仅管理员）
 * @param id - 查询参数
 */
export function getAppApi(id: number) {
  return useGet<API.AppEntity>(PREFIX + `/get?id=${id}`)
}

/**
 * 根据 id 获取应用封装类（AppVO）
 * @param id - 查询参数
 */
export function getAppVoApi(id: number) {
  return useGet<API.AppVO>(PREFIX + `/get/vo?id=${id}`)
}

/**
 * 删除应用
 * @param data - 删除请求参数
 */
export function deleteAppApi(data: API.DeleteRequest) {
  return usePost<boolean, API.DeleteRequest>(PREFIX + '/delete', data)
}

/**
 * 更新应用
 * @param data - 更新请求参数
 */
export function updateAppApi(data: API.AppUpdateRequest) {
  return usePost<boolean, API.AppUpdateRequest>(PREFIX + '/update', data)
}

/**
 * 更新应用（my）
 * @param data - 更新请求参数
 */
export function updateAppMyApi(data: API.AppUpdateRequest) {
  return usePost<boolean, API.AppUpdateRequest>(PREFIX + '/update/my', data)
}

/**
 * 分页获取应用封装列表（仅管理员）
 * @param data - 查询请求参数
 */
export function listAppPageVoApi(data: API.AppQueryRequest) {
  return usePost<API.PageResponse<API.AppVO[]>, API.AppQueryRequest>(PREFIX + '/list/page/vo', data)
}

/**
 * 分页获取自己应用封装列表
 * @param data - 查询请求参数
 */
export function listAppPageVoMyApi(data: API.AppQueryRequest) {
  return usePost<API.PageResponse<API.AppVO[]>, API.AppQueryRequest>(PREFIX + '/list/page/my', data)
}

/**
 * 分页获取精选应用封装列表
 * @param data - 查询请求参数
 */
export function listAppPageVoFeaturedApi(data: API.AppQueryRequest) {
  return usePost<API.PageResponse<API.AppVO[]>, API.AppQueryRequest>(
    PREFIX + '/list/page/featured',
    data,
  )
}
