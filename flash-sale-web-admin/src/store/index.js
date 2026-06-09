import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const auth = {
  state: {
    token: localStorage.getItem('admin_token') || '',
    userInfo: JSON.parse(localStorage.getItem('admin_info') || '{}')
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('admin_token', token)
    },
    SET_USER_INFO(state, info) {
      state.userInfo = info
      localStorage.setItem('admin_info', JSON.stringify(info))
    },
    LOGOUT(state) {
      state.token = ''
      state.userInfo = {}
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_info')
    }
  },
  actions: {
    login({ commit }, { token, userInfo }) {
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', userInfo)
    },
    logout({ commit }) {
      commit('LOGOUT')
    }
  }
}

const app = {
  state: {
    sidebar: {
      collapsed: false
    }
  },
  mutations: {
    TOGGLE_SIDEBAR(state) {
      state.sidebar.collapsed = !state.sidebar.collapsed
    }
  }
}

const tagsView = {
  state: {
    visitedViews: []
  },
  mutations: {
    ADD_VISITED_VIEW(state, view) {
      if (state.visitedViews.some(v => v.path === view.path)) return
      state.visitedViews.push({ ...view })
    },
    DEL_VISITED_VIEW(state, view) {
      const i = state.visitedViews.findIndex(v => v.path === view.path)
      if (i > -1) state.visitedViews.splice(i, 1)
    },
    DEL_OTHERS_VISITED_VIEWS(state, view) {
      state.visitedViews = state.visitedViews.filter(
        v => v.path === view.path || v.path === '/dashboard'
      )
    },
    DEL_ALL_VISITED_VIEWS(state) {
      state.visitedViews = state.visitedViews.filter(v => v.path === '/dashboard')
    }
  },
  actions: {
    addView({ commit }, view) {
      commit('ADD_VISITED_VIEW', view)
    },
    delView({ commit, state }, view) {
      return new Promise(resolve => {
        commit('DEL_VISITED_VIEW', view)
        resolve([...state.visitedViews])
      })
    },
    delOthersViews({ commit, state }, view) {
      return new Promise(resolve => {
        commit('DEL_OTHERS_VISITED_VIEWS', view)
        resolve([...state.visitedViews])
      })
    },
    delAllViews({ commit, state }) {
      return new Promise(resolve => {
        commit('DEL_ALL_VISITED_VIEWS')
        resolve([...state.visitedViews])
      })
    }
  }
}

export default new Vuex.Store({
  modules: { auth, app, tagsView },
  getters: {
    token: state => state.auth.token,
    userInfo: state => state.auth.userInfo,
    sidebarCollapsed: state => state.app.sidebar.collapsed,
    visitedViews: state => state.tagsView.visitedViews
  }
})
