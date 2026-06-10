# AGENTS.md

## 1. 项目定位

这是一个前后端分离的秒杀电商演示项目，项目名为 `flash-sale-platform`。

当前仓库包含 4 个主要模块：

- `flash-sale-server`
  - Spring Boot 后端服务
  - 对外提供用户端与管理后台共用的 REST API
- `flash-sale-web-user`
  - Vue 2 用户端 Web
  - 面向普通用户，覆盖秒杀、仓库、转卖市场、优惠券、积分、邀请关系等能力
- `flash-sale-web-admin`
  - Vue 2 管理后台
  - 面向管理员，覆盖用户管理、商品管理、库存调拨、优惠券与积分发放、系统配置、统计面板
- `flash-sale-miniapp-user`
  - 原生微信小程序用户端
  - 复用后端用户侧接口，提供移动端轻量版本

另有：

- `sql/init.sql`
  - 数据库初始化脚本

## 2. 技术栈

### 2.1 后端

- Java 17
- Spring Boot 2.7.18
- Spring Security
- JWT
- MyBatis-Plus
- MySQL
- Redis
- RabbitMQ
- Lombok
- Knife4j

### 2.2 Web 前端

用户端 `flash-sale-web-user`：

- Vue 2
- Vue Router
- Vuex
- Vant
- Axios

管理端 `flash-sale-web-admin`：

- Vue 2
- Vue Router
- Vuex
- Element UI
- Axios

### 2.3 小程序前端

用户端 `flash-sale-miniapp-user`：

- 原生微信小程序
- 无额外 npm 依赖
- 通过 `wx.request` 直接调用后端 REST API
- 使用本地存储保存 `token` 与 `userInfo`

## 3. 目录结构

```text
flash-sale-platform/
├─ AGENTS.md
├─ README.md
├─ flash-sale-server/
├─ flash-sale-web-user/
├─ flash-sale-web-admin/
├─ flash-sale-miniapp-user/
└─ sql/
```

## 4. 核心业务

### 4.1 注册与邀请关系

- 新用户注册时必须填写上级邀请码
- 系统会为新用户生成自己的邀请码
- 后端通过 `t_invite_relation` 维护邀请链路
- 下级用户完成秒杀后，上级可获得积分奖励

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/service/UserService.java`

### 4.2 秒杀商品生命周期

- 商品存在 `startTime`、`endTime`、`status`
- `status` 语义：
  - `0` 未开始
  - `1` 进行中
  - `2` 已结束
- 项目内已补充商品生命周期服务，按时间自动刷新状态
- 商品进入进行中时会预热 Redis 库存

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/service/ProductLifecycleService.java`

### 4.3 秒杀下单链路

当前链路已不是演示壳子，而是完整闭环：

1. 用户发起秒杀请求
2. 后端校验商品状态、重复购买、积分余额、优惠券可用性
3. Redis Lua 扣减库存
4. 创建 `t_order` 订单
5. 发送 RabbitMQ 消息
6. MQ 消费端完成库存落库、秒杀记录、用户仓库、积分奖励、邀请奖励等后续动作

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/service/FlashSaleService.java`
- `flash-sale-server/src/main/java/com/flashsale/service/MqConsumer.java`

### 4.4 用户仓库与转卖市场

- 用户秒杀成功后，商品进入个人仓库
- 仓库状态：
  - `1` 在仓库
  - `2` 已上架
- 用户可把仓库商品上架到转卖市场
- 上架时会按系统配置返还手续费积分
- 其他用户可购买市场中的已上架商品
- 购买完成后，商品归属转移给买家

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/service/InventoryService.java`
- `flash-sale-web-user/src/views/Market.vue`

### 4.5 管理后台能力

当前后台已覆盖：

