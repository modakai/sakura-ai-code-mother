import { ref } from 'vue'
import { addUserApi, updateUserApi } from '@/api/userApi.ts'
import { message } from 'ant-design-vue'

export interface FormData {
  id?: number
  userAccount: string
  userName: string
  userAvatar?: string
  userProfile?: string
  userRole: string // 'user' | 'admin'
}

export const loading = ref(false)

export function formatFormData(
  form: FormData,
  isEdit: boolean,
): API.UserCreateRequest | API.UserUpdateRequest {
  if (isEdit) {
    return {
      id: Number(form.id),
      userName: form.userName,
      userAvatar: form.userAvatar,
      userProfile: form.userProfile,
      userRole: form.userRole,
    }
  }
  return {
    userAccount: form.userAccount,
    userName: form.userName,
    userAvatar: form.userAvatar,
    userProfile: form.userProfile,
    userRole: form.userRole,
  }
}

export async function submitForm(
  isEdit: boolean,
  payload: API.UserCreateRequest | API.UserUpdateRequest,
): Promise<boolean> {
  loading.value = true
  try {
    if (isEdit) {
      const res = await updateUserApi(payload as API.UserUpdateRequest)
      const ok = res.code === 0 || (res.data as boolean)
      if (ok) message.success('修改成功')
      else message.error('修改失败')
      return ok
    } else {
      const res = await addUserApi(payload as API.UserCreateRequest)
      const ok = res.code === 0
      if (ok) message.success('添加成功')
      else message.error('添加失败')
      return ok
    }
  } catch (e: any) {
    return false
  } finally {
    loading.value = false
  }
}
