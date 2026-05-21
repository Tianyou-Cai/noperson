<template>
  <div class="wallet">
    <!-- 钱包概览 -->
    <div class="balance-section">
      <div class="balance-card">
        <div class="balance-header">
          <h3 class="section-title">账户余额</h3>
          <span class="header-icon">💰</span>
        </div>
        <div class="balance-content">
          <div class="balance-amount">
            <el-statistic 
              :value="walletBalance" 
              :precision="2"
              suffix="元"
              animation 
              animationDuration="2000"
              class="balance-number"
            />
          </div>
          <p class="balance-description">可用于提现和支付</p>
        </div>
        <div class="balance-actions">
          <el-button 
            type="primary" 
            @click="showRechargeDialog = true"
            class="action-button recharge-button"
          >
            <el-icon class="button-icon"><Plus /></el-icon>
            充值
          </el-button>
          <el-button 
            type="success" 
            @click="showWithdrawDialog = true"
            :disabled="walletBalance < withdrawMinAmount"
            class="action-button withdraw-button"
          >
            <el-icon class="button-icon"><ArrowDown /></el-icon>
            提现
          </el-button>
        </div>
      </div>
    </div>

    <!-- 交易记录 -->
    <el-card class="card transaction-card">
      <template #header>
        <div class="card-header">
          <h3 class="section-title">交易记录</h3>
          <span class="header-icon">📋</span>
        </div>
      </template>
      <div class="transaction-content">
        <div class="transaction-tabs">
          <el-tabs v-model="activeTab" type="card">
            <el-tab-pane label="全部" name="all">
              <div class="transaction-list">
                <div v-if="transactions.length === 0" class="empty-transactions">
                  <el-empty description="暂无交易记录" :image-size="80" />
                </div>
                <div v-else v-for="transaction in transactions" :key="transaction.id" class="transaction-item">
                  <div class="transaction-info">
                    <div class="transaction-icon" :class="getTransactionTypeClass(transaction.transactionType)">
                      {{ getTransactionTypeIcon(transaction.transactionType) }}
                    </div>
                    <div class="transaction-details">
                      <div class="transaction-title">{{ getTransactionTypeText(transaction.transactionType) }}</div>
                      <div class="transaction-time">{{ formatTime(transaction.createTime) }}</div>
                    </div>
                  </div>
                  <div class="transaction-amount" :class="getTransactionAmountClass(transaction.transactionType)">
                    {{ transaction.transactionType === 'INCOME' ? '+' : '-' }}{{ transaction.amount.toFixed(2) }}元
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="收入" name="income">
              <div class="transaction-list">
                <div v-if="filteredTransactions.income.length === 0" class="empty-transactions">
                  <el-empty description="暂无收入记录" :image-size="80" />
                </div>
                <div v-else v-for="transaction in filteredTransactions.income" :key="transaction.id" class="transaction-item">
                  <div class="transaction-info">
                    <div class="transaction-icon income">
                      {{ getTransactionTypeIcon(transaction.transactionType) }}
                    </div>
                    <div class="transaction-details">
                      <div class="transaction-title">{{ getTransactionTypeText(transaction.transactionType) }}</div>
                      <div class="transaction-time">{{ formatTime(transaction.createTime) }}</div>
                    </div>
                  </div>
                  <div class="transaction-amount positive">
                    +{{ transaction.amount.toFixed(2) }}元
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="支出" name="expense">
              <div class="transaction-list">
                <div v-if="filteredTransactions.expense.length === 0" class="empty-transactions">
                  <el-empty description="暂无支出记录" :image-size="80" />
                </div>
                <div v-else v-for="transaction in filteredTransactions.expense" :key="transaction.id" class="transaction-item">
                  <div class="transaction-info">
                    <div class="transaction-icon expense">
                      {{ getTransactionTypeIcon(transaction.transactionType) }}
                    </div>
                    <div class="transaction-details">
                      <div class="transaction-title">{{ getTransactionTypeText(transaction.transactionType) }}</div>
                      <div class="transaction-time">{{ formatTime(transaction.createTime) }}</div>
                    </div>
                  </div>
                  <div class="transaction-amount negative">
                    -{{ transaction.amount.toFixed(2) }}元
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-card>

    <!-- 充值对话框 -->
    <el-dialog
      v-model="showRechargeDialog"
      title="账户充值"
      width="400px"
      @close="resetRechargeForm"
    >
      <div class="recharge-form">
        <el-form :model="rechargeForm" label-width="80px">
          <el-form-item label="充值金额">
            <el-input-number
              v-model="rechargeForm.amount"
              :min="rechargeMinAmount"
              :max="10000"
              :step="100"
              :precision="2"
              :controls-position="'right'"
              style="width: 100%"
            />
            <div class="amount-options">
              <el-button
                v-for="amount in quickAmounts"
                :key="amount"
                type="text"
                @click="rechargeForm.amount = amount"
                class="quick-amount-button"
              >
                {{ amount }}元
              </el-button>
            </div>
          </el-form-item>
          <el-form-item label="支付方式">
            <el-radio-group v-model="rechargeForm.paymentMethod">
              <el-radio label="alipay">支付宝</el-radio>
              <el-radio label="wechat">微信支付</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showRechargeDialog = false">取消</el-button>
          <el-button type="primary" @click="handleRecharge">确认充值</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 提现对话框 -->
    <el-dialog
      v-model="showWithdrawDialog"
      title="提现申请"
      width="400px"
      @close="resetWithdrawForm"
    >
      <div class="withdraw-form">
        <el-form :model="withdrawForm" label-width="80px">
          <el-form-item label="提现金额">
            <el-input-number
              v-model="withdrawForm.amount"
              :min="withdrawMinAmount"
              :max="walletBalance"
              :step="100"
              :precision="2"
              :controls-position="'right'"
              style="width: 100%"
            />
            <div class="withdraw-info">
              <span class="balance-info">账户余额: {{ walletBalance.toFixed(2) }}元</span>
              <span class="min-amount">最低提现: {{ withdrawMinAmount }}元</span>
            </div>
          </el-form-item>
          <el-form-item label="收款银行卡">
            <el-select
              v-model="withdrawForm.bankCardId"
              placeholder="请选择银行卡"
              style="width: 100%"
            >
              <el-option
                v-for="card in bankCards"
                :key="card.id"
                :label="`${card.bankName} ${maskCardNumber(card.cardNumber)}`"
                :value="card.id"
              />
            </el-select>
            <el-button
              type="text"
              @click="showAddBankCardDialog = true"
              class="add-card-button"
            >
              <el-icon><Plus /></el-icon> 添加银行卡
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showWithdrawDialog = false">取消</el-button>
          <el-button type="primary" @click="handleWithdraw">确认提现</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加银行卡对话框 -->
    <el-dialog
      v-model="showAddBankCardDialog"
      title="添加银行卡"
      width="400px"
      @close="resetAddBankCardForm"
    >
      <div class="add-bank-card-form">
        <el-form :model="addBankCardForm" label-width="80px">
          <el-form-item label="银行名称">
            <el-input v-model="addBankCardForm.bankName" placeholder="请输入银行名称" />
          </el-form-item>
          <el-form-item label="银行卡号">
            <el-input v-model="addBankCardForm.cardNumber" placeholder="请输入银行卡号" />
          </el-form-item>
          <el-form-item label="持卡人姓名">
            <el-input v-model="addBankCardForm.cardHolder" placeholder="请输入持卡人姓名" />
          </el-form-item>
          <el-form-item label="开户支行">
            <el-input v-model="addBankCardForm.branch" placeholder="请输入开户支行名称" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddBankCardDialog = false">取消</el-button>
          <el-button type="primary" @click="handleAddBankCard">确认添加</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'
