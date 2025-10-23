declare namespace API {

  interface ChatHistory {
    id:number
    appId: number
    chatMessage: string
    messageType: string
    userId: number
    createTime: string
    updateTime: string
  }

  interface ChatHistoryVO {
    id:number
    appId: number
    chatMessage: string
    messageType: string
    userId: number
    user: UserVO
    createTime: string
  }

  interface ChatHistoryQueryRequest extends PageRequest{
    id?:number
    appId?:number
    userId?:number
    messageType?:string
    lastCreateTime?: string
  }

  interface CursorChatHistoryRequest {
    appId: number;
    pageSize: number;
    lastCreateTime?: string;
  }
}
