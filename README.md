# Java Kafka OMS (Learning Project)

Production-style modular monolith using Java 21 + Spring Boot + Kafka + PostgreSQL + Redis with clean architecture boundaries.

## Modules
- `oms-bootstrap`: executable Spring Boot application and wiring.
- `oms-shared-kernel`: event envelope + shared primitives.
- `oms-order`: order domain, API, persistence, and outbox scaffolding.
- `oms-testing`: shared Testcontainers support.

## Hướng dẫn chạy API bằng Docker

### 1) Yêu cầu môi trường
- Docker Engine + Docker Compose plugin.
- Cổng còn trống trên máy host:
  - `8080` (ứng dụng API)
  - `5432` (PostgreSQL)
  - `6379` (Redis)
  - `9092` (Kafka)

### 2) Build và khởi động toàn bộ stack
Từ thư mục gốc repo, chạy:

```bash
make up
```

Lệnh trên sẽ:
- Dựng các container `postgres`, `redis`, `kafka`, `app` từ file `docker/compose/docker-compose.yml`.
- Build image ứng dụng từ `docker/app/Dockerfile`.
- Expose API tại `http://localhost:8080`.

### 3) Kiểm tra nhanh API đã sẵn sàng
Kiểm tra health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Kết quả mong đợi (ví dụ):

```json
{"status":"UP"}
```

> Lưu ý: endpoint `/actuator/health` được mở public, không cần JWT.

### 4) Dừng và xoá dữ liệu local

```bash
make down
```

Lệnh này sẽ stop container và xoá volumes (`down -v`).

---

## Hướng dẫn sử dụng Swagger (OpenAPI)

Project đã tích hợp springdoc OpenAPI UI. Sau khi stack đã chạy:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 1) Mở và đọc tài liệu API
1. Truy cập `http://localhost:8080/swagger-ui/index.html`.
2. Mở nhóm endpoint tương ứng (hiện tại có endpoint tạo đơn hàng).
3. Chọn endpoint cần test và bấm **Try it out**.

### 2) Endpoint chính hiện tại
- `POST /api/v1/orders`

Request body mẫu:

```json
{
  "customerId": "11111111-1111-1111-1111-111111111111",
  "items": [
    {
      "sku": "SKU-IPHONE-15",
      "quantity": 1,
      "price": 999.99
    },
    {
      "sku": "SKU-AIRPODS-PRO",
      "quantity": 2,
      "price": 249.50
    }
  ]
}
```

Response thành công (`201 Created`) dạng:

```json
{
  "orderId": "a3cb4dd0-7391-40f3-8d29-4cf1cf74cf37",
  "status": "CREATED",
  "totalAmount": 1498.99
}
```

### 3) Xác thực khi gọi API từ Swagger
- `POST /api/v1/orders` yêu cầu quyền `ROLE_CUSTOMER` (JWT Bearer token).
- Trong Swagger UI:
  1. Bấm nút **Authorize**.
  2. Nhập token theo format: `Bearer <your_jwt_token>`.
  3. Bấm **Authorize** và gọi lại endpoint.

Nếu không có token hợp lệ, API sẽ trả `401/403`.

### 4) Test nhanh bằng curl (không qua Swagger)

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "customerId":"11111111-1111-1111-1111-111111111111",
    "items":[
      {"sku":"SKU-TEST-001","quantity":2,"price":10.50}
    ]
  }'
```

---

## Build
```bash
make build
```

## Test
```bash
make test
```

## Notes
This scaffold includes initial Flyway migration (`orders`, `outbox_events`, `processed_messages`) and security baseline.
