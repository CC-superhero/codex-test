const { getPointsLog } = require('../../api/coupon')
const { requireAuth } = require('../../utils/auth')

const TYPE_MAP = {
  1: '秒杀奖励',
  2: '邀请奖励',
  3: '上架返还',
  4: '后台发放或积分抵扣'
}

Page({
  data: {
    list: []
  },
  onShow() {
    if (!requireAuth()) return
    this.fetchList()
  },
  async fetchList() {
    try {
      const res = await getPointsLog()
      const list = (res.data || []).map((item) => ({
        ...item,
        typeText: item.remark || TYPE_MAP[item.type] || '积分变动'
      }))
      this.setData({ list })
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '积分记录加载失败',
        icon: 'none'
      })
    }
  }
})