- 管理员登录
- 用户列表、用户详情、邀请树查看
- 封禁/解封用户
- 查看用户库存
- 库存调拨
- 商品发布、编辑、删除
- 发放积分
- 发放优惠券
- 修改系统配置
- 仪表盘统计

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/controller/admin/`
- `flash-sale-web-admin/src/views/`

## 5. 小程序模块说明

### 5.1 模块定位

`flash-sale-miniapp-user` 是用户端的小程序版本，目标是提供更轻量的移动访问入口，不处理支付链路，重点覆盖用户核心路径。

### 5.2 已实现页面

- 首页：商品列表、状态展示、跳转详情
- 登录：昵称登录
- 注册：邀请码注册，可补充实名、手机号、地址、银行卡、身份证信息
- 商品详情：查看商品并发起秒杀，可选填写 `couponId`、`pointsUsed`
- 转卖市场：查看在售商品并购买
- 我的：用户资料摘要、仓库/优惠券/积分/邀请入口、退出登录
- 我的仓库：查看个人仓库商品并跳转上架
- 卖家仓库：查看已上架商品并下架
- 上架商品：把仓库商品上架到转卖市场
- 我的优惠券
- 积分记录
- 邀请中心

### 5.3 关键文件

- `flash-sale-miniapp-user/app.json`
  - 页面注册、TabBar、全局窗口配置
- `flash-sale-miniapp-user/utils/request.js`
  - 请求封装、Token 注入、401 处理、错误信息归一化
- `flash-sale-miniapp-user/utils/auth.js`
  - 登录态读写与登录跳转
- `flash-sale-miniapp-user/api/*.js`
  - 用户端接口封装
- `flash-sale-miniapp-user/pages/*`
  - 页面实现

### 5.4 小程序接口映射

复用的主要后端接口：

- `POST /api/user/login`
- `POST /api/user/register`
- `GET /api/user/info`
- `GET /api/user/product/list`
- `GET /api/user/product/{id}`
- `POST /api/user/flash/buy`
- `GET /api/user/inventory/list`
- `GET /api/user/inventory/seller`
- `POST /api/user/inventory/list`
- `DELETE /api/user/inventory/list/{id}`
- `GET /api/user/market/list`
- `POST /api/user/market/buy/{id}`
- `GET /api/user/coupon/list`
- `GET /api/user/points/log`
- `GET /api/user/invite/info`

### 5.5 小程序运行注意事项

- 当前 `utils/request.js` 默认接口地址是 `http://127.0.0.1:8080/api`
- 该地址适合本地联调或微信开发者工具关闭域名校验时使用
- 真机调试或正式发布前，必须改成可访问的服务地址，并在微信公众平台配置合法 request 域名
- 当前 `project.config.json` 已关闭 `urlCheck`，便于本地开发
- `project.private.config.json` 属于本地私有配置，已加入忽略规则，不应提交到 Git

## 6. 数据库核心表

初始化脚本：

- `sql/init.sql`

主要表：

- `t_user`
- `t_product`
- `t_order`
- `t_user_inventory`
- `t_coupon`
- `t_points_log`
- `t_invite_relation`
- `t_flash_record`
- `t_config`
- `t_admin`

## 7. 默认运行配置

后端配置文件：

- `flash-sale-server/src/main/resources/application.yml`

默认端口：

- 后端：`8080`
- 用户 Web：`8081`
- 管理后台：`8082`

默认依赖：

- MySQL：`localhost:3306/flash_sale`
- Redis：`localhost:6379`
- RabbitMQ：`localhost:5672`

默认管理员：

- 用户名：`admin`
- 密码：`admin123`

## 8. 本地启动方式

### 8.1 后端

在 `flash-sale-server` 目录执行：

```bash
mvn spring-boot:run
```

如需仅编译校验：

```bash
mvn -DskipTests compile
```

注意：

- Maven 需要运行在 JDK 17
- 这台机器历史上出现过 `mvn` 误用 JDK 8 的情况，若再出现 `--release` 报错，优先检查 `JAVA_HOME`

### 8.2 用户 Web

在 `flash-sale-web-user` 目录执行：

```bash
npm install
npm run serve
```

### 8.3 管理后台

在 `flash-sale-web-admin` 目录执行：

```bash
npm install
npm run serve
```

### 8.4 微信小程序

使用微信开发者工具打开：

- `flash-sale-miniapp-user`

联调前需要确认：

- 后端接口可访问
- 小程序请求地址与后端地址一致
- 域名校验设置符合当前开发环境

## 9. 当前完成度判断

### 9.1 已基本成型的部分

- 后端主业务链路已经闭环
- Web 用户端核心流程已具备
- 管理后台已覆盖主要管理动作
- 小程序用户端已补出一套基础可用版本
- GitHub 仓库与 `develop`、`test` 分支已建立

### 9.2 仍需注意的部分

- 支付仍是非重点项，当前不作为推进核心
- 小程序版本已完成静态实现，但尚未在微信开发者工具做完整真机链路验证
- 若后续要上线，需要补充接口域名、环境配置、联调与体验优化

## 10. 协作注意事项

- 不要提交 `node_modules/`、`dist/`、`target/`、`.idea/`、`*.iml`
- 不要提交微信开发者工具的私有配置文件 `project.private.config.json`
- 修改小程序请求地址时，优先同步更新本文档
- 如果继续扩展小程序，优先复用现有 `api/` 与 `utils/request.js`，不要在页面内直接散写请求逻辑
