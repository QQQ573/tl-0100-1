# 暑期研学出行险 + 电子协议组合包

基于 Spring Boot 3 + React 的研学出行险与电子协议组合包演示系统。

## 功能特性

- 🛒 **产品展示**：多档位保险产品、协议产品、组合套餐展示
- 🛍️ **购物车**：支持添加、删除、修改数量
- 💳 **模拟支付**：无需真实支付通道，模拟支付流程
- 🔄 **幂等回调**：同一 out_trade_no 多次回调只处理一次
- 📄 **PDF 生成**：电子协议、保险凭证、保障徽章（iText 7）
- 👤 **学员中心**：查询保障状态，显示已保障徽章
- 🔒 **隐私保护**：身份证号脱敏展示，公开页面不显示敏感信息
- ↩️ **退订校验**：校验未开营、无私募补差在途

## 技术栈

### 后端
- Java 17 + Spring Boot 3.2.0
- Spring Data JPA + MySQL 8.0
- iText 7.2.5 (PDF 生成)
- SpringDoc OpenAPI (Swagger)
- Lombok

### 前端
- React 18 + React Router 6
- Vite 5
- Ant Design 5
- Axios
- Day.js

## 项目结构

```
tl-0100-1/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/study/tour/
│   │   │   │   ├── common/      # 通用类
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── enums/       # 枚举类
│   │   │   │   ├── repository/  # 数据访问层
│   │   │   │   └── service/     # 业务逻辑层
│   │   │   └── resources/       # 资源文件
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── pages/           # 页面组件
│   │   ├── services/        # API 服务
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── vite.config.js
│   └── package.json
├── docs/                    # 文档
│   ├── API文档.md
│   └── PDF字段说明.md
└── docker-compose.yml       # Docker Compose 配置
```

## 快速开始

### 方式一：Docker Compose（推荐）

```bash
# 克隆项目后，在根目录执行
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

访问地址：
- 前端: http://localhost:3000
- 后端 API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### 方式二：本地开发

#### 前置条件
- JDK 17+
- Node.js 18+
- MySQL 8.0+

#### 启动后端

```bash
cd backend

# 修改 application.yml 中的数据库配置

# 启动
./mvnw spring-boot:run
# 或
mvn spring-boot:run
```

#### 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

## 核心业务流程

### 1. 选购产品
- 浏览保险产品、协议产品、组合套餐
- 加入购物车

### 2. 填写信息
- 填写学员及监护人信息
- 选择开营日期

### 3. 模拟支付
- 点击支付按钮模拟支付流程
- 系统自动回调处理

### 4. 签约成功
- 生成电子协议 PDF
- 生成保险凭证 PDF
- 学员获得「已保障」徽章

### 5. 学员查询
- 输入身份证号查询保障状态
- 查看保险徽章和订单记录
- （身份证号脱敏显示）

### 6. 退订（可选）
- 校验：未开营 + 无私募补差在途
- 退订后保障状态取消

## API 接口

详细接口文档请参考 [docs/API文档.md](docs/API文档.md)

主要接口：
- `GET /api/products` - 获取产品列表
- `POST /api/orders` - 创建订单
- `GET /api/payment/mock/callback` - 模拟支付回调
- `GET /api/pdf/agreement/{orderId}` - 生成协议 PDF
- `GET /api/pdf/insurance/{orderId}` - 生成保险凭证 PDF
- `GET /api/students/{id}/public` - 获取学员公开信息

## PDF 字段说明

详细字段说明请参考 [docs/PDF字段说明.md](docs/PDF字段说明.md)

包含三种 PDF：
1. **电子协议** - 甲乙双方信息、服务内容、费用、条款
2. **保险凭证** - 被保险人信息、保险信息、保障范围
3. **保险徽章** - 学员保障状态小卡片

## 隐私保护

- 身份证号脱敏：前6后4，中间8位用 * 代替
- 公开接口不返回完整身份证号
- 学员公开信息仅包含：姓名、保障状态、徽章链接

## 注意事项

1. 本项目为演示系统，支付功能为模拟，无需真实付款
2. PDF 生成使用 iText 开源版本，商用需注意授权
3. 身份证号等敏感信息仅作演示，生产环境需加强安全防护
4. 数据初始化会自动创建 8 个演示产品

## License

MIT
