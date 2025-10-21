declare namespace API {

  interface AppVO {
    id: number
    appName: string
    cover: string
    initPrompt: string
    deployKey?: string
    codeGenType?: string
    priority?: number
    userId: number
    user?: UserVO
    createTime: string
    deployTime: string
    updateTime: string
  }

  interface AppEntity {
    id: number
    appName: string
    cover: string
    initPrompt: string
    codeGenType?: string
    deployKey?: string
    deployTime?: string
    priority?: number
    userId: number
    createTime: string
    updateTime: string
    isDelete?: number
  }

  interface AppAddRequest {
    appName?: string
    cover?: string
    initPrompt: string
    codeGenType?: string
  }

  interface AppUpdateRequest {
    id: number
    appName?: string
    cover?: string
    priority?: number
  }

  interface AppQueryRequest extends PageRequest{
    id?: number
    appName?: string
    userId?: number
    priority?: number
    codeGenType?: string
  }

  interface AppDeployRequest {
    appId: number
  }
}
