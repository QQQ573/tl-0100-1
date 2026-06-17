# 暑期研学出行险 + 电子协议组合包 - API 接口文档

## 基础信息

- **服务名称**: 暑期研学出行险组合包服务
- **版本**: v1.0.0
- **基础路径**: `/api`
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

所有接口均返回统一的响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200 表示成功，其他表示失败 |
| message | string | 响应消息 |
| data | object | 响应数据 |

---

## 一、产品管理

### 1.1 获取所有产品

**接口**: `GET /api/products`

**描述**: 获取所有在售产品列表

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "基础版研学出行险",
      "description": "涵盖基本意外保障...",
      "price": 29.90,
      "type": "INSURANCE",
      "level": "BASIC",
      "status": "ACTIVE"
    }
  ]
}
```

### 1.2 按类型获取产品

**接口**: `GET /api/products/type/{type}`

**描述**: 按产品类型获取产品列表

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 是 | 产品类型：INSURANCE(保险)/AGREEMENT(协议)/PACKAGE(组合包) |

### 1.3 获取产品详情

**接口**: `GET /api/products/{id}`

**描述**: 根据产品ID获取产品详情

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 产品ID |

---

## 二、订单管理

### 2.1 创建订单

**接口**: `POST /api/orders`

**描述**: 创建研学保险+协议组合订单

**请求参数**:
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 1
    }
  ],
  "studentName": "张三",
  "studentIdCard": "110101201001011234",
  "studentPhone": "13800138000",
  "studentEmail": "zhangsan@example.com",
  "guardianName": "张父",
  "guardianIdCard": "110101198001011234",
  "guardianPhone": "13900139000",
  "campStartDate": "2026-07-15"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| items | array | 是 | 订单项列表 |
| items[].productId | long | 是 | 产品ID |
| items[].quantity | int | 是 | 数量 |
| studentName | string | 是 | 学员姓名 |
| studentIdCard | string | 是 | 学员身份证号 |
| studentPhone | string | 否 | 学员电话 |
| studentEmail | string | 否 | 学员邮箱 |
| guardianName | string | 是 | 监护人姓名 |
| guardianIdCard | string | 是 | 监护人身份证号 |
| guardianPhone | string | 是 | 监护人电话 |
| campStartDate | date | 是 | 开营日期 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "outTradeNo": "ST1234567890ABCDEF",
    "totalAmount": 59.90,
    "status": "PENDING",
    "studentId": 1,
    "guardianName": "张父",
    "guardianPhone": "13900139000",
    "createdAt": "2026-06-17T10:00:00",
    "items": [...]
  }
}
```

### 2.2 获取订单详情

**接口**: `GET /api/orders/{id}`

**描述**: 根据订单ID获取订单详情

### 2.3 根据订单号获取订单

**接口**: `GET /api/orders/outTradeNo/{outTradeNo}`

**描述**: 根据外部订单号获取订单详情

### 2.4 取消订单

**接口**: `PUT /api/orders/{id}/cancel`

**描述**: 取消待支付订单

**限制**: 只有 PENDING 状态的订单可以取消

### 2.5 退订订单

**接口**: `PUT /api/orders/{id}/refund`

**描述**: 退订已支付订单

**校验规则**:
- 订单必须是 PAID 状态
- 开营日期必须晚于当前日期（未开营）
- 无私募补差在途

---

## 三、支付管理

### 3.1 模拟支付

**接口**: `GET /api/payment/mock/pay/{outTradeNo}`

**描述**: 获取模拟支付链接（无需真实支付通道）

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| outTradeNo | string | 是 | 外部订单号 |

### 3.2 模拟支付回调

**接口**: `GET /api/payment/mock/callback`

**描述**: 模拟支付回调接口，用于测试支付成功流程

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| outTradeNo | string | 是 | 外部订单号 |
| tradeStatus | string | 是 | 交易状态：SUCCESS/FAILED |
| amount | string | 否 | 支付金额 |

**幂等说明**: 同一 out_trade_no 多次回调只会处理一次成功支付。

### 3.3 支付回调（正式）

**接口**: `POST /api/payment/callback`

**描述**: 支付渠道回调接口（幂等）

**请求体**:
```json
{
  "outTradeNo": "ST1234567890ABCDEF",
  "amount": "59.90",
  "tradeStatus": "SUCCESS",
  "tradeNo": "PAY202606170001",
  "sign": "xxxxxx"
}
```

**幂等保证**: 使用 out_trade_no + SUCCESS 状态作为唯一约束，重复回调返回 success 但不重复处理。

---

## 四、PDF 生成

### 4.1 生成电子协议 PDF

**接口**: `GET /api/pdf/agreement/{orderId}`

**描述**: 生成并下载电子协议 PDF

**响应**: application/pdf 二进制流

### 4.2 生成保险凭证 PDF

**接口**: `GET /api/pdf/insurance/{orderId}`

**描述**: 生成并下载保险凭证 PDF

**响应**: application/pdf 二进制流

### 4.3 生成保险徽章 PDF

**接口**: `GET /api/pdf/insurance-badge/{orderId}`

**描述**: 生成学员保障徽章（小卡片）

**响应**: application/pdf 二进制流

---

## 五、学员管理

### 5.1 获取学员详情

**接口**: `GET /api/students/{id}`

**描述**: 获取学员完整信息

### 5.2 根据身份证号获取学员

**接口**: `GET /api/students/idCard/{idCard}`

**描述**: 根据身份证号查询学员

### 5.3 获取学员公开信息

**接口**: `GET /api/students/{id}/public`

**描述**: 获取学员公开信息（不显示身份证号等敏感信息）

**返回字段**: id, name, hasInsurance, insuranceBadgeUrl

**隐私保护**: 公开接口不返回监护人身份证号等敏感信息。

---

## 六、数据字典

### 6.1 产品类型 (ProductType)
| 值 | 说明 |
|----|------|
| INSURANCE | 保险产品 |
| AGREEMENT | 协议产品 |
| PACKAGE | 组合包 |

### 6.2 产品级别 (ProductLevel)
| 值 | 说明 |
|----|------|
| BASIC | 基础版 |
| STANDARD | 标准版 |
| PREMIUM | 尊享版 |

### 6.3 订单状态 (OrderStatus)
| 值 | 说明 |
|----|------|
| PENDING | 待支付 |
| PAID | 已支付 |
| CANCELLED | 已取消 |
| REFUNDED | 已退款 |

### 6.4 支付状态 (PaymentStatus)
| 值 | 说明 |
|----|------|
| SUCCESS | 支付成功 |
| FAILED | 支付失败 |

---

## 七、错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 500 | 系统错误 |

---

## 八、Swagger UI

启动服务后可访问 Swagger UI 进行接口调试：

- **地址**: `http://localhost:8080/swagger-ui.html`
- **API 文档**: `http://localhost:8080/api-docs`
