import { useGet, usePost } from '@/utils/request.ts'

const PREFIX = '/history'

/**
 * 游标查询应用聊天消息
 * @param params
 */
export function cursorAppChatHistoryPage(params: API.CursorChatHistoryRequest) {
  return useGet<API.PageResponse<API.ChatHistory[]>>(PREFIX + '/app/cursor/' + params.appId, params)
}

export function loadAppChatHistoryApi(params: { id: number }) {
  return usePost(PREFIX + '/load/chat/' + params.id)
}
