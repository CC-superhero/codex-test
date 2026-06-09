# Flash Sale Platform

一个前后端分离的秒杀电商项目，包含：

- `flash-sale-server`
  - Spring Boot 2.7
  - MyBatis-Plus
  - MySQL
  - Redis
  - RabbitMQ
  - JWT 鉴权
- `flash-sale-web-user`
  - Vue 2
  - Vant
  - 用户端秒杀、仓库、转卖市场、积分、优惠券、邀请关系
- `flash-sale-web-admin`
  - Vue 2
  - Element UI
  - 后台用户管理、商品管理、配置管理、积分与优惠券发放

## 目录结构

```text
flash-sale-platform/
├─ flash-sale-server/        后端服务
├─ flash-sale-web-user/      用户端前端
├─ flash-sale-web-admin/     管理后台前端
└─ sql/                      数据库初始化脚本
```

## 核心功能

- 秒杀商品按时间自动流转状态
- Redis 预热库存，RabbitMQ 异步处理抢购订单
- 支持邀请码注册与邀请积分奖励
- 支持已购商品上架到转卖市场
- 支持后台发放积分、优惠券和调整系统配置

## 本地运行

### 1. 初始化数据库

执行：

```sql
sql/init.sql
```

### 2. 启动后端

进入 `flash-sale-server` 后运行：

```bash
mvn spring-boot:run
```

默认配置依赖：

- MySQL: `localhost:3306/flash_sale`
- Redis: `localhost:6379`
- RabbitMQ: `localhost:5672`

### 3. 启动用户端

进入 `flash-sale-web-user` 后运行：

```bash
npm install
npm run serve
```

默认端口：`8081`

### 4. 启动管理后台

进入 `flash-sale-web-admin` 后运行：

```bash
npm install
npm run serve
```

默认端口：`8082`

## 默认信息

- 后端端口：`8080`
- 默认管理员账号：`admin`
- 默认管理员密码：`admin123`

## 说明

- 本仓库默认不提交 `node_modules`、`dist`、`target`、IDE 配置等本地产物。
- 当前项目已完成核心业务闭环，但仍建议在正式上线前补充测试、部署配置和安全加固。
