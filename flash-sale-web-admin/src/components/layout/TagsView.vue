<template>
  <div class="tags-view-container">
    <div class="tags-view-wrapper" ref="scrollPane">
      <span
        v-for="tag in visitedViews"
        :key="tag.path"
        :class="['tags-view-item', { active: isActive(tag) }]"
        @click="navigateTo(tag)"
        @contextmenu.prevent.native="openMenu(tag, $event)"
      >
        {{ tag.title }}
        <i
          v-if="!isAffix(tag)"
          class="el-icon-close"
          @click.prevent.stop="closeTag(tag)"
        />
      </span>
    </div>
    <ul
      v-show="menuVisible"
      :style="{ left: menuLeft + 'px', top: menuTop + 'px' }"
      class="contextmenu"
    >
      <li @click="closeSelectedTag">关闭当前</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags">关闭所有</li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'TagsView',
  data() {
    return {
      menuVisible: false,
      menuLeft: 0,
      menuTop: 0,
      selectedTag: null
    }
  },
  computed: {
    visitedViews() {
      return this.$store.getters.visitedViews
    }
  },
  mounted() {
    document.addEventListener('click', this.hideMenu)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.hideMenu)
  },
  methods: {
    isActive(tag) {
      return tag.path === this.$route.path
    },
    isAffix(tag) {
      return tag.path === '/dashboard'
    },
    navigateTo(tag) {
      this.$router.push(tag.path)
    },
    closeTag(tag) {
      this.$store.dispatch('delView', tag).then(views => {
        if (this.isActive(tag)) {
          const last = views[views.length - 1]
          this.$router.push(last ? last.path : '/dashboard')
        }
      })
    },
    openMenu(tag, e) {
      this.menuVisible = true
      this.menuLeft = e.clientX
      this.menuTop = e.clientY
      this.selectedTag = tag
    },
    hideMenu() {
      this.menuVisible = false
    },
    closeSelectedTag() {
      if (this.selectedTag && !this.isAffix(this.selectedTag)) {
        this.closeTag(this.selectedTag)
      }
      this.menuVisible = false
    },
    closeOthersTags() {
      this.$store.dispatch('delOthersViews', this.selectedTag).then(views => {
        if (!views.some(v => v.path === this.$route.path)) {
          const last = views[views.length - 1]
          this.$router.push(last ? last.path : '/dashboard')
        }
      })
      this.menuVisible = false
    },
    closeAllTags() {
      this.$store.dispatch('delAllViews').then(views => {
        const last = views[views.length - 1]
        this.$router.push(last ? last.path : '/dashboard')
      })
      this.menuVisible = false
    }
  }
}
</script>

<style scoped>
.tags-view-container {
  height: 34px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  flex-shrink: 0;
}
.tags-view-wrapper {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 8px;
  overflow-x: auto;
  white-space: nowrap;
}
.tags-view-wrapper::-webkit-scrollbar {
  display: none;
}
.tags-view-item {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  margin: 0 2px;
  font-size: 12px;
  color: #909399;
  background: #f4f4f5;
  border: 1px solid #e9e9eb;
  border-radius: 2px;
  cursor: pointer;
  user-select: none;
}
.tags-view-item.active {
  color: #fff;
  background: #409EFF;
  border-color: #409EFF;
}
.tags-view-item:hover:not(.active) {
  color: #409EFF;
}
.tags-view-item .el-icon-close {
  font-size: 10px;
  margin-left: 6px;
  border-radius: 50%;
  padding: 2px;
  transition: background 0.2s;
}
.tags-view-item .el-icon-close:hover {
  background: rgba(0,0,0,0.15);
}
.contextmenu {
  position: fixed;
  z-index: 3000;
  min-width: 120px;
  margin: 0;
  padding: 5px 0;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.12);
  list-style: none;
}
.contextmenu li {
  padding: 8px 20px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
}
.contextmenu li:hover {
  background: #ecf5ff;
  color: #409EFF;
}
</style>
