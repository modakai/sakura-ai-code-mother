import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'

import router from '@/router'
import { message, notification } from 'ant-design-vue'
import { getToken, TOKEN_NAME } from '@/utils/tokenUtil.ts'

/**
 * @description: request method
 */
export enum RequestEnum {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  DELETE = 'DELETE',
}

/**
 * @description:  contentType
 */
export enum ContentTypeEnum {
  // json
  JSON = 'application/json',
  // form-data qs
  FORM_URLENCODED = 'application/x-www-form-urlencoded;charset=UTF-8',
  // form-data  upload
  FORM_DATA = 'multipart/form-data;charset=UTF-8',
}

export interface ApiResponse<T = never> {
  code: number
  data?: T
  message: string
}

export interface RequestConfigExtra {
  token?: boolean
  customDev?: boolean
  loading?: boolean
}

const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 6000000,
  headers: { 'Content-Type': ContentTypeEnum.JSON },
  withCredentials: true,
})
// const axiosLoading = new AxiosLoading()

async function requestHandler(
  config: InternalAxiosRequestConfig & RequestConfigExtra,
): Promise<InternalAxiosRequestConfig> {
  // const token = useAuthorization()
  const token = getToken()
  if (token && config.token !== false) config.headers.set(TOKEN_NAME, token)

  // if (config.loading) axiosLoading.addLoading()
  return config
}
function responseHandler(
  response: any,
): ApiResponse<any> | AxiosResponse<any> | Promise<any> | any {
  const { code } = response.data
  if (code === 401) {
    // const token = useAuthorization()

    // 获取当前页面的路径部分，不包含域名和端口
    const currentPath = window.location.pathname + window.location.search + window.location.hash

    // 检查当前是否已经在登录页，避免重复重定向
    if (currentPath.startsWith('/login')) {
      return
    }

    router.replace(`/login?redirect=${encodeURIComponent(currentPath)}`)
    // 抛出错误以中断后续逻辑
    return
  }

  if (code !== 0) {
    message.error(response.data.message || '未知错误')
    throw new Error(response.data.message)
  }
  return response.data
}

function errorHandler(error: AxiosError): Promise<any> {
  // const token = useAuthorization()
  // const notification = useNotification()

  if (error.response) {
    const { data, status, statusText } = error.response as AxiosResponse<ApiResponse>
    if (status === 401) {
      notification.error({
        message: data?.message || statusText,
        duration: 3,
      })
      /**
       * 这里处理清空用户信息和token的逻辑，后续扩展
       */
      // token.value = null

      // 获取当前页面的路径，不包含域名和端口
      const currentPath = window.location.pathname + window.location.search + window.location.hash

      // 检查当前是否已经在登录页，避免重复重定向
      if (currentPath.startsWith('/login')) {
        return Promise.reject(error)
      }

      router
        .push({
          path: '/login',
          query: {
            redirect: currentPath,
          },
        })
        .then(() => {})
    } else {
      notification.error({
        message: data?.message || statusText,
        duration: 3,
      })
    }
    if (data.code != 0) {
      message.error(data.message || statusText)
    }
  }
  return Promise.reject(error)
}

interface AxiosOptions<T> {
  url: string
  params?: T
  data?: T
}

instance.interceptors.request.use(requestHandler)

instance.interceptors.response.use(responseHandler, errorHandler)

export default instance

function instancePromise<R = any, T = any>(
  options: AxiosOptions<T> & RequestConfigExtra,
): Promise<ApiResponse<R>> {
  // const { loading } = options
  return new Promise((resolve, reject) => {
    instance
      .request(options)
      .then((res) => {
        resolve(res as any)
      })
      .catch((e: Error | AxiosError) => {
        reject(e)
      })
      .finally(() => {
        // if (loading) axiosLoading.closeLoading()
      })
  })
}

export function useGet<R = any, T = any>(
  url: string,
  params?: T,
  config?: AxiosRequestConfig & RequestConfigExtra,
): Promise<ApiResponse<R>> {
  const options = {
    url,
    params,
    method: RequestEnum.GET,
    ...config,
  }
  return instancePromise<R, T>(options)
}

export function usePost<R = any, T = any>(
  url: string,
  data?: T,
  config?: AxiosRequestConfig & RequestConfigExtra,
): Promise<ApiResponse<R>> {
  const options = {
    url,
    data,
    method: RequestEnum.POST,
    ...config,
  }
  return instancePromise<R, T>(options)
}

export function usePut<R = any, T = any>(
  url: string,
  data?: T,
  config?: AxiosRequestConfig & RequestConfigExtra,
): Promise<ApiResponse<R>> {
  const options = {
    url,
    data,
    method: RequestEnum.PUT,
    ...config,
  }
  return instancePromise<R, T>(options)
}

export function useDelete<R = any, T = any>(
  url: string,
  data?: T,
  config?: AxiosRequestConfig & RequestConfigExtra,
): Promise<ApiResponse<R>> {
  const options = {
    url,
    data,
    method: RequestEnum.DELETE,
    ...config,
  }
  return instancePromise<R, T>(options)
}
