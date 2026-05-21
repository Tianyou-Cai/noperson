// flyer/equipment/device-detail.js
const { api } = require('../../../utils/request');

Page({
  data: {
    deviceId: '',
    deviceDetail: null,
    loading: true,
    errorMessage: '',
    available: false
  },
  
  onLoad: function(options) {
    if (options.deviceId) {
      this.setData({
        deviceId: options.deviceId
      });
      this.loadDeviceDetail();
    }
  },
  


  // 加载设备详情
  loadDeviceDetail: async function() {
    this.setData({
      loading: true,
      errorMessage: ''
    });
    
    try {
      // 加载设备详情
      const detailResponse = await api.getDeviceDetail(this.data.deviceId);
      // 从response.data.deviceInfo获取设备详情
      const rawDetail = detailResponse.data.deviceInfo || {};
      
      // 检查设备可用性 - 增强的可用性检查逻辑
      let available = false;
      
      // 首先检查rental_status，考虑可能的字段名不一致
      const rentalStatus = rawDetail.rental_status !== undefined ? rawDetail.rental_status : 
                          (rawDetail.rentalStatus !== undefined ? rawDetail.rentalStatus : null);
      const flyerId = rawDetail.flyerId !== undefined ? rawDetail.flyerId : 
                      (rawDetail.flyer_id !== undefined ? rawDetail.flyer_id : null);
      
      // 优先根据rental_status判断设备是否可租借
      // 数据库中rental_status为0表示可租借，1表示已被租借
      if (rentalStatus === 1 || rentalStatus === '1' || rentalStatus === 'rented') {
        available = false;
      } else if (rentalStatus === 0 && (flyerId === null || flyerId === undefined || flyerId === '')) {
        available = true;
      } else {
        // 尝试通过API检查设备可用性
        try {
          const availableResponse = await api.checkDeviceAvailable(this.data.deviceId);
          available = availableResponse.data || false;
        } catch (checkError) {
          console.error('检查设备可用性失败', checkError);
        }
      }
      
      // 检查是否由当前用户租借
      let isRentedByMe = false;
      // 当rental_status为1或flyerId有值时，表示设备已被租借
      if ((rentalStatus === 1 || rentalStatus === '1' || rentalStatus === 'rented') || 
          (flyerId !== null && flyerId !== undefined && flyerId !== '')) {
        isRentedByMe = true;
      }
      
      // 字段映射，将API返回的字段名映射到WXML中使用的字段名
        const deviceDetail = {
          // 基本信息映射
          id: rawDetail.deviceId,
          name: rawDetail.deviceName || '无人机',
          type: rawDetail.deviceType || '未知型号',
          model: rawDetail.model || '未知型号',
          available: available,
          rentPrice: rawDetail.hourlyRent || 0,
          hourlyRent: rawDetail.hourlyRent || 0,
          manufacturer: rawDetail.manufacturer || '--',
          maxLoad: rawDetail.maxLoad || 0,
          maxFlightTime: rawDetail.endurance || 0,
          maxFlightAltitude: rawDetail.maxHeight || 0,
          // 根据API返回的实际数据设置飞行小时
          flightHours: rawDetail.flightHours || 0,
          // 维护信息
          lastMaintenanceDate: rawDetail.lastMaintainTime || '从未维护',
          // 机主信息
          ownerAvatar: '/assets/images/user-avatar.png', // 默认头像
          ownerName: detailResponse.data.ownerInfo?.realName || '未知机主', // 从ownerInfo获取
          ownerPhone: detailResponse.data.ownerInfo?.phone || '未知', // 从ownerInfo获取
          // 维护记录
          maintenanceRecords: detailResponse.data.maintainRecords || [],
          // 最后维护记录
          lastMaintainRecord: detailResponse.data.lastMaintainRecord || null,
          // 检查是否由当前用户租借
          isRentedByMe: isRentedByMe,
          // 显式保存rental_status以便在WXML中使用
          rental_status: rentalStatus,
          // 保留原始数据
          ...rawDetail
        };
      
      this.setData({
        deviceDetail: deviceDetail,
        available: available
      });
    } catch (error) {
      this.setData({
        errorMessage: '加载失败，请重试',
        deviceDetail: null
      });
    } finally {
      this.setData({
        loading: false
      });
    }
  },
  
  // 租借设备
  rentDevice: async function() {
    const deviceId = this.data.deviceId;
    const deviceDetail = this.data.deviceDetail;
    
    try {
      // 检查飞手资质状态
      const qualificationStatus = await api.getQualificationStatus();
      const status = qualificationStatus.data || 0; // 默认未提交
      
      // 如果资质未通过（状态不是2），提示用户去完成资质认证
      if (status !== 2) {
        wx.showModal({
          title: '资质未通过',
          content: '您需要先完成飞手资质认证才能租借设备，是否立即去认证？',
          success: (res) => {
            if (res.confirm) {
              wx.navigateTo({
                url: '/pages/flyer/qualification/index'
              });
            }
          }
        });
        return;
      }
      
      // 获取飞手好评率
      wx.showLoading({ title: '检查押金要求...' });
      const userId = wx.getStorageSync('userInfo')?.id;
      let needDeposit = false;
      let depositAmount = 0;
      
      try {
        const positiveRateResponse = await api.getFlyerPositiveRate(userId);
        const positiveRate = positiveRateResponse.data || 0;
        console.log('飞手好评率:', positiveRate);
        
        // 判断是否需要押金：好评率<=50需要500押金
        if (positiveRate <= 50) {
          needDeposit = true;
          depositAmount = 500;
        }
      } catch (rateError) {
        console.error('获取好评率失败', rateError);
        // 默认需要押金
        needDeposit = true;
        depositAmount = 500;
      }
      
      wx.hideLoading();
      
      // 计算总金额
      const rentPrice = deviceDetail.rentPrice || 0;
      const totalAmount = needDeposit ? rentPrice + depositAmount : rentPrice;
      
      // 显示确认弹窗
      let confirmContent = `确定要租借该无人机吗？\n设备型号：${deviceDetail.model || '未知'}\n租借价格：¥${rentPrice}/小时`;
      if (needDeposit) {
        confirmContent += `\n押金：¥${depositAmount}`;
      }
      confirmContent += `\n总计：¥${totalAmount}`;
      
      wx.showModal({
        title: '确认租借',
        content: confirmContent,
        success: async (res) => {
          if (res.confirm) {
            wx.showLoading({ title: '创建支付订单中...' });
            try {
              // 调用设备租借接口（后端会自动处理余额扣除和押金）
              wx.showLoading({ title: '处理租借...' });
              try {
                const rentResponse = await api.rentDeviceByFlyer(deviceId);
                
                wx.hideLoading();
                
                // 更新本地状态，将设备标记为已租借
                this.setData({
                  'deviceDetail.available': false,
                  'deviceDetail.isRentedByMe': true,
                  'deviceDetail.rental_status': 1,
                  available: false
                });
                
                wx.showToast({
                  title: '设备租借成功',
                  icon: 'success'
                });
                
                // 重新加载设备详情以确保数据一致性
                setTimeout(async () => {
                  await this.loadDeviceDetail();
                }, 1500);
              } catch (rentError) {
                console.error('租借设备失败', rentError);
                wx.hideLoading();
                const errorMsg = rentError.message || rentError.data?.message || rentError.data?.msg || '租借失败，请重试';
                wx.showToast({
                  title: errorMsg,
                  icon: 'none'
                });
                await this.loadDeviceDetail();
              }
            } catch (paymentError) {
              console.error('创建支付订单失败', paymentError);
              wx.hideLoading();
              const errorMsg = paymentError.message || paymentError.data?.message || paymentError.data?.msg || '创建支付订单失败，请重试';
              wx.showToast({
                title: errorMsg,
                icon: 'none'
              });
            }
          }
        }
      });
    } catch (error) {
      console.error('租借设备流程异常', error);
      wx.hideLoading();
    }
  },
  
  // 归还设备
  returnDevice: async function() {
    const deviceId = this.data.deviceId;
    
    try {
      // 显示确认弹窗
      wx.showModal({
        title: '确认归还',
        content: '确定要归还该无人机吗？',
        success: async (res) => {
          if (res.confirm) {
            wx.showLoading({ title: '归还中...' });
            try {
              // 调用设备归还接口
              const returnResponse = await api.returnDeviceByFlyer(deviceId);
              console.log('归还设备接口返回:', returnResponse);
              
              wx.hideLoading(); // 提前隐藏loading
              
              // 直接更新本地状态，将设备标记为可租借
              this.setData({
                'deviceDetail.available': true,
                'deviceDetail.isRentedByMe': false,
                'deviceDetail.rental_status': 0,
                available: true
              });
              
              wx.showToast({
                title: '设备归还成功',
                icon: 'success'
              });
              
              // 重新加载设备详情以确保数据一致性
              setTimeout(async () => {
                console.log('归还后重新加载设备详情...');
                await this.loadDeviceDetail();
              }, 1500);
            } catch (returnError) {
              console.error('归还设备失败', returnError);
              wx.hideLoading(); // 确保在错误情况下也隐藏loading
              // 检查是否有错误信息
              const errorMsg = returnError.message || returnError.data?.message || returnError.data?.msg || '归还失败，请重试';
              wx.showToast({
                title: errorMsg,
                icon: 'none'
              });
              // 尝试重新加载设备详情，确保显示最新状态
              await this.loadDeviceDetail();
            }
          }
        }
      });
    } catch (error) {
      console.error('归还设备流程异常', error);
      wx.hideLoading();
    }
  },
  
  // 取消租借（全额退款）
  cancelRental: async function() {
    wx.showModal({
      title: '取消租借',
      content: '确定要取消租借该设备吗？取消后将全额退款。',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '处理中...' });
            // 调用取消租借接口
            await api.cancelDeviceRental(this.data.deviceId);
            
            wx.hideLoading(); // 提前隐藏loading
            
            wx.showToast({
              title: '取消成功，已全额退款',
              icon: 'success'
            });
            
            // 重新加载设备详情
            await this.loadDeviceDetail();
          } catch (error) {
            console.error('取消租借失败', error);
            wx.hideLoading(); // 确保在错误情况下也隐藏loading
            wx.showToast({
              title: '取消失败，请重试',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 在线联系
  contactOnline: function() {
    const ownerName = this.data.deviceDetail.ownerName || '未知';
    wx.showModal({
      title: '在线联系',
      content: `是否立即联系机主 ${ownerName}？`,
      success: (res) => {
        if (res.confirm) {
          // 跳转到聊天页面
          wx.navigateTo({
            url: `/pages/chat/chat?targetUserId=${this.data.deviceDetail.ownerId || 0}&targetUserName=${ownerName}`
          });
        }
      }
    });
  },

  // 电话联系
  contactPhone: function() {
    const phone = this.data.deviceDetail.ownerPhone;
    if (phone && phone !== '未知') {
      wx.makePhoneCall({
        phoneNumber: phone,
        success: () => {
          console.log('拨打电话成功');
        },
        fail: (error) => {
          console.error('拨打电话失败', error);
          wx.showToast({
            title: '拨打电话失败',
            icon: 'none'
          });
        }
      });
    } else {
      wx.showToast({
        title: '机主电话未设置',
        icon: 'none'
      });
    }
  }
});