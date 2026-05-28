# 医生工作站系统

基于 Vue 2 + Spring Boot + MyBatis + MySQL 的医生工作站系统，覆盖挂号、接诊、病历书写、处方开具全流程，支持管理员、医生、患者三种角色。

> 原型来源：[iCaaat/EMRSystem](https://github.com/iCaaat/EMRSystem)

## 环境要求

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 后端运行环境 |
| Maven | 3.6+ | 后端构建工具 |
| Node.js | 14+ | 前端运行环境 |
| npm | 6+ | 前端包管理 |
| MySQL | 8.0+ | 数据库，11 张表 |

## 项目结构

```
EMRSystem/
├── springboot/                 # Spring Boot 后端
│   └── src/main/
│       ├── java/usc/emrsytem/springboot/
│       │   ├── controller/     # 接口控制器
│       │   ├── service/        # 业务逻辑
│       │   ├── mapper/         # MyBatis 数据访问
│       │   ├── entity/         # 实体类
│       │   ├── config/         # 配置（CORS、拦截器）
│       │   └── common/         # 公共类（Result、JWT）
│       └── resources/
│           ├── application.yml # 应用配置
│           └── mapper/         # SQL 映射 XML
├── src/                        # Vue 前端
│   ├── views/                  # 页面组件
│   │   ├── login/              # 登录页
│   │   ├── home/               # 首页仪表盘
│   │   ├── MedicalRecord/      # 病历管理
│   │   ├── prescription/       # 处方管理
│   │   ├── Registration/       # 挂号管理
│   │   ├── user/               # 用户管理
│   │   └── person/             # 个人中心
│   ├── components/             # 公共组件
│   ├── router/                 # 路由配置
│   └── utils/                  # 工具函数（axios 封装）
├── sql/                        # 数据库初始化脚本
├── docs/                       # Bug 修复文档
└── README.md
```

## 部署准备

### 1. 安装 MySQL 并创建数据库

确保 MySQL 服务已启动，使用 root 账号（密码 `123456`）登录，创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS emrs DEFAULT CHARSET utf8mb4;
```

> **如果 MySQL 密码不是 `123456`，需要修改以下文件：**

| 文件 | 改什么 | 跳转 |
|------|--------|------|
| 后端配置 | `spring.datasource.password` | [application.yml](springboot/src/main/resources/application.yml) |
| 初始化脚本 | `-p123456` 替换为你的密码 | [init.sql](sql/init.sql) |
| 迁移脚本 | `-p123456` 替换为你的密码 | [migration-archive.sql](sql/migration-archive.sql) · [migration-registration.sql](sql/migration-registration.sql) |

[application.yml](springboot/src/main/resources/application.yml) 中修改：
```yaml
spring:
  datasource:
    username: root
    password: 你的密码   # ← 改这里
```

### 2. 安装 Node.js 并安装前端依赖

```bash
cd EMRSystem
npm install
```

### 3. 安装 JDK 17 和 Maven

确保 `java -version` 和 `mvn -version` 可用。

## 启动步骤

### 第一步：启动后端

```bash
# 进入后端目录
cd springboot

# 编译打包（首次或代码变更后需要）
mvn package -DskipTests

# 启动应用
java -jar target/springboot-0.0.1-SNAPSHOT.jar
```

启动成功后控制台输出 `启动成功`，后端运行在 **http://localhost:9090**。

> 启动前请确保已完成 [数据库初始化](#数据库初始化)，否则后端接口会报错。

### 第二步：启动前端

```bash
# 在项目根目录
npm run serve
```

启动成功后前端运行在 **http://localhost:8080**，浏览器访问即可。

> **如果需要修改端口：**

| 端口 | 用途 | 修改文件 | 跳转 |
|------|------|----------|------|
| 9090 | 后端 API 端口 | 改 `server.port` | [application.yml](springboot/src/main/resources/application.yml) |
| 9090 | 前端请求地址 | 改 `baseURL` | [request.js](src/utils/request.js) |
| 9090 | 图片上传地址 | 改 `action` | [AddMedicalRecord.vue](src/views/MedicalRecord/AddMedicalRecord.vue) |
| 8080 | 前端开发服务器 | 添加 `devServer.port` | [vue.config.js](vue.config.js) |

修改 [vue.config.js](vue.config.js) 示例：

```js
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 3000   // ← 改成你想要的端口
  }
})
```
| 8080 | 前端开发服务器 | Vue CLI 自动分配，或 `vue.config.js` 中配置 `devServer.port` | |

### 停止项目

前端按 `Ctrl+C` 终止 Vue CLI 开发服务器。

后端按 `Ctrl+C` 终止 Java 进程，或通过端口查找并终止：

```bash
# Windows（CMD 或 PowerShell）
netstat -ano | findstr "9090"    # 找到后端进程 PID
taskkill /F /PID 这里输入实际的PID /T          # 终止进程

