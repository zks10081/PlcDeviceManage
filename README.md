# DM PLC 设备管理系统

一个前后端分离的基础管理脚手架，包含 **登录、用户管理、字典管理** 三大模块。

- 后端：Spring Boot 2.7 + Java 8 + Spring Security + JWT + Spring Data JPA（H2 内存数据库，零配置即可运行）
- 前端：Vue 3 + Vite + Element Plus + Vue Router + Pinia + Axios

---

## 目录结构

```
DM_PLCDevice/
├── backend/      # Spring Boot 后端（Java 8）
└── frontend/     # Vue 3 前端
```

默认账号：`admin / 123456`

---

## 后端运行

> 需要 JDK 8+ 与 Maven 3.6+。本项目 `pom.xml` 中 `java.version=1.8`，
> 可在 JDK 8 ~ 17 上编译运行。

```bash
cd backend
mvn clean spring-boot:run
```

- 接口前缀：`http://localhost:8080/api`
- 登录接口：`POST /api/auth/login`
- H2 控制台：`http://localhost:8080/api/h2-console`（JDBC URL：`jdbc:h2:mem:plcdb`）

### 主要接口

| 模块 | 方法 | 路径 | 鉴权 |
| --- | --- | --- | --- |
| 登录 | POST | `/auth/login` | 公开 |
| 用户分页 | GET | `/users` | 登录 |
| 新增用户 | POST | `/users` | ADMIN |
| 修改用户 | PUT | `/users` | ADMIN |
| 删除用户 | DELETE | `/users/{id}` | ADMIN |
| 字典类型分页 | GET | `/dicts` | 登录 |
| 字典类型增改删 | POST/PUT/DELETE | `/dicts` | ADMIN |
| 字典数据分页 | GET | `/dicts/data/page` | 登录 |
| 按类型查字典数据 | GET | `/dicts/by-type/{type}` | 公开 |

---

## 前端运行

```bash
cd frontend
npm install
npm run dev
```

访问：`http://localhost:5173`

前端通过 Vite 代理把 `/api` 转发到 `http://localhost:8080`，
因此前后端可分别独立启动、联调。

构建产物：`npm run build`（输出到 `frontend/dist`）。

---

## 说明

- 数据库使用 H2 内存库，应用启动时会自动初始化管理员账号与示例字典数据。
- 如需切换为 MySQL，修改 `backend/src/main/resources/application.yml` 中的
  `spring.datasource` 与 `spring.jpa.hibernate.ddl-auto`，并引入对应驱动依赖即可。
- JWT 密钥与过期时间在 `application.yml` 的 `jwt` 节点配置。
