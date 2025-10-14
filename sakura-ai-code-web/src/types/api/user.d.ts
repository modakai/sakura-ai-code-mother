/**
 * 用户相关接口类型定义
 */

declare namespace API {


  interface TokenInfo {
    tokenName: string;
    tokenValue: string;
    isLogin: boolean;
    loginId?: object
  }

  interface UserVO {
    id?: number;
    username: string;
    nickname: string;
    userProfile: string;
    userAvatar?: string;
    userRole?: string;
    createTime?: string
  }


  /**
   * 搜索用户请求
   */
  interface UserQueryRequest extends API.PageRequest{
    /**
     * 用户ID
     */
    id?: number;

    username?: string;

    nickname?: string;

    userProfile?: string;

    /**
     * 用户角色
     */
    userRole?: string;
  }
}
