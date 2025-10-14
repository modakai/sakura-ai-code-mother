<template>
  <a-modal
    :open="visible"
    :title="isEdit ? '修改用户' : '添加用户'"
    :confirm-loading="submitting"
    @ok="onOk"
    @cancel="onCancel"
    destroy-on-close
  >
    <a-form
      :model="formModel"
      :rules="formRules"
      ref="formRef"
      :label-col="{ span: 5 }"
      :wrapper-col="{ span: 19 }"
    >
      <a-form-item v-if="!isEdit" label="账号" name="userAccount">
        <a-input v-model:value="formModel.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item label="用户名" name="userName">
        <a-input v-model:value="formModel.userName" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item label="头像" name="userAvatar">
        <a-input v-model:value="formModel.userAvatar" placeholder="请输入头像URL" />
      </a-form-item>
      <a-form-item label="简介" name="userProfile">
        <a-input v-model:value="formModel.userProfile" placeholder="请输入简介" />
      </a-form-item>
      <a-form-item label="角色" name="userRole">
        <a-select v-model:value="formModel.userRole" :options="roleOptions" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance } from 'ant-design-vue'
import { formatFormData, type FormData } from '@/views/admin/user/model/userFormModel'

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  initialData?: Partial<API.UserVO> | null
}>()

const emit = defineEmits<{
  (e: 'cancel'): void
  (e: 'submit', payload: API.UserCreateRequest | API.UserUpdateRequest): void
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)

const roleOptions = [
  { value: 'user', label: '普通用户' },
  { value: 'admin', label: '管理员' },
]

const formModel = reactive<FormData>({
  id: undefined,
  userAccount: '',
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: 'user',
})

const formRules = {
  userAccount: [{ required: !props.isEdit, message: '请输入账号' }],
  userName: [{ required: true, message: '请输入用户名' }],
  userRole: [{ required: true, message: '请选择角色' }],
}

// 根据初始数据填充
watch(
  () => props.initialData,
  (val) => {
    if (props.isEdit && val) {
      formModel.id = val.id as number
      formModel.userAccount = (val.userAccount as string) || ''
      formModel.userName = (val.userName as string) || ''
      formModel.userAvatar = (val.userAvatar as string) || ''
      formModel.userProfile = (val.userProfile as string) || ''
      formModel.userRole = (val.userRole as string) || 'user'
    } else if (!props.isEdit) {
      formModel.id = undefined
      formModel.userAccount = ''
      formModel.userName = ''
      formModel.userAvatar = ''
      formModel.userProfile = ''
      formModel.userRole = 'user'
    }
  },
  { immediate: true },
)

const onCancel = () => emit('cancel')

const onOk = async () => {
  submitting.value = true
  try {
    await formRef.value?.validate()
    const payload = formatFormData(formModel, props.isEdit)
    emit('submit', payload)
  } finally {
    submitting.value = false
  }
}
</script>
