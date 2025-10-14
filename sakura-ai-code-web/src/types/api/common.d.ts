/**
 * 通用API接口类型定义
 */
declare namespace API {
  interface PageRequest {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
  }

  interface PageResponse<T = never> {
    records: T
    pageNumber: number
    pageSize: number
    totalRow: number
  }

  interface DeleteRequest {
    id: number
  }
}
