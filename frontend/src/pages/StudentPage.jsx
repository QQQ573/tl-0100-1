import { useState } from 'react'
import { Card, Button, Input, Form, message, Space, Typography, Row, Col, Tag, Spin, Divider, Descriptions, Modal, List, Radio } from 'antd'
import { SearchOutlined, SafetyOutlined, IdcardOutlined, FilePdfOutlined, RiseOutlined } from '@ant-design/icons'
import { studentApi, orderApi, pdfApi, supplementApi, paymentApi } from '../services/api'

const { Title, Text } = Typography

const levelNames = { BASIC: '基础版', STANDARD: '标准版', PREMIUM: '尊享版' }

function StudentPage() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [student, setStudent] = useState(null)
  const [orders, setOrders] = useState([])
  const [supplementMap, setSupplementMap] = useState({})
  const [searched, setSearched] = useState(false)
  const [upgradeModalVisible, setUpgradeModalVisible] = useState(false)
  const [upgradePreview, setUpgradePreview] = useState(null)
  const [upgradeOrderId, setUpgradeOrderId] = useState(null)
  const [selectedLevel, setSelectedLevel] = useState(null)
  const [upgradeLoading, setUpgradeLoading] = useState(false)

  const handleSearch = async (values) => {
    setLoading(true)
    setSearched(true)
    try {
      const studentData = await studentApi.getStudentByIdCard(values.idCard)
      setStudent(studentData)
      if (studentData && studentData.id) {
        const ordersData = await orderApi.getOrdersByStudentId(studentData.id)
        setOrders(ordersData || [])
        const sMap = {}
        for (const order of (ordersData || [])) {
          if (order.status === 'PAID') {
            try {
              const supplements = await supplementApi.getSupplementsByParentOrderId(order.id)
              sMap[order.id] = supplements || []
            } catch { sMap[order.id] = [] }
          }
        }
        setSupplementMap(sMap)
      }
    } catch (e) {
      message.error(e.message || '查询失败')
      setStudent(null)
      setOrders([])
    } finally {
      setLoading(false)
    }
  }

  const refreshData = async () => {
    const idCard = form.getFieldValue('idCard')
    if (!idCard) return
    try {
      const studentData = await studentApi.getStudentByIdCard(idCard)
      setStudent(studentData)
      if (studentData?.id) {
        const ordersData = await orderApi.getOrdersByStudentId(studentData.id)
        setOrders(ordersData || [])
        const sMap = {}
        for (const order of (ordersData || [])) {
          if (order.status === 'PAID') {
            try {
              const supplements = await supplementApi.getSupplementsByParentOrderId(order.id)
              sMap[order.id] = supplements || []
            } catch { sMap[order.id] = [] }
          }
        }
        setSupplementMap(sMap)
      }
    } catch {}
  }

  const handleUpgrade = async (orderId) => {
    setUpgradeOrderId(orderId)
    setUpgradeLoading(true)
    try {
      const preview = await supplementApi.getUpgradePreview(orderId)
      setUpgradePreview(preview)
      setSelectedLevel(null)
      setUpgradeModalVisible(true)
    } catch (e) {
      message.error(e.message || '获取升档预览失败')
    } finally {
      setUpgradeLoading(false)
    }
  }

  const handleConfirmUpgrade = async () => {
    if (!selectedLevel) {
      message.warning('请选择目标档位')
      return
    }
    setUpgradeLoading(true)
    try {
      const supplement = await supplementApi.createSupplementOrder({
        parentOrderId: upgradeOrderId,
        toLevel: selectedLevel,
      })
      message.success('补差单创建成功，正在模拟支付...')
      await paymentApi.mockCallback({
        outTradeNo: supplement.supplementNo,
        tradeStatus: 'SUCCESS',
      })
      message.success('补差支付成功，保障已升级！')
      setUpgradeModalVisible(false)
      refreshData()
    } catch (e) {
      message.error(e.message || '补差升档失败')
    } finally {
      setUpgradeLoading(false)
    }
  }

  const handleViewBadge = () => {
    const firstPaidOrder = orders.find((o) => o.status === 'PAID')
    if (!firstPaidOrder) return
    const supplements = supplementMap[firstPaidOrder.id] || []
    const effectiveLevel = supplements.filter(s => s.status === 'PAID').length > 0
      ? supplements.filter(s => s.status === 'PAID').pop().toLevel
      : 'BASIC'
    const badgeAmount = { BASIC: '¥200,000', STANDARD: '¥500,000', PREMIUM: '¥1,000,000' }[effectiveLevel] || '¥200,000'

    Modal.info({
      title: '保险保障徽章',
      width: 400,
      content: (
        <div style={{ textAlign: 'center', padding: '20px' }}>
          <div style={{
            width: '280px', height: '200px', margin: '0 auto',
            background: 'linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%)',
            borderRadius: '12px', display: 'flex', flexDirection: 'column',
            alignItems: 'center', justifyContent: 'center', border: '2px solid #b7eb8f',
          }}>
            <div style={{ fontSize: '28px', color: '#52c41a', fontWeight: 'bold' }}>✓ 已保障</div>
            <div style={{ fontSize: '18px', fontWeight: 'bold', marginTop: '10px' }}>{student?.name} 同学</div>
            <div style={{ fontSize: '14px', color: '#666', marginTop: '6px' }}>
              暑期研学出行险（{levelNames[effectiveLevel] || effectiveLevel}）
            </div>
            <div style={{ fontSize: '20px', color: '#ff4d4f', fontWeight: 'bold', marginTop: '10px' }}>
              保额 {badgeAmount}
            </div>
            <div style={{ fontSize: '12px', color: '#999', marginTop: '12px' }}>
              保单号: INS{firstPaidOrder.outTradeNo}
            </div>
          </div>
          <Button type="primary" icon={<FilePdfOutlined />} style={{ marginTop: '20px' }}
            onClick={() => window.open(pdfApi.getBadgeUrl(firstPaidOrder.id), '_blank')}>
            下载徽章PDF
          </Button>
        </div>
      ),
    })
  }

  const handleRefund = async (orderId) => {
    Modal.confirm({
      title: '确认退订',
      content: '退订需要满足：未开营且无私募补差在途。确定要退订吗？',
      onOk: async () => {
        try {
          await orderApi.refundOrder(orderId)
          message.success('退订成功')
          refreshData()
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

  const getStatusText = (status) => ({ PENDING: '待支付', PAID: '已支付', CANCELLED: '已取消', REFUNDED: '已退款' }[status] || status)
  const getStatusColor = (status) => ({ PENDING: 'orange', PAID: 'green', CANCELLED: 'default', REFUNDED: 'default' }[status] || 'default')

  const canUpgrade = (order) => {
    if (order.status !== 'PAID') return false
    if (order.campStartDate && new Date(order.campStartDate) <= new Date()) return false
    const supplements = supplementMap[order.id] || []
    if (supplements.some(s => s.status === 'PENDING')) return false
    return true
  }

  const getEffectiveLevel = (order) => {
    const supplements = (supplementMap[order.id] || []).filter(s => s.status === 'PAID')
    if (supplements.length === 0) return null
    return supplements[supplements.length - 1].toLevel
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>👤 学员中心</h1>
        <p>输入学员身份证号查询保障状态</p>
      </div>

      <Card style={{ maxWidth: '500px', margin: '0 auto 30px' }}>
        <Form form={form} layout="inline" onFinish={handleSearch}>
          <Form.Item name="idCard" rules={[{ required: true, message: '请输入身份证号' }, { len: 18, message: '身份证号应为18位' }]} style={{ flex: 1 }}>
            <Input prefix={<IdcardOutlined />} placeholder="请输入学员身份证号查询" size="large" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" icon={<SearchOutlined />} loading={loading} size="large">查询</Button>
          </Form.Item>
        </Form>
      </Card>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px' }}><Spin /></div>
      ) : student ? (
        <>
          <Card style={{ maxWidth: '800px', margin: '0 auto 24px' }}>
            <Row gutter={24} align="middle">
              <Col flex="auto">
                <Title level={3} style={{ marginBottom: '8px' }}>
                  {student.name}
                  {student.hasInsurance ? (
                    <Tag color="green" icon={<SafetyOutlined />} style={{ marginLeft: '12px', fontSize: '14px', padding: '4px 12px', cursor: 'pointer' }} onClick={handleViewBadge}>已保障</Tag>
                  ) : (
                    <Tag color="default" style={{ marginLeft: '12px', fontSize: '14px', padding: '4px 12px' }}>未投保</Tag>
                  )}
                </Title>
                <Text type="secondary">学员ID: {student.id}</Text>
              </Col>
              <Col>
                {student.hasInsurance && <Button type="primary" icon={<SafetyOutlined />} onClick={handleViewBadge}>查看保障徽章</Button>}
              </Col>
            </Row>
            <Divider />
            <Descriptions column={2} size="small">
              <Descriptions.Item label="学员姓名">{student.name}</Descriptions.Item>
              <Descriptions.Item label="保障状态">{student.hasInsurance ? <Tag color="green">已投保</Tag> : <Tag color="default">未投保</Tag>}</Descriptions.Item>
              <Descriptions.Item label="身份证号">{maskIdCard(student.idCard)}</Descriptions.Item>
              <Descriptions.Item label="联系电话">{student.phone || '未填写'}</Descriptions.Item>
            </Descriptions>
            <div style={{ marginTop: '16px', padding: '16px', background: '#fffbe6', borderRadius: '8px' }}>
              <Text type="warning" style={{ fontSize: '13px' }}>🔒 隐私保护：为保护学员隐私，页面仅展示脱敏后的身份证号，完整信息需授权查看。</Text>
            </div>
          </Card>

          {orders.length > 0 && (
            <Card title={`订单记录 (${orders.length})`} style={{ maxWidth: '800px', margin: '0 auto' }}>
              <List dataSource={orders} renderItem={(order) => {
                const effectiveLevel = getEffectiveLevel(order)
                const supplements = supplementMap[order.id] || []
                const hasPendingSupplement = supplements.some(s => s.status === 'PENDING')

                return (
                  <List.Item key={order.id} style={{ borderBottom: '1px solid #f0f0f0', padding: '16px 0' }}>
                    <List.Item.Meta
                      title={
                        <Space>
                          <span>{order.items?.map((item) => item.productName).join('、')}</span>
                          <Tag color={getStatusColor(order.status)}>{getStatusText(order.status)}</Tag>
                          {effectiveLevel && <Tag color="blue">{levelNames[effectiveLevel]}（已升档）</Tag>}
                          {hasPendingSupplement && <Tag color="orange">补差在途</Tag>}
                        </Space>
                      }
                      description={
                        <Space direction="vertical" size="small">
                          <Text type="secondary" style={{ fontSize: '12px' }}>订单号：{order.outTradeNo}</Text>
                          <Text type="secondary" style={{ fontSize: '12px' }}>下单时间：{order.createdAt}</Text>
                          {supplements.length > 0 && (
                            <div style={{ marginTop: '4px' }}>
                              {supplements.map(s => (
                                <Tag key={s.id} color={s.status === 'PAID' ? 'green' : 'orange'} style={{ fontSize: '11px' }}>
                                  补差: {levelNames[s.fromLevel]}→{levelNames[s.toLevel]} ¥{s.diffAmount} {s.status === 'PAID' ? '已付' : '待付'}
                                </Tag>
                              ))}
                            </div>
                          )}
                        </Space>
                      }
                    />
                    <Space direction="vertical" align="end" size="small">
                      <span className="price-tag" style={{ fontSize: '18px' }}>¥{order.totalAmount}</span>
                      <Space size="small">
                        {canUpgrade(order) && (
                          <Button type="link" size="small" icon={<RiseOutlined />}
                            onClick={() => handleUpgrade(order.id)}>补差升档</Button>
                        )}
                        {order.status === 'PAID' && (
                          <Button type="link" size="small" icon={<FilePdfOutlined />}
                            onClick={() => window.open(pdfApi.getAgreementUrl(order.id), '_blank')}>协议</Button>
                        )}
                        {order.status === 'PAID' && (
                          <Button type="link" size="small" icon={<FilePdfOutlined />}
                            onClick={() => window.open(pdfApi.getInsuranceUrl(order.id), '_blank')}>保险单</Button>
                        )}
                        {order.status === 'PAID' && !hasPendingSupplement && (
                          <Button type="link" size="small" danger onClick={() => handleRefund(order.id)}>退订</Button>
                        )}
                      </Space>
                    </Space>
                  </List.Item>
                )
              }} />
            </Card>
          )}
        </>
      ) : searched === false && (
        <div style={{ textAlign: 'center', padding: '60px', color: '#999' }}>
          <div style={{ fontSize: '64px', marginBottom: '16px' }}>🔍</div>
          <p>请输入学员身份证号进行查询</p>
        </div>
      )}

      <Modal
        title="📈 补差升档"
        open={upgradeModalVisible}
        onCancel={() => setUpgradeModalVisible(false)}
        onOk={handleConfirmUpgrade}
        confirmLoading={upgradeLoading}
        okText="确认补差"
        okButtonProps={{ disabled: !selectedLevel }}
        width={520}
      >
        {upgradePreview && (
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <div style={{ padding: '16px', background: '#f5f5f5', borderRadius: '8px' }}>
              <Text type="secondary">当前档位：</Text>
              <Text strong style={{ marginLeft: '8px' }}>
                {upgradePreview.currentProductName}（¥{upgradePreview.currentPrice}）
              </Text>
            </div>

            <div>
              <Text strong style={{ marginBottom: '12px', display: 'block' }}>选择目标档位：</Text>
              <Radio.Group value={selectedLevel} onChange={(e) => setSelectedLevel(e.target.value)} style={{ width: '100%' }}>
                <Space direction="vertical" style={{ width: '100%' }}>
                  {upgradePreview.upgradeOptions?.map((opt) => (
                    <Radio key={opt.level} value={opt.level} style={{ width: '100%' }}>
                      <div style={{
                        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                        padding: '12px 16px', border: selectedLevel === opt.level ? '2px solid #1890ff' : '1px solid #d9d9d9',
                        borderRadius: '8px', width: '100%', background: selectedLevel === opt.level ? '#e6f7ff' : '#fff',
                      }}>
                        <div>
                          <div><Text strong>{opt.productName}</Text></div>
                          <div><Text type="secondary" style={{ fontSize: '12px' }}>产品价格：¥{opt.price}</Text></div>
                        </div>
                        <div style={{ textAlign: 'right' }}>
                          <div style={{ color: '#ff4d4f', fontWeight: 'bold', fontSize: '18px' }}>
                            +¥{opt.diffAmount}
                          </div>
                          <Text type="secondary" style={{ fontSize: '12px' }}>补差金额</Text>
                        </div>
                      </div>
                    </Radio>
                  ))}
                </Space>
              </Radio.Group>
            </div>

            {selectedLevel && (
              <div style={{ padding: '16px', background: '#fffbe6', borderRadius: '8px' }}>
                <Text type="warning">💡 补差成功后，保险凭证和电子协议将自动更新为{levelNames[selectedLevel]}档位与保额</Text>
              </div>
            )}
          </Space>
        )}
      </Modal>
    </div>
  )
}

export default StudentPage
