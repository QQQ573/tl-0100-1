import { useState, useEffect } from 'react'
import { Card, Button, Steps, message, Space, Typography, Row, Col, Tag, Spin } from 'antd'
import { CheckCircleOutlined, FilePdfOutlined, SafetyOutlined, HomeOutlined } from '@ant-design/icons'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { orderApi, pdfApi } from '../services/api'

const { Step } = Steps
const { Title, Text } = Typography

function OrderSuccessPage() {
  const [searchParams] = useSearchParams()
  const outTradeNo = searchParams.get('outTradeNo')
  const navigate = useNavigate()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (outTradeNo) {
      loadOrder()
    }
  }, [outTradeNo])

  const loadOrder = async () => {
    setLoading(true)
    try {
      const data = await orderApi.getOrderByOutTradeNo(outTradeNo)
      setOrder(data)
    } catch (e) {
      message.error('加载订单失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px' }}>
        <Spin />
      </div>
    )
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>🎉 支付成功</h1>
        <p>您的研学保障已生效，祝您研学愉快！</p>
      </div>

      <Steps current={3} style={{ marginBottom: '30px', maxWidth: '600px', margin: '0 auto 30px' }}>
        <Step title="选购产品" />
        <Step title="填写信息" />
        <Step title="支付" />
        <Step title="完成" />
      </Steps>

      <Card style={{ maxWidth: '800px', margin: '0 auto' }}>
        <div style={{ textAlign: 'center', padding: '20px 0 30px' }}>
          <CheckCircleOutlined style={{ fontSize: '64px', color: '#52c41a' }} />
          <Title level={3} style={{ marginTop: '16px', color: '#52c41a' }}>
            签约成功
          </Title>
          <Tag color="green" icon={<SafetyOutlined />}>
            保险保障已生效
          </Tag>
        </div>

        <Row gutter={16} style={{ marginBottom: '24px' }}>
          <Col span={12}>
            <Card size="small" title="订单信息">
              <Space direction="vertical" size="small">
                <div>
                  <Text type="secondary">订单号：</Text>
                  <Text copyable>{order?.outTradeNo}</Text>
                </div>
                <div>
                  <Text type="secondary">支付金额：</Text>
                  <span className="price-tag" style={{ fontSize: '16px' }}>
                    {order?.totalAmount}
                  </span>
                </div>
                <div>
                  <Text type="secondary">订单状态：</Text>
                  <Tag color="green">已支付</Tag>
                </div>
              </Space>
            </Card>
          </Col>
          <Col span={12}>
            <Card size="small" title="学员信息">
              <Space direction="vertical" size="small">
                <div>
                  <Text type="secondary">学员姓名：</Text>
                  <Text strong>{order?.studentName || '-'}</Text>
                </div>
                <div>
                  <Text type="secondary">联系电话：</Text>
                  <Text>{order?.guardianPhone || '-'}</Text>
                </div>
                <div>
                  <Text type="secondary">开营日期：</Text>
                  <Text>{order?.campStartDate || '-'}</Text>
                </div>
              </Space>
            </Card>
          </Col>
        </Row>

        <Title level={4} style={{ marginTop: '20px' }}>📄 电子文档</Title>
        <Row gutter={[16, 16]}>
          <Col span={12}>
            <Card
              size="small"
              className="card-hover"
              actions={[
                <Button
                  type="primary"
                  icon={<FilePdfOutlined />}
                  onClick={() => window.open(pdfApi.getAgreementUrl(order?.id), '_blank')}
                >
                  下载协议
                </Button>,
              ]}
            >
              <Card.Meta
                avatar={<FilePdfOutlined style={{ fontSize: '32px', color: '#ff4d4f' }} />}
                title="电子协议"
                description="暑期研学出行服务协议"
              />
            </Card>
          </Col>
          <Col span={12}>
            <Card
              size="small"
              className="card-hover"
              actions={[
                <Button
                  type="primary"
                  icon={<FilePdfOutlined />}
                  onClick={() => window.open(pdfApi.getInsuranceUrl(order?.id), '_blank')}
                >
                  下载凭证
                </Button>,
              ]}
            >
              <Card.Meta
                avatar={<SafetyOutlined style={{ fontSize: '32px', color: '#52c41a' }} />}
                title="保险凭证"
                description="暑期研学出行保险单"
              />
            </Card>
          </Col>
        </Row>

        <div style={{ marginTop: '30px', textAlign: 'center' }}>
          <Space size="middle">
            <Button type="primary" icon={<HomeOutlined />} onClick={() => navigate('/')}>
              返回首页
            </Button>
            <Button onClick={() => navigate('/student')}>
              查看学员中心
            </Button>
          </Space>
        </div>
      </Card>
    </div>
  )
}

export default OrderSuccessPage
