<template>
  <div class="farmers-container">
    <h2>农户管理</h2>
    <el-card>
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索农户姓名或电话" style="width: 300px; margin-right: 10px;"></el-input>
        <el-button type="primary" @click="getFarmersList">搜索</el-button>
      </div>
      <el-table :data="farmersList" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="userId" label="用户ID" width="100"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="realName" label="真实姓名"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额"></el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewFarmerDetail(scope.row)">查看</el-button>
            <el-button type="warning" size="small" @click="editFarmer(scope.row)">修改</el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="deleteFarmer(scope.row)"
              :disabled="scope.row.status === 0"
            >删除</el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="toggleStatus(scope.row)"
            >{{ scope.row.status === 1 ? '禁用' : '启用' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
    
    <!-- 农户详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="农户详情" width="600px">
      <el-descriptions :column="1" v-if="selectedFarmer">
        <el-descriptions-item label="用户ID">{{ selectedFarmer.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ selectedFarmer.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ selectedFarmer.realName }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ selectedFarmer.phone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="selectedFarmer.status === 1 ? 'success' : 'danger'">
            {{ selectedFarmer.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="余额">{{ selectedFarmer.balance }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ selectedFarmer.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 修改农户对话框 -->
    <el-dialog v-model="editDialogVisible" title="修改农户信息" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveFarmer">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from '../utils/axios'

export default {
  data() {
    return {
      farmersList: [],
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      viewDialogVisible: false,
      editDialogVisible: false,
      selectedFarmer: null,
      editForm: {
        userId: null,
        username: '',
        realName: '',
        phone: '',
        status: 1
      }
    }
  },
  mounted() {
    this.getFarmersList()
  },
  methods: {
    async getFarmersList() {
      try {
        // 调用后端管理员API获取农户列表
        const response = await axios.get('/admin/farmers/page', {
          params: {
            pageNum: this.currentPage,
            pageSize: this.pageSize,
            keyword: this.searchKeyword || ''
          }
        })
        if (response.code === 200) {
          this.farmersList = response.data.records || []
          this.total = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取农户列表失败')
          // 如果获取失败，使用空数组
          this.farmersList = []
          this.total = 0
        }
      } catch (error) {
        console.error('获取农户列表失败:', error)
        this.$message.error('获取农户列表失败，请稍后重试')
        // 错误情况下使用空数组
        this.farmersList = []
        this.total = 0
      }
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.getFarmersList()
    },
    handleCurrentChange(current) {
      this.currentPage = current
      this.getFarmersList()
    },

    viewFarmerDetail(farmer) {
      this.selectedFarmer = farmer
      this.viewDialogVisible = true
    },
    editFarmer(farmer) {
      this.editForm = {
        userId: farmer.userId,
        username: farmer.username,
        realName: farmer.realName,
        phone: farmer.phone,
        status: farmer.status
      }
      this.editDialogVisible = true
    },
    async saveFarmer() {
      try {
        const response = await axios.put('/admin/farmers', this.editForm)
        if (response.code === 200) {
          this.$message.success('修改成功')
          this.editDialogVisible = false
          this.getFarmersList()
        } else {
          this.$message.error(response.message || '修改失败')
        }
      } catch (error) {
        console.error('修改失败:', error)
        this.$message.error('修改失败，请稍后重试')
      }
    },
    async deleteFarmer(farmer) {
      this.$confirm('确定要删除该农户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await axios.delete(`/admin/farmers/${farmer.userId}`)
          if (response.code === 200) {
            this.$message.success('删除成功')
            this.getFarmersList()
          } else {
            this.$message.error(response.message || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败，请稍后重试')
        }
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    async toggleStatus(farmer) {
      const newStatus = farmer.status === 1 ? 0 : 1
      const action = newStatus === 1 ? '启用' : '禁用'
      
      this.$confirm(`确定要${action}该农户吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await axios.put(`/admin/farmers/${farmer.userId}/status`, null, {
            params: { status: newStatus }
          })
          if (response.code === 200) {
            this.$message.success(`${action}成功`)
            this.getFarmersList()
          } else {
            this.$message.error(response.message || `${action}失败`)
          }
        } catch (error) {
          console.error(`${action}失败:`, error)
          this.$message.error(`${action}失败，请稍后重试`)
        }
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    }
  }
}
</script>

<style scoped>
.farmers-container {
  height: 100%;
}

.farmers-container h2 {
  margin-bottom: 20px;
  color: #303133;
}

.search-bar {
  display: flex;
  align-items: center;
}
</style>