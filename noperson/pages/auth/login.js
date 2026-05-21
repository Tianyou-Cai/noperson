// login.js
const { api } = require('../../utils/request');

Page({
  data: {
    loading: false,
    showWechatLoginDialog: false,
    roles: [
      {
        code: 'FARMER',
        name: '农户',
        icon: '/assets/images/nonghu.jpg',
        description: '发布农业需求，查看巡检报告'
      },
      {
        code: 'FLYER',
        name: '飞手',
        icon: '/assets/images/feishou.jpg',
        description: '承接作业任务，上传巡检数据'
      }
    ],
    selectedRole: ''
  },

  onLoad: function() {
    const app = getApp();
    this.setData({
      appName: app.globalData.appName
    });
  },

  selectRole: function(e) {
    const roleCode = e.currentTarget.dataset.rolecode;
    this.setData({
      selectedRole: roleCode
    });
  },

  enterRole: function(e) {
    const roleCode = e.currentTarget.dataset.rolecode || this.data.selectedRole;
    
    if (!roleCode) {
      wx.showToast({
        title: '请选择角色',
        icon: 'none'
      });
      return;
    }

    this.setData({
      selectedRole: roleCode,
      showWechatLoginDialog: true
    });
  },

  onConfirmWechatLogin: function() {
    this.setData({
      showWechatLoginDialog: false
    });
    this.performWechatLogin();
  },

  onCancelWechatLogin: function() {
    this.setData({
      showWechatLoginDialog: false
    });
    wx.showToast({
      title: '已取消登录',
      icon: 'none'
    });
  },

  performWechatLogin: async function() {
    this.setData({
      loading: true
    });

    try {
      console.log('开始微信登录...');
      
      // 登录前清除旧的登录状态，避免缓存问题
      wx.removeStorageSync('token');
      wx.removeStorageSync('userId');
      wx.removeStorageSync('userRole');
      wx.removeStorageSync('userInfo');
      
      const app = getApp();
      app.globalData.token = '';
      app.globalData.userId = null;
      app.globalData.userRole = null;
      app.globalData.userInfo = null;

      const loginResult = await wx.login({
        timeout: 10000
      });

      if (!loginResult.code) {
        wx.showToast({
          title: '微信登录失败，请重试',
          icon: 'none'
        });
        this.setData({ loading: false });
        return;
      }

      console.log('微信登录code:', loginResult.code);

      const res = await api.wxMiniLogin({
        code: loginResult.code,
        nickname: '',
        avatar: ''
      });

      console.log('登录成功，返回数据：', res);

      if (res.code === 200 && res.data) {
        const app = getApp();
        const token = res.data.token;
        const userId = res.data.userId;
        const roles = res.data.roles || [];

        app.globalData.token = token;
        app.globalData.userId = userId;
        app.globalData.roles = roles;
        app.globalData.userInfo = {
          id: userId,
          userId: userId,
          username: res.data.username || '',
          realName: res.data.username || '',
          avatar: res.data.avatar || '',
          roles: roles
        };

        wx.setStorageSync('token', token);
        wx.setStorageSync('userId', userId);
        wx.setStorageSync('userRole', this.data.selectedRole);
        wx.setStorageSync('userInfo', app.globalData.userInfo);

        const isNewRole = !roles.includes(this.data.selectedRole);

        if (isNewRole) {
          const roleRes = await api.selectRole({
            userId: userId,
            roleCode: this.data.selectedRole
          });

          if (roleRes.code !== 200) {
            wx.showToast({
              title: roleRes.message || '角色选择失败',
              icon: 'none'
            });
            this.setData({ loading: false });
            return;
          }
        }

        app.globalData.currentRole = this.data.selectedRole;
        app.globalData.userRole = this.data.selectedRole.toLowerCase();

        this.navigateToRolePage(this.data.selectedRole);
      } else {
        wx.showToast({
          title: res.message || '登录失败',
          icon: 'none'
        });
      }

    } catch (error) {
      console.error('登录失败', error);
      wx.showToast({
        title: error.message || '登录失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({
        loading: false
      });
    }
  },

  navigateToRolePage: function(role) {
    const app = getApp();

    switch(role) {
      case 'FARMER':
        app.globalData.userRole = 'FARMER';
        wx.redirectTo({
          url: '/pages/farmer/index'
        });
        break;
      case 'FLYER':
        app.globalData.userRole = 'FLYER';
        wx.switchTab({
          url: '/pages/flyer/index'
        });
        break;
      default:
        wx.showToast({
          title: '角色类型错误',
          icon: 'none'
        });
    }
  }
})