import { Plus, ArrowDown } from '@element-plus/icons-vue'

export default {
  name: 'Wallet',
  components: {
    Plus,
    ArrowDown
  },
  setup() {
    // 状态管理
    const walletBalance = ref(1234.56)
    const transactions = ref([
      {
        id: 1,
        transactionType: 'INCOME',
        amount: 500.00,
        createTime: '2026-04-08 14:30:00'
      },
      {
        id: 2,
        transactionType: 'INCOME',
        amount: 1000.00,
        createTime: '2026-04-08 10:15:00'
      },
      {
        id: 3,
        transactionType: 'EXPENSE',
        amount: 200.00,
        createTime: '2026-04-07 16:45:00'
      },
      {
        id: 4,
        transactionType: 'INCOME',
        amount: 1000.00,
        createTime: '2026-04-06 09:30:00'
      },
      {
        id: 5,
        transactionType: 'EXPENSE',
        amount: 1000.00,
        createTime: '2026-04-05 14:00:00'
      }
    ])
    const bankCards = ref([
      {
        id: 1,
        bankName: '工商银行',
        cardNumber: '6222021234567890123',
        cardHolder: '张三'
      }
    ])
    const activeTab = ref('all')
    const showRechargeDialog = ref(false)
    const showWithdrawDialog = ref(false)
    const showAddBankCardDialog = ref(false)
    
    // 常量
    const rechargeMinAmount = 100
    const withdrawMinAmount = 100
    const quickAmounts = [100, 500, 1000, 2000, 5000]
    
    // 表单数据
    const rechargeForm = ref({
      amount: 100,
      paymentMethod: 'alipay'
    })
    
    const withdrawForm = ref({
      amount: 100,
      bankCardId: null
    })
    
    const addBankCardForm = ref({
      bankName: '',
      cardNumber: '',
      cardHolder: '',
      branch: ''
    })
    
    // 计算属性
    const filteredTransactions = computed(() => {
      const income = transactions.value.filter(t => t.transactionType === 'INCOME')
      const expense = transactions.value.filter(t => t.transactionType === 'EXPENSE')
      return { income, expense }
    })
    
    // 工具函数
    const formatTime = (timeStr) => {
      const date = new Date(timeStr)
      const now = new Date()
      const diffMs = now - date
      const diffMins = Math.floor(diffMs / 60000)
      const diffHours = Math.floor(diffMins / 60)
      const diffDays = Math.floor(diffHours / 24)
      
      if (diffMins < 1) return '刚刚'
      if (diffMins < 60) return `${diffMins}分钟前`
      if (diffHours < 24) return `${diffHours}小时前`
      if (diffDays < 7) return `${diffDays}天前`
      return date.toLocaleDateString('zh-CN')
    }
    
    const getTransactionTypeText = (type) => {
      switch (type) {
        case 'INCOME': return '账户充值'
        case 'EXPENSE': return '提现'
        default: return '其他'
      }
    }
    
    const getTransactionTypeIcon = (type) => {
      switch (type) {
        case 'INCOME': return '💵'
        case 'EXPENSE': return '💳'
        default: return '📝'
      }
    }
    
    const getTransactionTypeClass = (type) => {
      switch (type) {
        case 'INCOME': return 'income'
        case 'EXPENSE': return 'expense'
        default: return 'other'
      }
    }
    
    const getTransactionAmountClass = (type) => {
      return type === 'INCOME' ? 'positive' : 'negative'
    }
    
    const maskCardNumber = (cardNumber) => {
      if (!cardNumber || cardNumber.length < 8) return cardNumber
      const prefix = cardNumber.substring(0, 4)
      const suffix = cardNumber.substring(cardNumber.length - 4)
      return `${prefix}****${suffix}`
    }
    
    // 重置表单
    const resetRechargeForm = () => {
      rechargeForm.value = {
        amount: 100,
        paymentMethod: 'alipay'
      }
    }
    
    const resetWithdrawForm = () => {
      withdrawForm.value = {
        amount: 100,
        bankCardId: null
      }
    }
    
    const resetAddBankCardForm = () => {
      addBankCardForm.value = {
        bankName: '',
        cardNumber: '',
        cardHolder: '',
        branch: ''
      }
    }
    
    // API调用函数
    const fetchWalletInfo = async () => {
      try {
        const response = await axios.get('/owner/wallet/info')
        if (response.code === 200) {
          walletBalance.value = response.data.balance || 0
          transactions.value = response.data.transactions || []
          bankCards.value = response.data.bankCards || []
        }
      } catch (error) {
        console.error('获取钱包信息失败:', error)
        ElMessage.error('获取钱包信息失败')
      }
    }
    
    const handleRecharge = async () => {
      try {
        const response = await axios.post('/owner/wallet/recharge', {
          amount: rechargeForm.value.amount,
          paymentMethod: rechargeForm.value.paymentMethod
        })
        if (response.code === 200) {
          ElMessage.success('充值成功')
          showRechargeDialog.value = false
          resetRechargeForm()
          fetchWalletInfo()
        }
      } catch (error) {
        console.error('充值失败:', error)
        ElMessage.error('充值失败')
      }
    }
    
    const handleWithdraw = async () => {
      if (!withdrawForm.value.bankCardId) {
        ElMessage.warning('请选择收款银行卡')
        return
      }
      
      try {
        const response = await axios.post('/owner/wallet/withdraw', {
          amount: withdrawForm.value.amount,
          bankCardId: withdrawForm.value.bankCardId
        })
        if (response.code === 200) {
          ElMessage.success('提现申请已提交')
          showWithdrawDialog.value = false
          resetWithdrawForm()
          fetchWalletInfo()
        }
      } catch (error) {
        console.error('提现失败:', error)
        ElMessage.error('提现失败')
      }
    }
    
    const handleAddBankCard = async () => {
      if (!addBankCardForm.value.bankName || !addBankCardForm.value.cardNumber || !addBankCardForm.value.cardHolder) {
        ElMessage.warning('请填写完整的银行卡信息')
        return
      }
      
      try {
        const response = await axios.post('/owner/wallet/bank-card/add', addBankCardForm.value)
        if (response.code === 200) {
          ElMessage.success('银行卡添加成功')
          showAddBankCardDialog.value = false
          resetAddBankCardForm()
          fetchWalletInfo()
        }
      } catch (error) {
        console.error('添加银行卡失败:', error)
        ElMessage.error('添加银行卡失败')
      }
    }
    
    // 生命周期
    onMounted(() => {
      fetchWalletInfo()
    })
    
    return {
      walletBalance,
      transactions,
      bankCards,
      activeTab,
      showRechargeDialog,
      showWithdrawDialog,
      showAddBankCardDialog,
      rechargeMinAmount,
      withdrawMinAmount,
      quickAmounts,
      rechargeForm,
      withdrawForm,
      addBankCardForm,
      filteredTransactions,
      formatTime,
      getTransactionTypeText,
      getTransactionTypeIcon,
      getTransactionTypeClass,
      getTransactionAmountClass,
      maskCardNumber,
      handleRecharge,
      handleWithdraw,
      handleAddBankCard
    }
  }
}
</script>

