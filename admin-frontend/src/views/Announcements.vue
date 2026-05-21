<template>
  <div class="announcements-container">
    <div class="page-header">
      <h2>公告管理</h2>
      <button class="add-btn" @click="showAddModal = true">发布公告</button>
    </div>

    <div class="search-bar">
      <input 
        type="text" 
        v-model="searchForm.title" 
        placeholder="搜索公告标题"
        class="search-input"
      />
      <select v-model="searchForm.status" class="status-select">
        <option value="">全部状态</option>
        <option value="1">启用</option>
        <option value="0">禁用</option>
      </select>
      <button class="search-btn" @click="loadList">搜索</button>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>公告标题</th>
            <th>目标角色</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in announcementList" :key="item.id">
            <td>{{ item.id }}</td>
            <td class="title-cell">{{ item.title }}</td>
            <td>{{ getRoleText(item.targetRole) }}</td>
            <td>
              <span class="status-tag" :class="{ active: item.isActive === 1 }">
                {{ item.isActive === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td class="action-cell">
              <button class="action-btn edit" @click="editAnnouncement(item)">编辑</button>
              <button class="action-btn status" @click="toggleStatus(item)">
                {{ item.isActive === 1 ? '禁用' : '启用' }}
              </button>
              <button class="action-btn delete" @click="deleteAnnouncement(item)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      />
    </div>

    <div class="modal-overlay" v-if="showAddModal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editForm.id ? '编辑公告' : '发布公告' }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>公告标题</label>
            <input 
              type="text" 
              v-model="editForm.title" 
              class="form-input"
              placeholder="请输入公告标题"
            />
          </div>
          <div class="form-group">
            <label>目标角色</label>
            <select v-model="editForm.targetRole" class="form-select">
              <option value="ALL">全部用户</option>
              <option value="FARMER">农户</option>
              <option value="FLYER">飞手</option>
            </select>
          </div>
          <div class="form-group">
            <label>公告内容</label>
            <textarea 
              v-model="editForm.content" 
              class="form-textarea"
              placeholder="请输入公告内容"
              rows="6"
            ></textarea>
          </div>
          <div class="form-group">
            <label>
              <input type="checkbox" v-model="editForm.isActive" />
              启用公告
            </label>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeModal">取消</button>
          <button class="submit-btn" @click="submitForm">
            {{ editForm.id ? '保存修改' : '发布公告' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const announcementList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showAddModal = ref(false)

const searchForm = ref({
  title: '',
  status: ''
})

const editForm = ref({
  id: null,
  title: '',
  content: '',
  targetRole: 'ALL',
  isActive: 1
})

const getRoleText = (role) => {
  const roleMap = {
    'ALL': '全部用户',
    'FARMER': '农户',
    'FLYER': '飞手'
  }
  return roleMap[role] || role
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const loadList = async () => {
  try {
    const token = localStorage.getItem('adminToken')
    const response = await axios.get('/api/announcement/list', {
      headers: { Authorization: `Bearer ${token}` },
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        title: searchForm.value.title || undefined,
        isActive: searchForm.value.status ? parseInt(searchForm.value.status) : undefined
      }
    })
    if (response.data.code === 200) {
      announcementList.value = response.data.data.records || []
      total.value = response.data.data.total || 0
    }
  } catch (error) {
    console.error('加载公告列表失败:', error)
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadList()
}

const editAnnouncement = (item) => {
  editForm.value = {
    id: item.id,
    title: item.title,
    content: item.content,
    targetRole: item.targetRole,
    isActive: item.isActive
  }
  showAddModal.value = true
}

const toggleStatus = async (item) => {
  try {
    const token = localStorage.getItem('adminToken')
    const response = await axios.post(
      `/api/announcement/${item.id}/status`,
      null,
      {
        headers: { Authorization: `Bearer ${token}` },
        params: { isActive: item.isActive === 1 ? 0 : 1 }
      }
    )
    if (response.data.code === 200) {
      item.isActive = item.isActive === 1 ? 0 : 1
    }
  } catch (error) {
    console.error('修改状态失败:', error)
  }
}

const deleteAnnouncement = async (item) => {
  if (!confirm(`确定要删除公告「${item.title}」吗？`)) return
  
  try {
    const token = localStorage.getItem('adminToken')
    const response = await axios.delete(`/api/announcement/${item.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (response.data.code === 200) {
      loadList()
    }
  } catch (error) {
    console.error('删除公告失败:', error)
  }
}

const closeModal = () => {
  showAddModal.value = false
  editForm.value = {
    id: null,
    title: '',
    content: '',
    targetRole: 'ALL',
    isActive: 1
  }
}

const submitForm = async () => {
  if (!editForm.value.title || !editForm.value.content) {
    alert('请填写公告标题和内容')
    return
  }

  try {
    const token = localStorage.getItem('adminToken')
    let response
    
    if (editForm.value.id) {
      response = await axios.put(
        `/api/announcement/${editForm.value.id}`,
        {
          title: editForm.value.title,
          content: editForm.value.content,
          targetRole: editForm.value.targetRole,
          isActive: editForm.value.isActive ? 1 : 0
        },
        { headers: { Authorization: `Bearer ${token}` } }
      )
    } else {
      response = await axios.post(
        '/api/announcement',
        {
          title: editForm.value.title,
          content: editForm.value.content,
          targetRole: editForm.value.targetRole,
          isActive: editForm.value.isActive ? 1 : 0
        },
        { headers: { Authorization: `Bearer ${token}` } }
      )
    }

    if (response.data.code === 200) {
      closeModal()
      loadList()
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.announcements-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.add-btn {
  background: #1890ff;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.status-select {
  padding: 10px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.search-btn {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #d9d9d9;
  padding: 10px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.table-container {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  background: #fafafa;
  font-weight: 600;
  color: #666;
}

.title-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  background: #f5f5f5;
  color: #999;
}

.status-tag.active {
  background: #f6ffed;
  color: #52c41a;
}

.action-cell {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.action-btn.edit {
  background: #fff7e6;
  color: #fa8c16;
}

.action-btn.status {
  background: #e6f7ff;
  color: #1890ff;
}

.action-btn.delete {
  background: #fff2f0;
  color: #f5222d;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  line-height: 1;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.form-input,
.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn {
  padding: 8px 20px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: white;
  color: #666;
  cursor: pointer;
  font-size: 14px;
}

.submit-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  background: #1890ff;
  color: white;
  cursor: pointer;
  font-size: 14px;
}
</style>