import { useState, useEffect } from 'react'
import { Row, Col, Card, Button, Tag, message } from 'antd'
import { PlusOutlined, SafetyOutlined, FileTextOutlined, GiftOutlined } from '@ant-design/icons'
import { productApi } from '../services/api'

const typeIcons = {
  INSURANCE: <SafetyOutlined style={{ color: '#52c41a', fontSize: '24px' }} />,
  AGREEMENT: <FileTextOutlined style={{ color: '#1890ff', fontSize: '24px' }} />,
  PACKAGE: <GiftOutlined style={{ color: '#fa8c16', fontSize: '24px' }} />,
}

const typeNames = {
  INSURANCE: '保险产品',
  AGREEMENT: '协议产品',
  PACKAGE: '组合套餐',
}

const levelColors = {
  BASIC: 'default',
  STANDARD: 'blue',
  PREMIUM: 'gold',
}

const levelNames = {
  BASIC: '基础版',
  STANDARD: '标准版',
  PREMIUM: '尊享版',
}

function HomePage({ addToCart }) {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadProducts()
  }, [])

  const loadProducts = async () => {
    setLoading(true)
    try {
      const data = await productApi.getAllProducts()
      setProducts(data)
    } catch (e) {
      message.error('加载产品失败')
    } finally {
      setLoading(false)
    }
  }

  const handleAddToCart = (product) => {
    addToCart(product)
    message.success(`已添加 ${product.name} 到购物车`)
  }

  const groupedProducts = {
    PACKAGE: products.filter((p) => p.type === 'PACKAGE'),
    INSURANCE: products.filter((p) => p.type === 'INSURANCE'),
    AGREEMENT: products.filter((p) => p.type === 'AGREEMENT'),
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>🏕️ 暑期研学出行保障</h1>
        <p>一站式选购研学出行险与电子协议，为孩子的暑期研学之旅保驾护航</p>
      </div>

      {Object.entries(groupedProducts).map(([type, list]) => (
        list.length > 0 && (
          <div key={type}>
            <h2 className="section-title">
              {typeIcons[type]} {typeNames[type]}
            </h2>
            <Row gutter={[24, 24]}>
              {list.map((product) => (
                <Col xs={24} sm={12} md={8} key={product.id}>
                  <Card
                    className="card-hover"
                    actions={[
                      <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={() => handleAddToCart(product)}
                        block
                      >
                        加入购物车
                      </Button>,
                    ]}
                  >
                    <Card.Meta
                      title={
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                          {typeIcons[product.type]}
                          <span>{product.name}</span>
                        </div>
                      }
                      description={
                        <div style={{ marginTop: '12px' }}>
                          <Tag color={levelColors[product.level]}>
                            {levelNames[product.level]}
                          </Tag>
                          <p style={{ marginTop: '8px', color: '#666', fontSize: '13px', minHeight: '40px' }}>
                            {product.description}
                          </p>
                          <div className="price-tag" style={{ marginTop: '12px' }}>
                            {product.price}
                          </div>
                        </div>
                      }
                    />
                  </Card>
                </Col>
              ))}
            </Row>
          </div>
        )
      ))}

      <div style={{ marginTop: '40px', padding: '30px', background: '#e6f7ff', borderRadius: '8px' }}>
        <h3 style={{ color: '#1890ff' }}>💡 为什么选择我们？</h3>
        <Row gutter={[24, 16]} style={{ marginTop: '16px' }}>
          <Col span={8}>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '32px' }}>🛡️</div>
              <h4>全面保障</h4>
              <p style={{ color: '#666', fontSize: '13px' }}>涵盖意外、医疗、救援等多重保障</p>
            </div>
          </Col>
          <Col span={8}>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '32px' }}>📄</div>
              <h4>电子协议</h4>
              <p style={{ color: '#666', fontSize: '13px' }}>在线签约，方便快捷，安全可靠</p>
            </div>
          </Col>
          <Col span={8}>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '32px' }}>⚡</div>
              <h4>即时生效</h4>
              <p style={{ color: '#666', fontSize: '13px' }}>支付成功后立即生成保险凭证</p>
            </div>
          </Col>
        </Row>
      </div>
    </div>
  )
}

export default HomePage
