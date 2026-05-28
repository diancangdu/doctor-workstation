# 医生工作站系统 — 新增功能记录

## 目录

| 编号 | 功能 | 涉及文件 | 跳转 |
|------|------|----------|------|
| 1 | 患者端过往病历查询 | MyMedicalRecord.vue · RecordRequest.java · MedicalRecord.xml | [→](#1-患者端过往病历查询) |
| 2 | 病历归档与检索 | MedicalRecord.java · MedicalRecord.xml · ViewMedicalRecord.vue | [→](#2-病历归档与检索) |
| 3 | 多图片上传与查看 | AddMedicalRecord.vue · ViewMedicalRecord.vue · AssignedPatients.vue · MyMedicalRecord.vue | [→](#3-多图片上传与查看) |
| 4 | 挂号对接 | Registration.java · DoctorQueue.vue · RegistrationManage.vue | [→](#4-挂号对接) |
| 5 | 标准1-8：病历增强 | MedicalRecord.java · DiagnosisTemplate · DrugInfo · 11 文件 | [→](#5-标准18病历增强) |
| 6 | 病历对比 | CompareMedicalRecord.vue | [→](#6-病历对比) |
| 7 | 标准7补全：审核/补增/痕迹 | MedicalRecordService.java · AuditLog · ViewMedicalRecord.vue | [→](#7-标准7补全审核补增痕迹) |
| 8 | 编辑病历（草稿） | AddMedicalRecord.vue · MedicalRecordController · MedicalRecord.xml | [→](#8-编辑病历草稿) |
| 9 | 医患关系页面重写 | BindPatientAndDoctor.vue | [→](#9-医患关系页面重写) |
| 10 | 处方多选 + 详情展示 | AddMedicalRecord.vue | [→](#10-处方多选--详情展示) |
| 11 | 既往史自动填入 | AddMedicalRecord.vue | [→](#11-既往史自动填入) |
| 12 | 图片缩略预览 + 放大 | AddMedicalRecord.vue | [→](#12-图片缩略预览--放大) |
| 13 | 归档导出统一 CSV | ExcelUtil.java · MedicalRecordService.java | [→](#13-归档导出统一-csv) |
| 14 | 实时时钟显示 | AppAside.vue | [→](#14-实时时钟显示) |
| 15 | 挂号实时录入按钮 | RegistrationManage.vue | [→](#15-挂号实时录入按钮) |

---

## 1. 患者端过往病历查询

### 需求背景

系统原有病历查询页面仅对医生和管理员开放。患者角色登录后无法查看自己的过往病历。

### 设计方案

在"个人中心"下为患者新增"我的病历"入口，患者登录后自动按身份过滤。后端复用 `/medicalRecord/list`，新增 `userId` 参数，Mapper 通过 JOIN `patient` 表过滤。前端新建独立页面。

### 改动文件

| 层级 | 文件 | 操作 |
|------|------|------|
| 后端 | [RecordRequest.java](../springboot/src/main/java/usc/emrsytem/springboot/controller/request/RecordRequest.java) | 修改：增加 `userId` 字段 |
| 后端 | [MedicalRecord.xml](../springboot/src/main/resources/mapper/MedicalRecord.xml) | 修改：JOIN patient 表按 userId 过滤 |
| 前端 | [MyMedicalRecord.vue](../src/views/MedicalRecord/MyMedicalRecord.vue) | 新建：患者病历页面 |
| 前端 | [router/index.js](../src/router/index.js) | 修改：新增路由 |
| 前端 | [AppAside.vue](../src/components/AppAside.vue) | 修改："个人中心"增加"我的病历"（仅患者可见） |

### 功能要点

- 患者登录后在"个人中心 > 我的病历"进入
- 自动按患者 userId 过滤，只看到自己的病历
- 支持按症状、诊断搜索，分页
- 查看详情包含关联处方和图片
- 患者不能删除或编辑病历

### 数据流

```
患者登录 → Cookie 存储 userId
  → GET /api/medicalRecord/list?userId={userId}
  → SQL: SELECT mr.* FROM medical_record mr
         INNER JOIN patient p ON mr.patient_id = p.patient_id
         WHERE mr.status = 'active' AND p.user_id = ?
  → 分页返回 → 前端渲染
```

---

## 2. 病历归档与检索

### 需求背景

原有系统中病历只能物理删除，无法归档保留。需要实现"归档"代替删除，已归档病历可恢复。

### 设计方案

在 `medical_record` 表增加 `status` 字段（`active` / `archived`）。查询默认过滤 `status='active'`，归档操作改为 SET status='archived'，支持恢复和查看已归档记录。

### 改动文件

| 层级 | 文件 | 操作 |
|------|------|------|
| 数据库 | [sql/migration-archive.sql](../sql/migration-archive.sql) | 新建：ALTER TABLE 增加 status 字段 |
| 数据库 | [sql/init.sql](../sql/init.sql) | 修改：建表语句同步增加 status |
| 后端 | [MedicalRecord.java](../springboot/src/main/java/usc/emrsytem/springboot/entity/MedicalRecord.java) | 修改：实体增加 `status` |
| 后端 | [RecordRequest.java](../springboot/src/main/java/usc/emrsytem/springboot/controller/request/RecordRequest.java) | 修改：增加 `status` 查询参数 |
| 后端 | [MedicalRecordMapper.java](../springboot/src/main/java/usc/emrsytem/springboot/mapper/MedicalRecordMapper.java) | 修改：增加 archive/restore 方法 |
| 后端 | [MedicalRecord.xml](../springboot/src/main/resources/mapper/MedicalRecord.xml) | 修改：查询加 status 过滤，新增归档/恢复 SQL |
| 后端 | [IMedicalRecordService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/IMedicalRecordService.java) | 修改：接口增加方法 |
| 后端 | [MedicalRecordService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/impl/MedicalRecordService.java) | 修改：实现归档/恢复逻辑 |
| 后端 | [MedicalRecordController.java](../springboot/src/main/java/usc/emrsytem/springboot/controller/MedicalRecordController.java) | 修改：新增 archive/restore 端点 |
| 前端 | [ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue) | 修改：表格增状态列，删除改归档，增加恢复和"查看已归档"切换 |

### 新增 API

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/medicalRecord/archive/{recordId}` | 归档病历（status → archived） |
| PUT | `/medicalRecord/restore/{recordId}` | 恢复病历（status → active） |

### 使用方式

- 医生/管理员在查询病历页点击**归档** → 病历标记为已归档
- 点击**查看已归档**切换 → 列出所有已归档病历
- 对已归档病历点击**恢复** → 回到活跃列表
- `GET /list` 默认只返回激活记录，传 `status=archived` 可查已归档

---

## 3. 多图片上传与查看

### 需求背景

新增病历时只能上传单张图片，查看详情时也仅显示一张。需要支持多图上传和多图展示。

### 设计方案

`chart` 字段存储逗号分隔的图片路径。前端上传组件启用 `multiple`，每张图单独上传，路径收集到数组，提交时 join 为字符串。查看端按逗号 split 后 `v-for` 渲染。

### 改动文件

| 文件 | 操作 |
|------|------|
| [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) | 修改：el-upload 加 multiple，chart 改为数组收集，提交时逗号拼接 |
| [ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue) | 修改：图片弹窗补全（之前缺失），改 v-for 多图遍历 |
| [AssignedPatients.vue](../src/views/MedicalRecord/AssignedPatients.vue) | 修改：图片弹窗补全，改多图遍历 |
| [MyMedicalRecord.vue](../src/views/MedicalRecord/MyMedicalRecord.vue) | 修改：图片弹窗改多图遍历 |

### 使用方式

- 新增病历时拖拽或选择多张图片，每张独立上传
- 已上传列表可逐张移除
- 查看详情时弹窗展示所有图片，向下排列

### 兼容性

`'url'.split(',')` → `['url']`，现有单图病历不受影响。

---

## 4. 挂号对接

### 需求背景

系统缺少挂号/排队环节，医生无法按队列接诊。增加挂号模块后覆盖"挂号 → 排队 → 接诊 → 病历书写"全门诊流程。

### 设计方案

新增 `registration` 表，记录患者挂号、排队序号、接诊状态。管理端创建挂号，医生端查看待诊队列、开始接诊、从队列直接跳转病历书写。

### 改动文件

| 层级 | 文件 | 操作 |
|------|------|------|
| 数据库 | [registration 表](../sql/init.sql) | 新建表 |
| 后端 | [Registration.java](../springboot/src/main/java/usc/emrsytem/springboot/entity/Registration.java) | 新建实体 |
| 后端 | [RegistrationMapper.java](../springboot/src/main/java/usc/emrsytem/springboot/mapper/RegistrationMapper.java) | 新建 Mapper |
| 后端 | [Registration.xml](../springboot/src/main/resources/mapper/Registration.xml) | 新建 SQL 映射 |
| 后端 | [IRegistrationService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/IRegistrationService.java) | 新建接口 |
| 后端 | [RegistrationService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/impl/RegistrationService.java) | 新建实现 |
| 后端 | [RegistrationController.java](../springboot/src/main/java/usc/emrsytem/springboot/controller/RegistrationController.java) | 新建控制器 |
| 前端 | [DoctorQueue.vue](../src/views/Registration/DoctorQueue.vue) | 新建：医生接诊排队页 |
| 前端 | [RegistrationManage.vue](../src/views/Registration/RegistrationManage.vue) | 新建：挂号管理页 |
| 前端 | [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) | 修改：支持从排队页传入 patientId 自动选中 |
| 前端 | [router/index.js](../src/router/index.js) | 新增路由 |
| 前端 | [AppAside.vue](../src/components/AppAside.vue) | 新增"接诊排队"和"挂号管理"菜单 |

### 新增 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/registration/add` | 创建挂号 |
| GET | `/registration/list` | 查询挂号列表（支持 status/doctorId 筛选） |
| GET | `/registration/queue` | 获取指定医生的待诊队列 |
| PUT | `/registration/start/{id}` | 开始接诊 |
| PUT | `/registration/complete/{id}` | 完成就诊 |
| PUT | `/registration/cancel/{id}` | 取消挂号 |

### 业务流程

```
管理端创建挂号 → 患者进入医生待诊队列
医生查看"接诊排队" → 点击"接诊" → 状态变为"接诊中"
医生点击"书写病历" → 跳转新增病历页，患者已自动选中
医生完成病历 → 回到排队页点击"完成就诊"
```

---

## 5. 标准1-8：病历增强

### 需求背景

对照门（急）诊医生工作站国家标准前8项，完善病历核心能力。

### 改动文件

| 层级 | 文件 | 操作 |
|------|------|------|
| 数据库 | doctor 表 | 新增 `title` 字段 |
| 数据库 | medical_record 表 | 新增 `chief_complaint`/`present_illness`/`past_history`/`physical_exam`/`record_status`/`void_reason`/`total_fee` |
| 数据库 | prescription 表 | 新增 `unit_price`/`total_price` |
| 数据库 | diagnosis_template 表 | 新建（20条模板数据） |
| 数据库 | drug_info 表 | 新建（20种药品数据） |
| 后端 | Doctor.java / MedicalRecord.java / Prescription.java | 实体增加新字段 |
| 后端 | DiagnosisTemplate.java · DrugInfo.java | 新建实体 |
| 后端 | DiagnosisTemplateMapper · DrugInfoMapper + XML | 新建 |
| 后端 | MedicalRecordMapper + XML | 新增 confirm/void/getById，insert/update 扩展新字段 |
| 后端 | IMedicalRecordService · MedicalRecordService | 新增 confirm/void 方法 |
| 后端 | MedicalRecordController | 新增 confirm/void 端点 |
| 后端 | CommonController | 新建：模板和药品查询 API |
| 前端 | AddMedicalRecord.vue | 表单重写：医生信息区、模板选择、主诉/现病史/既往史/体征/诊断/处方分区 |
| 前端 | ViewMedicalRecord.vue | 详情弹窗扩展新字段，操作列加确认/作废/打印，加 print 样式 |

### 功能要点

| 标准 | 实现 |
|------|------|
| 1 提取病人信息 | 挂号队列选患者自动填充姓名/性别/年龄 |
| 2 医生信息 | 病历表单显示科室+职称 |
| 3 诊疗信息 | 新增主诉/现病史/既往史/体格检查四个结构化字段 |
| 4 病历模板 | 20个诊断模板，下拉选择一键填充诊断/症状/处方 |
| 5 合理用药 | 20种药品，处方区输入后提示用法/禁忌/相互作用 |
| 6 费用信息 | 处方 unit_price/total_price，病历 total_fee |
| 7 审核锁定 | record_status(draft→confirmed→voided)，确认后锁定 |
| 8 打印 | 详情弹窗点"打印"，@media print 隐藏无关元素 |

---

## 6. 病历对比

### 需求背景

标准9要求医生可查询历次就诊信息并提供比较功能。

### 改动文件

| 文件 | 操作 |
|------|------|
| [CompareMedicalRecord.vue](../src/views/MedicalRecord/CompareMedicalRecord.vue) | 新建 |
| [router/index.js](../src/router/index.js) | 新增路由 |
| [AppAside.vue](../src/components/AppAside.vue) | "门诊病历管理"增加"病历对比"菜单 |

### 功能要点

- 下拉选择患者 → 显示性别/过敏史/既往病史标签
- 左右两栏列出该患者所有历次就诊（含状态标签）
- 各选一条记录 → 下方并排对比 10 个关键字段
- 每行标注"有变化"/"相同"

---

## 7. 标准7补全：审核/补增/痕迹

### 需求背景

标准7要求自动审核完整性、确认后不可更改、医嘱补增、作废及痕迹示踪。此前只完成了确认锁定和作废留因。

### 改动文件

| 层级 | 文件 | 操作 |
|------|------|------|
| 数据库 | audit_log 表 | 新建 |
| 数据库 | medical_record 表 | 新增 `supplement` 字段 |
| 后端 | AuditLog.java + AuditLogMapper + XML | 新建 |
| 后端 | MedicalRecord.java | 新增 `supplement` |
| 后端 | MedicalRecordMapper + XML | 新增 getById，update 扩展 supplement |
| 后端 | MedicalRecordService | 确认前校验完整性；作废/确认/补增均记 audit_log |
| 后端 | MedicalRecordController | 新增 supplement/auditLogs 端点 |
| 前端 | ViewMedicalRecord.vue | 确认/作废传 userId+username；新增补增按钮；详情弹窗时间线展示操作日志 |

### 新增 API

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/medicalRecord/confirm/{id}` | 确认病历（校验诊断+处方必填） |
| PUT | `/medicalRecord/supplement/{id}` | 补增医嘱（已确认病历追加补充） |
| GET | `/medicalRecord/auditLogs/{id}` | 获取操作日志 |

### 功能要点

- 确认时校验诊断和处方不为空
- 确认/作废/补增均记录 audit_log（操作人、时间、内容）
- 详情弹窗底部时间线展示全部操作记录
- 已确认病历可追加补增医嘱，不修改原文

---

## 8. 编辑病历（草稿）

### 需求背景

草稿状态病历需要修改功能，且编辑界面应与新增病历时一致（含模板选择、图片上传、药品提示等）。

### 设计方案

不在弹窗内编辑（空间不足），改为跳转到新增病历页面复用完整表单。通过 `?edit=recordId` 参数区分新增/编辑模式，页面加载时调 `getById` 接口获取已有数据并预填表单。

### 改动文件

| 层级 | 文件 | 操作 |
|------|------|------|
| 后端 | [MedicalRecordController.java](../springboot/src/main/java/usc/emrsytem/springboot/controller/MedicalRecordController.java) | 新增 `GET /getById/{id}` |
| 后端 | [IMedicalRecordService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/IMedicalRecordService.java) | 新增 `getById` |
| 后端 | [MedicalRecordService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/impl/MedicalRecordService.java) | 实现 `getById` |
| 后端 | [MedicalRecord.xml](../springboot/src/main/resources/mapper/MedicalRecord.xml) | update 补 chart 字段 |
| 前端 | [ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue) | "编辑"按钮跳转 AddMedicalRecord?edit=id |
| 前端 | [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) | 新增 editMode 支持：检测 ?edit、加载已有数据预填、提交时调 update 接口 |

### 功能要点

- 草稿行显示"编辑"按钮，跳转到完整病历表单
- 标题、按钮文字自动切换（新增/编辑）
- 所有字段预填：主诉/现病史/既往史/体征/诊断/症状/处方/备注/图片
- 模板选择器、药品提示、图片上传均可用
- 编辑提交调用 `/medicalRecord/update`，成功后返回上一页

---

## 9. 医患关系页面重写

### 需求背景

原有医患关系页面操作流程别扭：选医生→点"确定医生"→选患者→点"添加患者"，步骤多且不能查看已有关系。

### 改动文件

| 文件 | 操作 |
|------|------|
| [BindPatientAndDoctor.vue](../src/views/MedicalRecord/BindPatientAndDoctor.vue) | 重写 |

### 功能要点

- 上部：选医生 + 选患者（可多选） + 一键"绑定"
- 下部：表格展示所有已有关系（医生/科室/患者），每行有"解除"按钮
- 加载全量患者数据（pageSize:999），避免显示空白

---

## 10. 处方多选 + 详情展示

### 需求背景

新增病历时开具处方需要支持从已有处方库中选择，可多选，选中后显示完整信息。

### 改动文件

| 文件 | 操作 |
|------|------|
| [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) | 修改 |

### 功能要点

- 多选处方下拉框，默认无
- 悬停 0.3 秒后右侧弹出 tooltip 显示完整信息（药品/剂量/频次/疗程/用法/备注）
- 提交时自动拼接选中处方的文本到开具处方文本框

---

## 11. 既往史自动填入

### 需求背景

选择患者后，既往史字段应自动填入该患者的病史和过敏史，减少医生重复录入。

### 改动文件

| 文件 | 操作 |
|------|------|
| [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) | 修改 |

### 功能要点

- `selectPatient` 中读取患者 `medicalHistory` 和 `allergies`
- 自动填入格式："既往病史：xxx；过敏史：xxx"
- 仅新增模式自动填入，编辑模式保留已有内容
- 医生可手动修改

---

## 12. 图片缩略预览 + 放大

### 需求背景

上传图片后需能直观看到缩略预览，点击可放大查看。

### 改动文件

| 文件 | 操作 |
|------|------|
| [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) | 修改 |

### 功能要点

- 上传完成后下方显示 100x100 缩略图
- 仅显示已完成上传的图片（`v-if="img.url"`）
- 点击缩略图弹出 60% 宽度大图弹窗

---

## 13. 归档导出统一 CSV

### 需求背景

病历归档操作改为导出到本地 Excel（CSV），统一归档文件便于管理。

### 改动文件

| 层级 | 文件 | 操作 |
|------|------|------|
| 后端 | [ExcelUtil.java](../springboot/src/main/java/usc/emrsytem/springboot/utils/ExcelUtil.java) | 新建：CSV 导出工具 |
| 后端 | [MedicalRecordService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/impl/MedicalRecordService.java) | 修改：归档时导出 CSV，恢复时追加记录 |
| 前端 | [ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue) | 修改：归档确认提示文字 |

### 功能要点

- 归档和恢复操作统一写入 `docs/archives/archives.csv`
- 首次操作自动创建文件并写表头
- 每次操作追加一行（操作列标"归档"或"恢复"）
- 含 BOM 头兼容 Excel 中文打开
- 文件路径写入病历备注

---

## 14. 实时时钟显示

### 需求背景

全局显示当前时间，便于医生随时掌握时间。

### 改动文件

| 文件 | 操作 |
|------|------|
| [AppAside.vue](../src/components/AppAside.vue) | 修改 |

### 功能要点

- 侧边栏顶部显示实时时钟（HH:MM:SS + 日期）
- 每秒更新，80px 高度与顶部 header 对齐
- 背景色与系统 header 一致（`#53a8ff`）
- 侧边栏收起时只显示 HH:MM
- 菜单滚动条隐藏

---

## 15. 挂号实时录入按钮

### 需求背景

挂号管理页面预留实时录入入口。

### 改动文件

| 文件 | 操作 |
|------|------|
| [RegistrationManage.vue](../src/views/Registration/RegistrationManage.vue) | 修改 |

### 功能要点

- 挂号管理页新增"挂号实时录入"按钮
- 位于"新建挂号"旁边，暂未绑定事件
