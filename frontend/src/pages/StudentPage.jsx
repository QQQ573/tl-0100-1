import { useState } from 'react'
import { Card, Button, Input, Form, message, Space, Typography, Row, Col, Tag, Spin, Divider, Descriptions, Modal, List } from 'antd'
import { SearchOutlined, SafetyOutlined, IdcardOutlined, FilePdfOutlined, CheckCircleOutlined } from '@ant-design/icons'
import { studentApi, orderApi, pdfApi } from '../services/api'

const { Title, Text } = Typography

function StudentPage() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [student, setStudent] = useState(null)
  const [orders, setOrders] = useState([])
  const [searched, setSearched] = useState(false)

  const handleSearch = async (values) => {
    setLoading(true)
    setSearched(true)
    try {
      const studentData = await studentApi.getStudentByIdCard(values.idCard)
      setStudent(studentData)

      if (studentData && studentData.id) {
        const ordersData = await orderApi.getOrdersByStudentId(studentData.id)
        setOrders(ordersData || [])
      }
    } catch (e) {
      message.error(e.message || '查询失败')
      setStudent(null)
      setOrders([])
    } finally {
      setLoading(false)
    }
  }

  const handleViewBadge = () => {
    const firstPaidOrder = orders.find((o) => o.status === 'PAID')
    if (firstPaidOrder) {
      Modal.info({
        title: '保险保障徽章',
        width: 400,
        content: (
          <div style={{ textAlign: 'center', padding: '20px' }}>
            <div
              style={{
                width: '280px',
                height: '200px',
                margin: '0 auto',
                background: 'linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%)',
                borderRadius: '12px',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                border: '2px solid #b7eb8f',
              }}
            >
              <div style={{ fontSize: '28px', color: '#52c41a', fontWeight: 'bold' }}>
                ✓ 已保障
              </div>
              <div style={{ fontSize: '18px', fontWeight: 'bold', marginTop: '10px' }}>
                {student?.name} 同学
              </div>
              <div style={{ fontSize: '14px', color: '#666', marginTop: '6px' }}>
                暑期研学出行险
              </div>
              <div style={{ fontSize: '20px', color: '#ff4d4f', fontWeight: 'bold', marginTop: '10px' }}>
                保额 ¥500,000
              </div>
              <div style={{ fontSize: '12px', color: '#999', marginTop: '12px' }}>
                保单号: INS{firstPaidOrder.outTradeNo}
              </div>
            </div>
            <Button
              type="primary"
              icon={<FilePdfOutlined />}
              style={{ marginTop: '20px' }}
              onClick={() => window.open(pdfApi.getBadgeUrl(firstPaidOrder.id), '_blank')}
            >
              下载徽章PDF
            </Button>
          </div>
        ),
      })
    }
  }

  const handleRefund = async (orderId) => {
    Modal.confirm({
      title: '确认退订',
      content: '退订需要满足：未开营且无私募补差在途。确定要退订吗？',
      onOk: async () => {
        try {
          await orderApi.refundOrder(orderId)
          message.success('退订成功')
          const ordersData = await orderApi.getOrdersByStudentId(student.id)
          setOrders(ordersData || [])
          const studentData = await studentApi.getStudentByIdCard(form.getFieldValue('idCard'))
          setStudent(studentData)
        } catch (e) {
          message.error(e.message || '退订失败')
        }
      },
    })
  }

  const maskIdCard = (idCard) => {
    if (!idCard || idCard.length < 8) return idCard
    return idCard.substring(0, 6) + '********' + idCard.substring(idCard.length - 4)
  }

  const getStatusText = (status) => {
    const map = {
      PENDING: '待支付',
      PAID: '已支付',
      CANCELLED: '已取消',
      REFUNDED: '已退款',
    }
    return map[status] || status
  }

  const getStatusColor = (status) => {
    const map = {
      PENDING: 'orange',
      PAID: 'green',
      CANCELLED: 'default',
      REFUNDED: 'default',
    }
    return map[status] || 'default'
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>👤 学员中心</h1>
        <p>输入学员身份证号查询保障状态</p>
      </div>

      <Card style={{ maxWidth: '500px', margin: '0 auto 30px' }}>
        <Form form={form} layout="inline" onFinish={handleSearch}>
          <Form.Item
            name="idCard"
            rules={[
              { required: true, message: '请输入身份证号' },
              { len: 18, message: '身份证号应为18位' },
            ]}
            style={{ flex: 1 }}
          >
            <Input prefix={<IdcardOutlined />} placeholder="请输入学员身份证号查询" size="large" />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              icon={<SearchOutlined />}
              loading={loading}
              size="large"
            >
              查询
            </Button>
          </Form.Item>
        </Form>
      </Card>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px' }}>
          <Spin />
        </div>
      ) : student ? (
        <>
          <Card style={{ maxWidth: '800px', margin: '0 auto 24px' }}>
            <Row gutter={24} align="middle">
              <Col flex="auto">
                <Title level={3} style={{ marginBottom: '8px' }}>
                  {student.name}
                  {student.hasInsurance && (
                    <Tag
                      color="green"
                      icon={<SafetyOutlined />}
                      style={{ marginLeft: '12px', fontSize: '14px', padding: '4px 12px', cursor: 'pointer' }}
                      onClick={handleViewBadge}
                    >
                      已保障
                    </Tag>
                  )}
                  {!student.hasInsurance && (
                    <Tag color="default" style={{ marginLeft: '12px', fontSize: '14px', padding: '4px 12px' }}>
                      未投保
                    </Tag>
                  )}
                </Title>
                <Text type="secondary">
                  学员ID: {student.id}
                </Text>
              </Col>
              <Col>
                {student.hasInsurance && (
                  <Button type="primary" icon={<SafetyOutlined />} onClick={handleViewBadge}>
                    查看保障徽章
                  </Button>
                )}
              </Col>
            </Row>

            <Divider />

            <Descriptions column={2} size="small">
              <Descriptions.Item label="学员姓名">{student.name}</Descriptions.Item>
              <Descriptions.Item label="保障状态">
                {student.hasInsurance ? (
                  <Tag color="green">已投保</Tag>
                ) : (
                  <Tag color="default">未投保</Tag>
                )}
              </Descriptions.Item>
              <Descriptions.Item label="身份证号">
                {maskIdCard(student.idCard)}
              </Descriptions.Item>
              <Descriptions.Item label="联系电话">
                {student.phone || '未填写'}
              </Descriptions.Item>
            </Descriptions>

            <div style={{ marginTop: '16px', padding: '16px', background: '#fffbe6', borderRadius: '8px' }}>
              <Text type="warning" style={{ fontSize: '13px' }}>
                🔒 隐私保护：为保护学员隐私，页面仅展示脱敏后的身份证号，完整信息需授权查看。
              </Text>
            </div>
          </Card>

          {orders.length > 0 && (
            <Card title={`订单记录 (${orders.length})`} style={{ maxWidth: '800px', margin: '0 auto' }}>
              <List
                dataSource={orders}
                renderItem={(order) => (
                  <List.Item
                    key={order.id}
                    style={{ borderBottom: '1px solid #f0f0f0', padding: '16px 0' }}
                  >
                    <List.Item.Meta
                      title={
                        <Space>
                          <span>{order.items?.map((item) => item.productName).join('、')}</span>
                          <Tag color={getStatusColor(order.status)}>
                            {getStatusText(order.status)}
                          </Tag>
                        </Space>
                      }
                      description={
                        <Space direction="vertical" size="small">
                          <Text type="secondary" style={{ fontSize: '12px' }}>
                            订单号：{order.outTradeNo}
                          </Text>
                          <Text type="secondary" style={{ fontSize: '12px' }}>
                            下单时间：{order.createdAt}
                          </Text>
                        </Space>
                      }
                    />
                    <Space direction="vertical" align="end" size="small">
                      <span className="price-tag" style={{ fontSize: '18px' }}>
                        ¥{order.totalAmount}
                      </span>
                      <Space size="small">
                        {order.status === 'PAID' && (
                          <Button
                            type="link"
                            size="small"
                            icon={<FilePdfOutlined />}
                            onClick={() => window.open(pdfApi.getAgreementUrl(order.id), '_blank')}
                          >
                            协议
                          </Button>
                        )}
                        {order.status === 'PAID' && (
                          <Button
                            type="link"
                            size="small"
                            icon={<FilePdfOutlined />}
                            onClick={() => window.open(pdfApi.getInsuranceUrl(order.id), '_blank')}
                          >
                            保险单
                          </Button>
                        )}
                        {order.status === 'PAID' && (
                          <Button
                            type="link"
                            size="small"
                            danger
                            onClick={() => handleRefund(order.id)}
                          >
                            退订
                          </Button>
                        )}
                      </Space>
                    </Space>
                  </List.Item>
                )}
              />
            </Card>
          )}
        </>
      ) : (
        searched === false && (
          <div style={{ textAlign: 'center', padding: '60px', color: '#999' }}>
            <div style={{ fontSize: '64px', marginBottom: '16px' }}>🔍</div>
            <p>请输入学员身份证号进行查询</p>
          </div>
        )
      )}
    </div>
  )
}

export default StudentPage
