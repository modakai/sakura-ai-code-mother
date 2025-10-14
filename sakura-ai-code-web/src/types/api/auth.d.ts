declare namespace API {
  interface LoginRequest {
    userAccount: string;
    userPassword: string;
  }

  interface RegisterRequest {
    userAccount: string;
    userPassword: string;
    checkPassword: string;
  }

  interface LoginUser {
    id: number;
    userAccount: string;
    userName: string;
    userPassword: string;
    userAvatar: string;
    userProfile: string;
    userRole: string;
    createTime?: string;
    updateTime?: string;
  }
}
