import { Routes, Route, Link, useLocation } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import { HomeOutlined, ShoppingCartOutlined, UserOutlined } from '@ant-design/icons'
import HomePage from './pages/HomePage'
import CartPage from './pages/CartPage'
import CheckoutPage from './pages/CheckoutPage'
import PaymentPage from './pages/PaymentPage'
import OrderSuccessPage from './pages/OrderSuccessPage'
import StudentPage from './pages/StudentPage'
import { useState, useMemo } from 'react'

const { Header, Content, Footer } = Layout

function App() {
  const location = useLocation()
  const [cartItems, setCartItems] = useState([])

  const selectedKey = useMemo(() => {
    const path = location.pathname
    if (path === '/' || path === '/order-success') return 'home'
    if (path.startsWith('/cart') || path.startsWith('/checkout') || path.startsWith('/payment')) return 'cart'
    if (path.startsWith('/student')) return 'student'
    return 'home'
  }, [location.pathname])

  const addToCart = (product) => {
    setCartItems((prev) => {
      const existing = prev.find((item) => item.id === product.id)
      if (existing) {
        return prev.map((item) =>
          item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        )
      }
      return [...prev, { ...product, quantity: 1 }]
    })
  }

  const removeFromCart = (productId) => {
    setCartItems((prev) => prev.filter((item) => item.id !== productId))
  }

  const updateQuantity = (productId, quantity) => {
    if (quantity <= 0) {
      removeFromCart(productId)
      return
    }
    setCartItems((prev) =>
      prev.map((item) =>
        item.id === productId ? { ...item, quantity } : item
      )
    )
  }

  const clearCart = () => {
    setCartItems([])
  }

  const cartCount = cartItems.reduce((sum, item) => sum + item.quantity, 0)

  const menuItems = [
    { key: 'home', icon: <HomeOutlined />, label: <Link to="/">产品中心</Link> },
    { key: 'cart', icon: <ShoppingCartOutlined />, label: <Link to="/cart">购物车 ({cartCount})</Link> },
    { key: 'student', icon: <UserOutlined />, label: <Link to="/student">学员中心</Link> },
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', background: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}>
        <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#1890ff', marginRight: '40px' }}>
          🏕️ 暑期研学保障中心
        </div>
        <Menu mode="horizontal" items={menuItems} selectedKeys={[selectedKey]} style={{ flex: 1, borderBottom: 'none' }} />
      </Header>
      <Content style={{ padding: '0 0 40px' }}>
        <Routes>
          <Route path="/" element={<HomePage addToCart={addToCart} />} />
          <Route path="/cart" element={<CartPage cartItems={cartItems} updateQuantity={updateQuantity} removeFromCart={removeFromCart} />} />
          <Route path="/checkout" element={<CheckoutPage cartItems={cartItems} clearCart={clearCart} />} />
          <Route path="/payment/:outTradeNo" element={<PaymentPage />} />
          <Route path="/order-success" element={<OrderSuccessPage />} />
          <Route path="/student" element={<StudentPage />} />
        </Routes>
      </Content>
      <Footer style={{ textAlign: 'center', background: '#fff' }}>
        暑期研学出行险 + 电子协议组合包 ©{new Date().getFullYear()} Created by StudyTour
      </Footer>
    </Layout>
  )
}

export default App
