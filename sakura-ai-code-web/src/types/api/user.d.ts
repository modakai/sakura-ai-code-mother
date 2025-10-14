declare namespace API {
  /** 创建用户请求参数 */
  interface UserCreateRequest {
    userName?: string;
    userAccount?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string; // user, admin
  }

  /** 创建用户返回 data（用户ID） */
  type UserCreateResponse = number;

  /** 根据 id 获取用户请求（仅管理员） */
  interface UserGetRequest {
    id?: number;
  }

  /** 用户实体类 */
  interface User {
    id: number;
    userAccount: string;
    userPassword: string;
    unionId?: string;
    mpOpenId?: string;
    userName: string;
    userAvatar: string;
    userProfile: string;
    userRole: string; // user/admin/ban
    createTime: string;
    updateTime: string;
    isDelete?: number;
  }

  /** 根据 id 获取用户VO 请求 */
  interface UserVOGetRequest {
    id?: number;
  }

  /** 用户VO */
  interface UserVO {
    id: number;
    userAccount: string;
    userName: string;
    userAvatar: string;
    userProfile: string;
    userRole: string; // user/admin
    createTime: string;
  }

  /** 更新用户请求 */
  interface UserUpdateRequest {
    id: number;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string; // user/admin
  }

  /** 分页查询用户VO请求参数 */
  interface UserQueryRequest extends PageRequest {
    id?: number;
    userName?: string;
    userAccount?: string;
    userProfile?: string;
    userRole?: string; // user/admin/ban
    sortField?: string;
    sortOrder?: string; // 默认升序
  }

  /** 分页查询用户VO响应 */
  type UserPageVOResponse = PageResponse<UserVO[]>;
}