<style scoped>
.wallet {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e6eaf0 100%);
  min-height: calc(100vh - 64px);
}

/* 余额卡片 */
.balance-section {
  margin-bottom: 24px;
}

.balance-card {
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(64, 158, 255, 0.3);
}

.balance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 24px;
}

.balance-content {
  text-align: center;
  margin-bottom: 32px;
}

.balance-amount {
  margin-bottom: 8px;
}

.balance-number {
  font-size: 48px !important;
  font-weight: 600;
}

.balance-description {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.balance-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.action-button {
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.recharge-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.recharge-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.withdraw-button {
  background: #67C23A;
  border: 1px solid #67C23A;
}

.withdraw-button:hover {
  background: #85ce61;
  transform: translateY(-2px);
}

.withdraw-button:disabled {
  background: #95D464;
  border-color: #95D464;
  cursor: not-allowed;
}

.button-icon {
  margin-right: 8px;
}

/* 交易记录卡片 */
.card {
  border-radius: 12px !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px 16px 24px !important;
  background-color: transparent;
  border-bottom: 1px solid #f0f0f0;
}

.transaction-content {
  padding: 20px;
}

.transaction-tabs {
  margin-bottom: 0;
}

.transaction-list {
  padding: 16px 0;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.transaction-item:hover {
  background: rgba(0, 0, 0, 0.02);
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.transaction-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.transaction-icon.income {
  background: rgba(103, 194, 58, 0.1);
}

.transaction-icon.expense {
  background: rgba(245, 108, 108, 0.1);
}

.transaction-icon.other {
  background: rgba(64, 158, 255, 0.1);
}

.transaction-details {
  flex: 1;
}

.transaction-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.transaction-time {
  font-size: 12px;
  color: #909399;
}

.transaction-amount {
  font-size: 18px;
  font-weight: 600;
}

.transaction-amount.positive {
  color: #67C23A;
}

.transaction-amount.negative {
  color: #F56C6C;
}

.empty-transactions {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

/* 充值表单 */
.recharge-form {
  padding: 20px 0;
}

.amount-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.quick-amount-button {
  padding: 6px 16px;
  border-radius: 20px;
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  font-size: 14px;
  transition: all 0.3s ease;
}

.quick-amount-button:hover {
  background: #ecf5ff;
  border-color: #c6e2ff;
  color: #409EFF;
}

/* 提现表单 */
.withdraw-form {
  padding: 20px 0;
}

.withdraw-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.add-card-button {
  margin-top: 8px;
  color: #409EFF;
}

/* 添加银行卡表单 */
.add-bank-card-form {
  padding: 20px 0;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
