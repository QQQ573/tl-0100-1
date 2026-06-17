import { Table, Button, InputNumber, Card, Typography, Space, Divider, Empty, Row, Col } from 'antd'
import { DeleteOutlined, ShoppingCartOutlined, ArrowRightOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

const { Title } = Typography

function CartPage({ cartItems, updateQuantity, removeFromCart }) {
  const navigate = useNavigate()

  const totalAmount = cartItems.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  )

  const columns = [
    {
      title: '产品名称',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <Space>
          <span style={{ fontSize: '20px' }}>
            {record.type === 'INSURANCE' ? '🛡️' : record.type === 'AGREEMENT' ? '📄' : '🎁'}
          </span>
          <span>{text}</span>
        </Space>
      ),
    },
    {
      title: '单价',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      render: (price) => <span className="price-tag" style={{ fontSize: '16px' }}>{price}</span>,
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 150,
      render: (qty, record) => (
        <InputNumber
          min={1}
          max={99}
          value={qty}
          onChange={(value) => updateQuantity(record.id, value)}
        />
      ),
    },
    {
      title: '小计',
      key: 'subtotal',
      width: 120,
      render: (_, record) => (
        <span className="price-tag" style={{ fontSize: '16px' }}>
          {(record.price * record.quantity).toFixed(2)}
        </span>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_, record) => (
        <Button
          type="text"
          danger
          icon={<DeleteOutlined />}
          onClick={() => removeFromCart(record.id)}
        />
      ),
    },
  ]

  const handleCheckout = () => {
    if (cartItems.length === 0) return
    navigate('/checkout')
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>🛒 购物车</h1>
        <p>确认您选购的研学保障产品</p>
      </div>

      {cartItems.length === 0 ? (
        <Empty
          description="购物车是空的"
          style={{ padding: '60px 0' }}
        >
          <Button type="primary" onClick={() => navigate('/')}>
            去选购
          </Button>
        </Empty>
      ) : (
        <Row gutter={24}>
          <Col span={16}>
            <Card>
              <Table
                dataSource={cartItems}
                columns={columns}
                rowKey="id"
                pagination={false}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card title="订单汇总">
              <Space direction="vertical" size="large" style={{ width: '100%' }}>
                <div>
                  <Space>
                    <span style={{ color: '#666' }}>商品数量：</span>
                    <span>{cartItems.reduce((sum, item) => sum + item.quantity, 0)} 件</span>
                  </Space>
                </div>
                <Divider />
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '16px' }}>应付总额：</span>
                  <span className="price-tag" style={{ fontSize: '28px' }}>
                    {totalAmount.toFixed(2)}
                  </span>
                </div>
                <Button
                  type="primary"
                  size="large"
                  block
                  icon={<ArrowRightOutlined />}
                  onClick={handleCheckout}
                >
                  去结算
                </Button>
                <Button
                  type="default"
                  block
                  onClick={() => navigate('/')}
                >
                  继续购物
                </Button>
              </Space>
            </Card>
          </Col>
        </Row>
      )}
    </div>
  )
}

export default CartPage
