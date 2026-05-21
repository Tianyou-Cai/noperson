// payment/create.js
const { api } = require('../../utils/request');

Page({
  data: {
    orderType: '',
    orderId: '',
    amount: 0,
    orderTitle: '',
    loading: false,
    confirmLoading: false,
    paymentMethods: [
      { id: 'WALLET', name: '钱包支付', icon: '/assets/images/wallet.png' },
      { id: 'ALIPAY', name: '支付宝', icon: '/assets/images/alipay.png' }
    ],
    selectedMethod: 'WALLET',
    type: '',
    deviceId: '',
    deviceName: '',
    walletBalance: 0,
    qrCodeUrl: '',
    showQrCode: false,
    currentPaymentData: null
  },
  
  onLoad: function(options) {
    const { orderType, orderId, amount } = options;
    
    let orderTitle = '';
    if (orderType === 'INSPECTION') {
      orderTitle = '农业巡检服务';
    } else if (orderType === 'SPRAY') {
      orderTitle = '农业喷洒服务';
    }
    
    this.setData({
      orderType: orderType || '',
      orderId: orderId || '',
      amount: parseFloat(amount) || 0,
      orderTitle: orderTitle,
      type: options.type || '',
      deviceId: options.deviceId || '',
      deviceName: decodeURIComponent(options.deviceName || '')
    });
    
    if (this.data.type === 'device_rent') {
      wx.setNavigationBarTitle({
        title: `租借${this.data.deviceName || '设备'}支付`
      });
    }
    
    this.loadWalletBalance();
  },
  
  loadWalletBalance: async function() {
    try {
      const response = await api.getWalletBalance();
      if (response && response.data) {
        this.setData({
          walletBalance: parseFloat(response.data.balance) || 0
        });
      }
    } catch (error) {
      console.error('获取钱包余额失败', error);
    }
  },
  
  selectPaymentMethod: function(e) {
    const methodId = e.currentTarget.dataset.id;
    
    if (methodId === 'WALLET' && this.data.walletBalance < this.data.amount) {
      wx.showToast({
        title: '钱包余额不足',
        icon: 'none'
      });
      return;
    }
    
    this.setData({
      selectedMethod: methodId
    });
  },
  
  createPayment: async function() {
    if (!this.data.orderType || !this.data.orderId || this.data.amount <= 0) {
      wx.showToast({
        title: '支付信息不完整',
        icon: 'none'
      });
      return;
    }
    
    this.setData({
      loading: true
    });
    
    try {
      if (this.data.selectedMethod === 'WALLET') {
        await this.handleWalletPayment();
      } else {
        await this.handleAlipayPayment();
      }
    } catch (error) {
      console.error('支付失败', error);
      wx.showToast({
        title: '支付失败，请重试',
        icon: 'none'
      });
      this.setData({
        loading: false
      });
    }
  },
  
  handleWalletPayment: async function() {
    if (this.data.walletBalance < this.data.amount) {
      wx.showToast({
        title: '钱包余额不足',
        icon: 'none'
      });
      this.setData({
        loading: false
      });
      return;
    }
    
    wx.showModal({
      title: '支付确认',
      content: `确认使用钱包支付${this.data.amount}元？`,
      success: async (res) => {
        if (res.confirm) {
          wx.showLoading({
            title: '支付处理中...',
            mask: true
          });
          
          try {
            const response = await api.createPayment({
              orderType: this.data.orderType,
              orderId: this.data.orderId,
              amount: this.data.amount,
              paymentMethod: 'WALLET'
            });
            
            wx.hideLoading();
            
            if (response && response.code === 200) {
              if (this.data.type === 'device_rent' && this.data.deviceId) {
                await api.rentDevice(this.data.deviceId);
                wx.showToast({
                  title: '支付成功，设备已租借',
                  icon: 'success',
                  duration: 2000
                });
                setTimeout(() => {
                  wx.navigateBack();
                }, 2000);
              } else {
                wx.showToast({
                  title: '支付成功',
                  icon: 'success',
                  duration: 2000
                });
                setTimeout(() => {
                  wx.navigateBack();
                }, 2000);
              }
            } else {
              wx.showToast({
                title: response.message || '支付失败',
                icon: 'none'
              });
            }
          } catch (error) {
            wx.hideLoading();
            console.error('钱包支付失败', error);
            wx.showToast({
              title: '支付失败，请重试',
              icon: 'none'
            });
          }
          
          this.setData({
            loading: false
          });
        } else {
          this.setData({
            loading: false
          });
        }
      }
    });
  },
  
  handleAlipayPayment: async function() {
    this.setData({
      loading: true
    });
    
    try {
      const response = await api.createAlipayOrder({
        orderType: this.data.orderType,
        orderId: this.data.orderId,
        amount: this.data.amount,
        orderTitle: this.data.orderTitle
      });
      
      if (response && response.data) {
        const paymentData = response.data;
        
        // 支付宝SDK返回的qrCode已经是二维码图片URL，直接使用
        const qrCodeUrl = paymentData.qrCode;
        
        // 更新页面显示二维码
        this.setData({
          qrCodeUrl: qrCodeUrl,
          showQrCode: true,
          currentPaymentData: paymentData,
          loading: false
        });
        
        console.log('支付宝二维码已获取并显示在页面上:', qrCodeUrl);
      }
    } catch (error) {
      console.error('创建支付宝订单失败', error);
      wx.showToast({
        title: '创建订单失败，请重试',
        icon: 'none'
      });
      this.setData({
        loading: false
      });
    }
  },
  
  confirmAlipayPayment: async function() {
    const paymentData = this.data.currentPaymentData;
    if (!paymentData) {
      wx.showToast({
        title: '支付信息无效',
        icon: 'none'
      });
      return;
    }
    
    this.setData({
      confirmLoading: true
    });
    
    // 查询支付状态
    wx.showLoading({
      title: '查询支付状态...',
      mask: true
    });
    
    setTimeout(async () => {
      try {
        // 模拟支付成功回调
        const result = await api.alipayNotify({
          outTradeNo: paymentData.outTradeNo,
          tradeStatus: 'TRADE_SUCCESS'
        });
        
        wx.hideLoading();
        
        if (result && result.code === 200) {
          if (this.data.type === 'device_rent' && this.data.deviceId) {
            await api.rentDevice(this.data.deviceId);
            wx.showToast({
              title: '支付成功，设备已租借',
              icon: 'success',
              duration: 2000
            });
            setTimeout(() => {
              wx.navigateBack();
            }, 2000);
          } else {
            wx.showToast({
              title: '支付成功',
              icon: 'success',
              duration: 2000
            });
            setTimeout(() => {
              wx.switchTab({
                url: '/pages/farmer/index'
              });
            }, 2000);
          }
        } else {
          wx.showToast({
            title: '支付失败，请重试',
            icon: 'none'
          });
        }
      } catch (error) {
        wx.hideLoading();
        console.error('支付宝支付回调失败', error);
        wx.showToast({
          title: '支付失败，请重试',
          icon: 'none'
        });
      }
      
      this.setData({
        confirmLoading: false,
        showQrCode: false
      });
    }, 1500);
  },
  
  cancelPayment: function() {
    wx.navigateBack();
  }
})