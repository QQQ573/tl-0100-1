import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

api.interceptors.response.use(
  (response) => {
    if (response.data && response.data.code === 200) {
      return response.data.data
    }
    return Promise.reject(new Error(response.data?.message || '请求失败'))
  },
  (error) => {
    return Promise.reject(error)
  }
)

export const productApi = {
  getAllProducts: () => api.get('/products'),
  getProductsByType: (type) => api.get(`/products/type/${type}`),
  getProductById: (id) => api.get(`/products/${id}`),
}

export const orderApi = {
  createOrder: (data) => api.post('/orders', data),
  getOrderById: (id) => api.get(`/orders/${id}`),
  getOrderByOutTradeNo: (outTradeNo) => api.get(`/orders/outTradeNo/${outTradeNo}`),
  getOrdersByStudentId: (studentId) => api.get(`/orders/student/${studentId}`),
  cancelOrder: (id) => api.put(`/orders/${id}/cancel`),
  refundOrder: (id) => api.put(`/orders/${id}/refund`),
}

export const paymentApi = {
  mockPay: (outTradeNo) => api.get(`/payment/mock/pay/${outTradeNo}`),
  mockCallback: (params) => api.get('/payment/mock/callback', { params }),
}

export const studentApi = {
  getStudentById: (id) => api.get(`/students/${id}`),
  getStudentByIdCard: (idCard) => api.get(`/students/idCard/${idCard}`),
  getStudentPublicInfo: (id) => api.get(`/students/${id}/public`),
}

export const pdfApi = {
  getAgreementUrl: (orderId) => `/api/pdf/agreement/${orderId}`,
  getInsuranceUrl: (orderId) => `/api/pdf/insurance/${orderId}`,
  getBadgeUrl: (orderId) => `/api/pdf/insurance-badge/${orderId}`,
}

export default api
