import { useState, useEffect } from 'react'
import { Card, Button, message, Steps, Spin, Space, Typography } from 'antd'
import { CheckCircleOutlined, LoadingOutlined } from '@ant-design/icons'
import { useParams, useNavigate } from 'react-router-dom'
import { paymentApi, orderApi } from '../services/api'

const { Step } = Steps
const { Title, Text } = Typography

function PaymentPage() {
  const { outTradeNo } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [order, setOrder] = useState(null)
  const [paying, setPaying] = useState(false)

  useEffect(() => {
    loadOrder()
  }, [outTradeNo])

  const loadOrder = async () => {
    try {
      const data = await orderApi.getOrderByOutTradeNo(outTradeNo)
      setOrder(data)
    } catch (e) {
      message.error('加载订单失败')
    }
  }

  const handlePay = async () => {
    setPaying(true)
    setLoading(true)
    try {
      setTimeout(async () => {
        try {
          await paymentApi.mockCallback({
            outTradeNo,
            tradeStatus: 'SUCCESS',
          })
          message.success('支付成功！')
          setTimeout(() => {
            navigate(`/order-success?outTradeNo=${outTradeNo}`)
          }, 1000)
        } catch (e) {
          message.error(e.message || '支付失败')
          setLoading(false)
          setPaying(false)
        }
      }, 2000)
    } catch (e) {
      message.error('支付失败')
      setLoading(false)
      setPaying(false)
    }
  }

  if (!order) {
    return (
      <div style={{ textAlign: 'center', padding: '100px' }}>
        <Spin />
      </div>
    )
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>💳 确认支付</h1>
        <p>请确认订单信息并完成支付</p>
      </div>

      <Steps current={2} style={{ marginBottom: '30px', maxWidth: '600px', margin: '0 auto 30px' }}>
        <Step title="选购产品" />
        <Step title="填写信息" />
        <Step title="支付" />
        <Step title="完成" />
      </Steps>

      <Card style={{ maxWidth: '600px', margin: '0 auto' }}>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div style={{ textAlign: 'center', padding: '30px 0' }}>
            <Title level={3}>订单金额</Title>
            <div className="price-tag" style={{ fontSize: '48px', marginTop: '10px' }}>
              {order.totalAmount}
            </div>
            <Text type="secondary">订单号：{order.outTradeNo}</Text>
          </div>

          <div style={{ padding: '20px', background: '#f5f5f5', borderRadius: '8px' }}>
            <Title level={5}>订单商品</Title>
            {order.items?.map((item) => (
              <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0' }}>
                <span>{item.productName} x{item.quantity}</span>
                <span>¥{item.price}</span>
              </div>
            ))}
          </div>

          <div style={{ padding: '20px', background: '#e6f7ff', borderRadius: '8px' }}>
            <p style={{ marginBottom: '8px' }}>
              <CheckCircleOutlined style={{ color: '#52c41a' }} /> 模拟支付环境
            </p>
            <p style={{ color: '#666', fontSize: '13px', margin: 0 }}>
              本系统为演示环境，点击下方按钮模拟支付成功，无需真实付款。
            </p>
          </div>

          {order.status === 'PENDING' && (
            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
              <Button
                type="primary"
                size="large"
                block
                loading={loading}
                onClick={handlePay}
              >
                {paying ? (
                  <span>
                    <LoadingOutlined /> 支付处理中...
                  </span>
                ) : (
                  '模拟支付 ¥' + order.totalAmount
                )}
              </Button>
              <Button size="large" block onClick={() => navigate('/')}>
                继续购物
              </Button>
            </Space>
          )}
          {order.status === 'PAID' && (
            <Space size="middle" style={{ width: '100%', justifyContent: 'center' }}>
              <Button size="large" onClick={() => navigate('/')}>
                继续购物
              </Button>
              <Button type="primary" size="large" onClick={() => navigate(`/order-success?outTradeNo=${outTradeNo}`)}>
                查看订单
              </Button>
            </Space>
          )}
        </Space>
      </Card>
    </div>
  )
}

export default PaymentPage
