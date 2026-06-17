import { useState } from 'react'
import { Form, Input, DatePicker, Button, Card, Row, Col, Steps, message, Space, Divider } from 'antd'
import { UserOutlined, IdcardOutlined, PhoneOutlined, MailOutlined } from '@ant-design/icons'
import { useNavigate, Navigate } from 'react-router-dom'
import { orderApi } from '../services/api'
import dayjs from 'dayjs'

const { Step } = Steps

function CheckoutPage({ cartItems, clearCart }) {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  if (cartItems.length === 0) {
    return <Navigate to="/cart" />
  }

  const totalAmount = cartItems.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  )

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      const orderData = {
        items: cartItems.map((item) => ({
          productId: item.id,
          quantity: item.quantity,
        })),
        studentName: values.studentName,
        studentIdCard: values.studentIdCard,
        studentPhone: values.studentPhone,
        studentEmail: values.studentEmail,
        guardianName: values.guardianName,
        guardianIdCard: values.guardianIdCard,
        guardianPhone: values.guardianPhone,
        campStartDate: values.campStartDate.format('YYYY-MM-DD'),
      }

      const order = await orderApi.createOrder(orderData)
      clearCart()
      message.success('订单创建成功')
      navigate(`/payment/${order.outTradeNo}`)
    } catch (e) {
      message.error(e.message || '创建订单失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>📝 填写订单信息</h1>
        <p>请准确填写学员及监护人信息，用于生成保险凭证和电子协议</p>
      </div>

      <Steps current={1} style={{ marginBottom: '30px' }}>
        <Step title="选购产品" />
        <Step title="填写信息" />
        <Step title="支付" />
        <Step title="完成" />
      </Steps>

      <Row gutter={24}>
        <Col span={16}>
          <Card title="学员及监护人信息">
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              initialValues={{
                campStartDate: dayjs().add(15, 'day'),
              }}
            >
              <Divider orientation="left">学员信息</Divider>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="studentName"
                    label="学员姓名"
                    rules={[{ required: true, message: '请输入学员姓名' }]}
                  >
                    <Input prefix={<UserOutlined />} placeholder="请输入学员姓名" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="studentIdCard"
                    label="学员身份证号"
                    rules={[
                      { required: true, message: '请输入学员身份证号' },
                      { len: 18, message: '身份证号应为18位' },
                    ]}
                  >
                    <Input prefix={<IdcardOutlined />} placeholder="请输入18位身份证号" />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="studentPhone"
                    label="学员电话（可选）"
                    rules={[{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]}
                  >
                    <Input prefix={<PhoneOutlined />} placeholder="请输入手机号" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="studentEmail"
                    label="学员邮箱（可选）"
                    rules={[{ type: 'email', message: '请输入正确的邮箱' }]}
                  >
                    <Input prefix={<MailOutlined />} placeholder="请输入邮箱" />
                  </Form.Item>
                </Col>
              </Row>

              <Divider orientation="left">监护人信息</Divider>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="guardianName"
                    label="监护人姓名"
                    rules={[{ required: true, message: '请输入监护人姓名' }]}
                  >
                    <Input prefix={<UserOutlined />} placeholder="请输入监护人姓名" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="guardianIdCard"
                    label="监护人身份证号"
                    rules={[
                      { required: true, message: '请输入监护人身份证号' },
                      { len: 18, message: '身份证号应为18位' },
                    ]}
                  >
                    <Input prefix={<IdcardOutlined />} placeholder="请输入18位身份证号" />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="guardianPhone"
                    label="监护人电话"
                    rules={[
                      { required: true, message: '请输入监护人电话' },
                      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
                    ]}
                  >
                    <Input prefix={<PhoneOutlined />} placeholder="请输入手机号" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="campStartDate"
                    label="开营日期"
                    rules={[{ required: true, message: '请选择开营日期' }]}
                  >
                    <DatePicker style={{ width: '100%' }} disabledDate={(d) => d && d.isBefore(dayjs())} />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item style={{ marginTop: '20px' }}>
                <Button type="primary" htmlType="submit" loading={loading} size="large" block>
                  提交订单
                </Button>
              </Form.Item>
            </Form>
          </Card>
        </Col>

        <Col span={8}>
          <Card title="订单商品">
            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
              {cartItems.map((item) => (
                <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span>{item.name} x{item.quantity}</span>
                  <span className="price-tag" style={{ fontSize: '14px' }}>
                    {(item.price * item.quantity).toFixed(2)}
                  </span>
                </div>
              ))}
              <Divider />
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '16px' }}>合计：</span>
                <span className="price-tag" style={{ fontSize: '24px' }}>
                  {totalAmount.toFixed(2)}
                </span>
              </div>
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default CheckoutPage
