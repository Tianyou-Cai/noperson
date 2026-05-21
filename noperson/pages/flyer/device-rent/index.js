// flyer/device-rent/index.js
const { api } = require('../../../utils/request');

Page({
  data: {
    ownerId: '',
    deviceList: [],
    loading: false,
    error: '',
    refreshing: false,
    showRentForm: false,
    selectedDevice: {},
    rentForm: {
      rentalHours: '',
      pickupTime: '',
      remark: ''
    },
    estimatedAmount: 0,
    minDate: ''
  },

  onLoad(options) {
    // 从页面参数中获取ownerId
    if (options.ownerId) {
      this.setData({
        ownerId: options.ownerId
      });
      // 设置最小日期为当前时间
      const now = new Date();
      const minDate = now.toISOString().slice(0, 16).replace('T', ' ');
      this.setData({
        minDate: minDate.replace(' ', 'T')
      });
      // 加载设备列表
      this.loadDeviceList();
    } else {
      this.setData({
        error: '机主ID参数错误',
        loading: false
      });
    }
  },

  // 加载设备列表
  loadDeviceList() {
    const { ownerId } = this.data;
    
    if (!ownerId) {
      this.setData({
        error: '机主ID为空',
        loading: false,
        refreshing: false
      });
      return;
    }
    
    this.setData({
      loading: !this.data.refreshing, // 如果是下拉刷新，不设置loading为true
      error: ''
    });
    
    // 调用获取机主设备列表的接口
    api.getOwnerDevices(ownerId)
      .then(res => {
        const deviceData = res.data || [];
        
        // 转换后端数据格式为前端需要的格式
        const formattedDevices = deviceData.map(device => ({
          id: device.deviceId || device.id,
          name: device.deviceName,
          type: device.model,
          status: device.flyerId ? 'unavailable' : 'available',
          flightHours: device.flightHours || 0,
          lastMaintenanceDate: device.lastMaintainTime ? device.lastMaintainTime.split('T')[0] : '从未维护',
          hourlyRent: device.hourlyRent || 0,
          description: device.description || '--'
        }));
        
        this.setData({
          deviceList: formattedDevices,
          loading: false,
          refreshing: false
        });
      })
      .catch(error => {
        console.error('获取设备列表失败:', error);
        this.setData({
          error: error.message || '获取设备列表失败',
          loading: false,
          refreshing: false
        });
      });
  },
  
  // 下拉刷新
  onRefresh() {
    this.setData({
      refreshing: true
    });
    this.loadDeviceList();
  },
  
  // 获取设备状态文本
  getStatusText(status) {
    const statusMap = {
      'available': '可租借',
      'unavailable': '已租借',
      'maintain': '维护中',
      'fault': '故障'
    };
    return statusMap[status] || '未知状态';
  },
  
  // 租借设备
  rentDevice(e) {
    const deviceId = e.currentTarget.dataset.id;
    const { deviceList } = this.data;
    
    // 查找选中的设备
    const selectedDevice = deviceList.find(device => device.id === deviceId);
    if (selectedDevice) {
      // 重置表单
      this.setData({
        selectedDevice: selectedDevice,
        showRentForm: true,
        rentForm: {
          rentalHours: '',
          pickupTime: '',
          remark: ''
        },
        estimatedAmount: 0
      });
    }
  },
  
  // 关闭租借表单
  closeRentForm() {
    this.setData({
      showRentForm: false
    });
  },
  
  // 租赁时长输入
  onRentalHoursInput(e) {
    const rentalHours = e.detail.value;
    const { selectedDevice } = this.data;
    
    this.setData({
      'rentForm.rentalHours': rentalHours
    });
    
    // 计算预估费用
    this.calculateEstimatedAmount(rentalHours, selectedDevice.hourlyRent);
  },
  
  // 取设备时间选择
  onPickupTimeChange(e) {
    this.setData({
      'rentForm.pickupTime': e.detail.value
    });
  },
  
  // 备注输入
  onRemarkInput(e) {
    this.setData({
      'rentForm.remark': e.detail.value
    });
  },
  
  // 计算预估费用
  calculateEstimatedAmount(rentalHours, hourlyRent) {
    if (rentalHours && hourlyRent) {
      const estimatedAmount = parseFloat(rentalHours) * parseFloat(hourlyRent);
      this.setData({
        estimatedAmount: estimatedAmount.toFixed(2)
      });
    } else {
      this.setData({
        estimatedAmount: 0
      });
    }
  },
  
  // 格式化日期时间
  formatDateTime(dateTime) {
    if (!dateTime) return '';
    
    const date = new Date(dateTime.replace('T', ' '));
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  },
  
  // 提交租借表单
  submitRentForm() {
    const { selectedDevice, rentForm } = this.data;
    
    wx.showModal({
      title: '确认租借',
      content: `确认租借设备：${selectedDevice.name}\n租赁时长：${rentForm.rentalHours}小时\n预估费用：¥${this.data.estimatedAmount}`,
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({
            title: '处理中...'
          });
          
          // 准备租借数据
          const rentData = {
            deviceId: selectedDevice.id,
            rentalHours: parseInt(rentForm.rentalHours),
            pickupTime: new Date(rentForm.pickupTime.replace('T', ' ')).toISOString(),
            remark: rentForm.remark
          };
          
          console.log('提交租借数据:', rentData);
          
          // 调用租借设备接口
          api.rentDeviceByFlyer(rentData)
            .then(res => {
              wx.hideLoading();
              
              wx.showToast({
                title: '租借成功',
                icon: 'success',
                duration: 2000,
                success: () => {
                  // 更新设备状态为已租借
                  const { deviceList } = this.data;
                  const updatedList = deviceList.map(device => {
                    if (device.id === selectedDevice.id) {
                      return { ...device, status: 'unavailable' };
                    }
                    return device;
                  });
                  this.setData({
                    deviceList: updatedList,
                    showRentForm: false
                  });
                }
              });
            })
            .catch(error => {
              wx.hideLoading();
              console.error('租借设备失败:', error);
              
              // 根据错误类型显示不同的提示信息
              let errorMessage = '租借失败';
              if (error.message) {
                if (error.message.includes('余额不足')) {
                  errorMessage = '余额不足，请充值！';
                } else if (error.message.includes('设备正在被其他操作占用')) {
                  errorMessage = '设备正在被其他飞手租借，请稍后重试';
                } else if (error.message.includes('设备不存在')) {
                  errorMessage = '设备不存在';
                } else {
                  errorMessage = error.message;
                }
              }
              
              wx.showToast({
                title: errorMessage,
                icon: 'none',
                duration: 3000
              });
            });
        }
      }
    });
  }
});