netstat -ano | findstr "8080"    # 找到前端进程 PID
taskkill /F /PID 这里输入实际的PID /T           # 终止进程
```

```bash
# Linux / macOS
lsof -ti:9090 | xargs kill       # 终止后端
lsof -ti:8080 | xargs kill       # 终止前端
```

## 数据库初始化

首次启动后端前，需要在 MySQL 中创建 `emrs` 库和表结构。提供两种方式：

### 方式一：命令行导入 SQL 文件（推荐）

项目根目录下已准备好 [sql/init.sql](sql/init.sql)，包含建库、建表和初始账号的全部语句。

在 VSCode 终端或命令行中执行：

```bash
mysql -uroot -p123456 < sql/init.sql
```

> 把 `123456` 替换为你的 MySQL 密码。如果密码不是 `123456`，还需修改上述 [application.yml](springboot/src/main/resources/application.yml) 中的配置。

### 方式二：手动执行 SQL

<details>
<summary>点击展开完整 SQL（与 init.sql 内容一致）</summary>

```sql
CREATE TABLE IF NOT EXISTS user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    status BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    department VARCHAR(100),
    gender VARCHAR(10),
    specialty VARCHAR(200),
    experience_years INT,
    qualification VARCHAR(200),
    title VARCHAR(100),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    gender VARCHAR(10),
    date_of_birth DATE,
    address VARCHAR(255),
    emergency_contact VARCHAR(50),
    contact_phone VARCHAR(20),
    medical_history TEXT,
    allergies TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    admin_level VARCHAR(50),
    department VARCHAR(100),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS doctor_patient (
    doctor_patient_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS medical_record (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prescription_id INT,
    diagnosis TEXT,
    symptoms TEXT,
    chart TEXT,
    remarks TEXT,
    prescription TEXT,
    status VARCHAR(20) DEFAULT 'active',
    chief_complaint VARCHAR(500),
    present_illness TEXT,
    past_history TEXT,
    physical_exam TEXT,
    record_status VARCHAR(20) DEFAULT 'draft',
    void_reason VARCHAR(500),
    supplement TEXT,
    total_fee DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prescription (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    medication_name VARCHAR(200),
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    instructions TEXT,
    remarks TEXT,
    unit_price DECIMAL(10,2) DEFAULT 0,
    total_price DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS diagnosis_template (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    diagnosis_name VARCHAR(200),
    symptoms TEXT,
    prescription_hint TEXT,
    usage_count INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS drug_info (
    drug_id INT AUTO_INCREMENT PRIMARY KEY,
    drug_name VARCHAR(200),
    specification VARCHAR(100),
    unit_price DECIMAL(10,2),
    dosage_hint VARCHAR(200),
    frequency_hint VARCHAR(100),
    indications TEXT,
    contraindications TEXT,
    adverse_reactions TEXT,
    interactions TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    user_id INT NOT NULL,
    username VARCHAR(50),
    action VARCHAR(30),
    detail TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS registration (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    patient_user_id INT NOT NULL,
    doctor_id INT NOT NULL,
    department VARCHAR(100),
    chief_complaint VARCHAR(500),
    queue_number INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'waiting',
    registration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    consultation_time TIMESTAMP NULL,
    completion_time TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO user (username, password, phone_number, email, role, status) VALUES
('管理员', '$2a$10$AkEXt6eXtxDiRh2MOxkZIeus/Ge6ywbNuYBeueZWGOdA7TaCsQlci', '13800000000', 'admin@example.com', 'admin', TRUE),
('张医生', '$2a$10$AkEXt6eXtxDiRh2MOxkZIeus/Ge6ywbNuYBeueZWGOdA7TaCsQlci', '13800000001', 'doctor@example.com', 'doctor', TRUE),
('李病人', '$2a$10$AkEXt6eXtxDiRh2MOxkZIeus/Ge6ywbNuYBeueZWGOdA7TaCsQlci', '13800000002', 'patient@example.com', 'patient', TRUE);

INSERT INTO admin (user_id, admin_level, department) VALUES (1, '超级管理员', '信息科');
INSERT INTO doctor (user_id, department, gender, specialty, qualification, title, experience_years) VALUES (2, '内科', 'male', '心内科', '主任医师', '主任医师', 15);
INSERT INTO patient (user_id, gender, date_of_birth, address, emergency_contact, contact_phone, medical_history, allergies) VALUES (3, 'female', '1990-05-15', '广州市天河区', '李四', '13800000003', '高血压', '青霉素');
```

</details>

## 默认账号

> 初始账号已包含在 [sql/init.sql](sql/init.sql) 和方式二的 SQL 中，导入后自动可用。

| 角色 | 手机号 | 密码 |
|------|--------|------|
| 管理员 | 13800000000 | admin123 |
| 医生 | 13800000001 | admin123 |
| 患者 | 13800000002 | admin123 |

## 功能概览

| 功能 | 管理员 | 医生 | 患者 | 说明 |
|------|:------:|:----:|:----:|------|
| 个人中心 | ✓ | ✓ | ✓ | 查看/编辑个人信息 |
| 修改密码 | ✓ | ✓ | ✓ | 修改当前账号密码 |
| 接诊排队 | ✗ | ✓ | ✗ | 医生查看待诊患者队列，接诊/完成就诊 |
| 挂号管理 | ✓ | ✓ | ✗ | 创建/管理挂号记录 |
| 我的病历 | ✗ | ✗ | ✓ | 患者查看自己的过往病历 |
| 查询病历 | ✓ | ✓ | ✗ | 搜索/查看/归档(CSV导出)/恢复/确认/作废/补增/打印/操作日志 |
| 病历对比 | ✓ | ✓ | ✗ | 选择同一患者的两次病历并排对比 |
| 新增病历 | ✗ | ✓ | ✗ | 书写病历（含模板/药品提示/多图），支持编辑草稿 |
| 查看名下患者 | ✗ | ✓ | ✗ | 查看绑定到当前医生的患者 |
| 注册医患关系 | ✓ | ✓ | ✗ | 一键绑定/解除绑定，查看所有关系 |
| 查询处方 | ✓ | ✓ | ✗ | 按条件搜索处方 |
| 新增处方 | ✗ | ✓ | ✗ | 开具新处方 |
| 查询用户 | ✓ | ✓ | ✗ | 查看所有用户信息 |
| 添加用户 | ✓ | ✗ | ✗ | 创建新用户（患者/医生/管理员） |
| 删除用户 | ✓ | ✗ | ✗ | 删除患者或医生账号 |

## 配置说明

| 配置项 | 文件 | 默认值 | 说明 |
|--------|------|--------|------|
| 后端端口 | [application.yml](springboot/src/main/resources/application.yml) | 9090 | Spring Boot 服务端口 |
| 数据库地址 | [application.yml](springboot/src/main/resources/application.yml) | localhost:3306/emrs | MySQL 连接 |
| 数据库用户 | [application.yml](springboot/src/main/resources/application.yml) | root | MySQL 用户名 |
| 数据库密码 | [application.yml](springboot/src/main/resources/application.yml) | 123456 | MySQL 密码 |
| 上传路径 | [application.yml](springboot/src/main/resources/application.yml) | E:/temp/uploads/ | 病历图片存储目录 |
| API 地址 | [request.js](src/utils/request.js) | localhost:9090/api | 前端请求后端地址 |
| 前端端口 | 默认 | 8080 | Vue CLI 开发服务器 |

> 部署到其他环境时需修改 `application.yml` 中的数据库连接信息和 `src/utils/request.js` 中的 API 地址。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 2 + Vue Router |
| UI 组件 | Element UI |
| HTTP 客户端 | Axios |
| 图表 | ECharts + vue-echarts |
| 后端框架 | Spring Boot 3 |
| ORM | MyBatis + PageHelper |
| 认证 | JWT (无状态 Token) |
| 密码加密 | BCrypt (Spring Security Crypto) |
| 数据库 | MySQL 8.0 |
| 构建工具 | Maven (后端) / Vue CLI (前端) |

## 常见问题

**Q: 启动后端报数据库连接失败？**
确认 MySQL 已启动，`emrs` 库已创建，`application.yml` 中用户名密码正确。

**Q: 前端页面空白或接口报错？**
确认后端已启动在 9090 端口，且前端 `src/utils/request.js` 中 `baseURL` 指向正确的后端地址。

**Q: 登录报"用户不存在"？**
确认已执行数据库初始化中的建表 SQL 和初始账号插入 SQL。

**Q: 上传图片失败？**
确认 `application.yml` 中 `upload.path` 对应的目录存在且有写入权限。
