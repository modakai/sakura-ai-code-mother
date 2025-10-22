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

  interface CursorChatHistoryRequest {
    appId: number;
    pageSize: number;
    lastCreateTime?: string;
  }
}
