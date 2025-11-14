# Frontend Cần Chỉnh - Hướng Dẫn Ngắn Gọn

## 1. Sửa cách gọi API tạo Payment URL

### ❌ SAI (Trước đây - Query Parameter):
```javascript
// SAI - Không dùng query parameter nữa
const response = await fetch(
  'http://14.225.206.98:8080/api/payment/vnpay/create-url?orderId=7',
  { method: 'POST' }
);
```

### ✅ ĐÚNG (Bây giờ - JSON Body):
```javascript
// ĐÚNG - Gửi orderId trong request body
const response = await fetch(
  'http://14.225.206.98:8080/api/payment/vnpay/create-url',
  {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      orderId: 7  // Phải là số, không phải string
    })
  }
);

const paymentUrl = await response.text(); // Response là string URL
window.location.href = paymentUrl; // Redirect đến VNPAY
```

### Ví dụ đầy đủ:
```javascript
async function handlePayment(orderId) {
  try {
    const response = await fetch(
      'http://14.225.206.98:8080/api/payment/vnpay/create-url',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ orderId: orderId })
      }
    );

    if (!response.ok) {
      const error = await response.text();
      alert('Lỗi: ' + error);
      return;
    }

    const paymentUrl = await response.text();
    window.location.href = paymentUrl; // Redirect đến VNPAY
  } catch (error) {
    console.error('Error:', error);
    alert('Đã xảy ra lỗi khi tạo payment URL');
  }
}
```

---

## 2. Tạo Route `/payment/result` để nhận callback

Sau khi thanh toán trên VNPAY, backend sẽ redirect về frontend với query parameters.

### Tạo component PaymentResult:

```jsx
// PaymentResult.jsx
import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';

function PaymentResult() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [result, setResult] = useState(null);

  useEffect(() => {
    const status = searchParams.get('status');
    const orderId = searchParams.get('orderId');
    const transactionNo = searchParams.get('transactionNo');
    const amount = searchParams.get('amount');
    const errorCode = searchParams.get('errorCode');

    if (status === 'success') {
      setResult({
        success: true,
        orderId: orderId,
        transactionNo: transactionNo,
        amount: amount ? parseInt(amount) / 100 : null, // VNPAY trả về * 100
        message: 'Thanh toán thành công!'
      });
    } else if (status === 'failed') {
      setResult({
        success: false,
        orderId: orderId,
        errorCode: errorCode,
        message: `Thanh toán thất bại. Mã lỗi: ${errorCode}`
      });
    } else if (status === 'error') {
      setResult({
        success: false,
        message: 'Đã xảy ra lỗi trong quá trình thanh toán'
      });
    }
  }, [searchParams]);

  if (!result) {
    return <div>Đang xử lý...</div>;
  }

  return (
    <div className="payment-result">
      {result.success ? (
        <div>
          <h2>✅ Thanh toán thành công!</h2>
          <p>{result.message}</p>
          {result.orderId && <p>Mã đơn hàng: {result.orderId}</p>}
          {result.transactionNo && <p>Mã giao dịch: {result.transactionNo}</p>}
          {result.amount && (
            <p>Số tiền: {result.amount.toLocaleString('vi-VN')} VND</p>
          )}
          <button onClick={() => navigate('/post/create')}>
            Đăng tin mới
          </button>
        </div>
      ) : (
        <div>
          <h2>❌ Thanh toán thất bại</h2>
          <p>{result.message}</p>
          {result.orderId && <p>Mã đơn hàng: {result.orderId}</p>}
          <button onClick={() => navigate('/post/create')}>
            Thử lại
          </button>
        </div>
      )}
    </div>
  );
}

export default PaymentResult;
```

### Thêm route vào router:

```jsx
// App.jsx hoặc router config
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import PaymentResult from './pages/PaymentResult';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/payment/result" element={<PaymentResult />} />
        {/* ... other routes */}
      </Routes>
    </BrowserRouter>
  );
}
```

---

## Tóm tắt thay đổi

### ✅ Cần sửa:
1. **API call**: Đổi từ query parameter sang JSON body
2. **Tạo route**: `/payment/result` để nhận callback
3. **Xử lý query params**: Đọc `status`, `orderId`, `transactionNo`, `amount`, `errorCode`

### ✅ Không cần sửa:
- URL API endpoint vẫn giữ nguyên: `/api/payment/vnpay/create-url`
- Cách redirect đến VNPAY vẫn giữ nguyên: `window.location.href = paymentUrl`

---

## Kiểm tra nhanh

1. ✅ Gọi API với `body: JSON.stringify({ orderId: 7 })` thay vì `?orderId=7`
2. ✅ Có route `/payment/result` trong router
3. ✅ Component PaymentResult đọc được query params từ URL
4. ✅ Hiển thị kết quả thành công/thất bại

Sau khi chỉnh xong, luồng sẽ là:
1. User click thanh toán → Gọi API với JSON body
2. Redirect đến VNPAY
3. Thanh toán xong → VNPAY redirect về backend
4. Backend xử lý → Redirect về frontend `/payment/result?status=success&...`
5. Frontend hiển thị kết quả


