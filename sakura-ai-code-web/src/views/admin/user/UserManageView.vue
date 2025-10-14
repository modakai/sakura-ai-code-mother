<script setup lang="ts">
// 字段
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUserApi, listPageVoApi } from '@/api/userApi.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import UserFormModal from './components/UserFormModal.vue'
import { submitForm } from './model/userFormModel'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 数据
const data = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'create_time',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async () => {
  const res = await listPageVoApi({
    ...searchParams,
  })
  if (res.data) {
    data.value = res.data.records ?? []
    total.value = res.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.message)
  }
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: API.UserQueryRequest) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 获取数据
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: number) => {
  if (!id) {
    return
  }
  const res = await deleteUserApi({ id })
  if (res.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

// 模态框状态
const modalVisible = ref(false)
const isEditMode = ref(false)
const currentUser = ref<Partial<API.UserVO> | null>(null)

const handleOpenAdd = () => {
  isEditMode.value = false
  currentUser.value = null
  modalVisible.value = true
}

const handleOpenEdit = (record: API.UserVO) => {
  isEditMode.value = true
  currentUser.value = record
  modalVisible.value = true
}

const handleCancel = () => {
  modalVisible.value = false
  currentUser.value = null
}

const handleSubmit = async (payload: API.UserCreateRequest | API.UserUpdateRequest) => {
  const ok = await submitForm(isEditMode.value, payload)
  if (ok) {
    modalVisible.value = false
    currentUser.value = null
    fetchData()
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<template>
  <div id="userManageView">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />

    <div style="margin-bottom: 12px">
      <a-button type="primary" @click="handleOpenAdd">添加用户</a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button @click="handleOpenEdit(record)">编辑</a-button>
            <a-button danger @click="doDelete(record.id)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <UserFormModal
      :visible="modalVisible"
      :isEdit="isEditMode"
      :initialData="currentUser"
      @cancel="handleCancel"
      @submit="handleSubmit"
    />
  </div>
</template>

<style scoped>
#userManageView {
  padding: 24px;
  background: white;
  margin-top: 16px;
}
</style>
