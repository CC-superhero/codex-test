# AGENTS.md

## 1. 项目定位

这是一个前后端分离的秒杀电商项目，项目名为 `flash-sale-platform`。

项目包含三部分：

- `flash-sale-server`
  - 后端服务
  - 提供用户端和管理后台共用的 REST API
- `flash-sale-web-user`
  - 用户端前端
  - 面向普通用户，包含秒杀、仓库、转卖市场、积分、优惠券、邀请关系
- `flash-sale-web-admin`
  - 管理后台前端
  - 面向管理员，包含用户管理、商品管理、库存调拨、积分与优惠券发放、系统配置

另有：

- `sql/init.sql`
  - 数据库初始化脚本

## 2. 技术栈

### 后端

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

### 前端

用户端：

- Vue 2
- Vue Router
- Vuex
- Vant
- Axios

管理后台：

- Vue 2
- Vue Router
- Vuex
- Element UI
- Axios

## 3. 目录结构

```text
flash-sale-platform/
├─ AGENTS.md
├─ README.md
├─ flash-sale-server/
│  ├─ pom.xml
│  └─ src/main/java/com/flashsale/
├─ flash-sale-web-user/
│  ├─ package.json
│  └─ src/
├─ flash-sale-web-admin/
│  ├─ package.json
│  └─ src/
└─ sql/
   └─ init.sql
```

## 4. 核心业务

### 4.1 用户注册与邀请关系

- 用户注册时必须填写上级邀请码
- 系统会为新用户生成自己的邀请码
- 后端会建立邀请关系表 `t_invite_relation`
- 后续下级用户秒杀成功时，上级会获得积分奖励

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/service/UserService.java`

### 4.2 秒杀商品生命周期

- 商品有 `startTime`、`endTime`、`status`
- `status` 语义：
  - `0` 未开始
  - `1` 进行中
  - `2` 已结束
- 现在项目中已经补了一个商品生命周期服务，会按时间刷新商品状态
- 商品进入进行中状态时，会预热 Redis 库存

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/service/ProductLifecycleService.java`

### 4.3 秒杀下单链路

当前链路已经是闭环，不再只是演示接口：

1. 用户发起秒杀请求
2. 后端校验商品状态、重复购买、积分余额、优惠券状态
3. Redis Lua 扣减秒杀库存
4. 创建 `t_order` 订单记录
5. 发送 RabbitMQ 消息
6. MQ 消费端落库存、生成秒杀记录、生成用户仓库、发放积分和邀请奖励

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/service/FlashSaleService.java`
- `flash-sale-server/src/main/java/com/flashsale/service/MqConsumer.java`

### 4.4 用户仓库与转卖市场

项目支持用户将已购商品上架到转卖市场：

- 仓库状态：
  - `1` 在仓库
  - `2` 已上架
- 用户可以把自己仓库中的商品上架
- 上架时会按系统配置返还手续费积分
- 其他用户可以在转卖市场购买已上架商品
- 购买后库存归属会转移到买家

关键代码：

- `flash-sale-server/src/main/java/com/flashsale/service/InventoryService.java`
- `flash-sale-web-user/src/views/Market.vue`

### 4.5 管理后台

后台当前支持：

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

## 5. 数据库核心表

初始化脚本在：

- `sql/init.sql`

主要表：

- `t_user`
  - 用户
- `t_product`
  - 秒杀商品
- `t_order`
  - 订单
- `t_user_inventory`
  - 用户仓库
- `t_coupon`
  - 优惠券
- `t_points_log`
  - 积分记录
- `t_invite_relation`
  - 邀请关系
- `t_flash_record`
  - 秒杀记录
- `t_config`
  - 系统配置
- `t_admin`
  - 管理员

## 6. 默认运行配置

后端配置文件：

- `flash-sale-server/src/main/resources/application.yml`

默认端口：

- 后端：`8080`
- 用户端：`8081`
- 管理后台：`8082`

默认依赖：

- MySQL：`localhost:3306/flash_sale`
- Redis：`localhost:6379`
- RabbitMQ：`localhost:5672`

默认管理员：

- 用户名：`admin`
- 密码：`admin123`

## 7. 本地启动方式

### 后端

在 `flash-sale-server` 下运行：

```bash
mvn spring-boot:run
```

如果需要编译验证：

```bash
mvn -DskipTests compile
```

注意：

- Maven 必须跑在 JDK 17 下
- 这台机器之前存在 `mvn` 使用 JDK 8 的情况，编译会报 `--release` 错误

### 用户端

在 `flash-sale-web-user` 下运行：

```bash
npm install
npm run serve
```

### 管理后台

在 `flash-sale-web-admin` 下运行：

```bash
npm install
npm run serve
```

## 8. 当前已知状态

### 已确认可用

- 后端在切换到 JDK 17 后可以编译通过
- 管理后台生产构建已通过
- Git 仓库已初始化
- 当前远程仓库已绑定 GitHub
- 已存在远程分支：
  - `develop`
  - `test`

### 已知限制

- 用户端在当前工作区里没有现成的 `node_modules`
- 因此用户端未在本地完成一次完整构建验证
- 但关键页面的模板语法和明显坏行已经修过

### 支付说明

- 当前支付仍然是模拟支付
- 用户要求中明确说过“支付不用管”
- 因此后续如无明确要求，不需要优先动支付链路

## 9. 已做过的重要修复

本仓库当前已经不是原始版本，已经做过这些补强：

- 修复部分实体类中的坏行和缺失字段
- 增加商品状态自动流转服务
- 秒杀时补上订单表落库
- 秒杀时补上积分校验和优惠券使用
- 增加转卖市场购买接口和页面
- 增加管理后台仪表盘统计接口
- 重写了多处受乱码影响的前端页面
- 清理了误生成的空目录
- 配置了 `.gitignore` 和 `.gitattributes`

## 10. 协作注意事项

### 提交代码时不要带入这些内容

- `node_modules/`
- `dist/`
- `target/`
- `.idea/`
- `*.iml`

### 修改前端时

- 用户端基于 Vue 2 + Vant
- 后台基于 Vue 2 + Element UI
- 很多页面原始版本存在乱码遗留，改动时要优先检查模板标签和引号是否完整

### 修改后端时

- 秒杀主链路优先看 `FlashSaleService`
- 商品状态优先看 `ProductLifecycleService`
- 仓库与转卖逻辑优先看 `InventoryService`
- 管理端统计优先看 `AdminDashboardController`

### 涉及环境问题时

- 如果 Maven 报 `invalid flag: --release`，优先检查 `mvn -version`
- 如果 Git 推送报认证失败，当前项目远程是 GitHub，不是 Gitee

## 11. 建议后续优先级

如果继续开发，建议按这个顺序：

1. 给用户端补完整构建验证
2. 为后端核心业务补测试
3. 增加异常补偿和更严格的数据一致性保护
4. 优化前端国际化/编码问题
5. 最后再考虑真实支付接入
