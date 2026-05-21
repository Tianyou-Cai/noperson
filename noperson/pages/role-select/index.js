// index.js
const { api } = require('../../utils/request');

Page({
  data: {
    allRoles: [
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
    userRoles: [],
    availableRoles: [],
    selectedRole: '',
    loading: false
  },

  onLoad: function() {
    const app = getApp();
    const userId = app.globalData.userId;
    console.log('当前用户ID:', userId);

    if (!userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        wx.redirectTo({
          url: '/pages/auth/login'
        });
      }, 1500);
      return;
    }

    this.loadRoles(userId);
  },

  loadRoles: async function(userId) {
    try {
      const [userRolesRes, availableRolesRes] = await Promise.all([
        api.getUserRoles(userId),
        api.getAvailableRoles()
      ]);

      const userRoles = userRolesRes.data || [];
      const allRoles = availableRolesRes.data?.roles || [];

      const availableRoles = allRoles.filter(role => !userRoles.includes(role));

      this.setData({
        userRoles: userRoles,
        availableRoles: availableRoles,
        selectedRole: userRoles.length > 0 ? userRoles[0] : ''
      });

      console.log('用户已有角色:', userRoles);
      console.log('可选角色:', availableRoles);
    } catch (error) {
      console.log('获取角色信息失败:', error);
    }
  },

  selectRole: function(e) {
    const roleCode = e.currentTarget.dataset.rolecode;
    console.log('选中角色:', roleCode);
    this.setData({
      selectedRole: roleCode
    });
  },

  enterRoleHome: async function() {
    const { selectedRole, userRoles } = this.data;

    if (!selectedRole) {
      wx.showToast({
        title: '请选择一个角色',
        icon: 'none'
      });
      return;
    }

    const app = getApp();
    const userId = app.globalData.userId;

    if (!userId) {
      wx.showToast({
        title: '登录已过期，请重新登录',
        icon: 'none'
      });
      setTimeout(() => {
        wx.redirectTo({
          url: '/pages/auth/login'
        });
      }, 1500);
      return;
    }

    this.setData({
      loading: true
    });

    try {
      const isNewRole = !userRoles.includes(selectedRole);

      if (isNewRole) {
        const res = await api.selectRole({
          userId: userId,
          roleCode: selectedRole
        });

        if (res.code !== 200) {
          wx.showToast({
            title: res.message || '角色选择失败',
            icon: 'none'
          });
          this.setData({ loading: false });
          return;
        }
      }

      app.globalData.currentRole = selectedRole;
      app.globalData.userRole = selectedRole.toLowerCase();
      app.globalData.roles = isNewRole ? [...userRoles, selectedRole] : userRoles;

      this.navigateToRolePage(selectedRole);
    } catch (error) {
      console.error('角色选择失败', error);
      wx.showToast({
        title: error.message || '角色选择失败，请重试',
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
  },

  logout: function() {
    const app = getApp();
    app.logout();
  }
})