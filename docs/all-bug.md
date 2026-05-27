# 医生工作站系统 — 全部 Bug 修复记录

## 目录

| 编号 | Bug 描述 | 涉及文件 | 跳转 |
|------|----------|----------|------|
| 1 | 查看名下患者偶现重复数据 | [AssignedPatients.vue](../src/views/MedicalRecord/AssignedPatients.vue) | [→](#bug-1查看名下患者偶现重复数据) |
| 2 | 管理员账号可被删除 | [UserTable.vue](../src/views/user/UserTable.vue) · [UserService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/impl/UserService.java) | [→](#bug-2管理员账号可被删除) |
| 3 | 查询用户页面操作列 UI 拥挤 | [UserTable.vue](../src/views/user/UserTable.vue) | [→](#bug-3查询用户页面操作列-ui-拥挤) |
| 4 | updated() 无限循环导致密码被破坏 | [UserTable.vue](../src/views/user/UserTable.vue) | [→](#bug-4updated-无限循环导致密码被破坏) |
| 5 | 医生角色越权操作 + 修改密码无响应 | [UserTable.vue](../src/views/user/UserTable.vue) | [→](#bug-5医生角色在查询用户页面存在越权操作) |
| 6 | 修改保存时报服务器异常 | [UserTable.vue](../src/views/user/UserTable.vue) | [→](#bug-6修改用户信息保存时报服务器异常) |
| 7 | 性别单选不显示上次保存的值 | [UserTable.vue](../src/views/user/UserTable.vue) | [→](#bug-7修改弹窗中性别单选不显示上次保存的值) |
| 8 | 查询病历缺少就诊时间 | [ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue) | [→](#bug-8医生管理员查询病历缺少就诊时间) |
| 9 | 病历详情中图片查看按钮无响应 | [ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue) · [AssignedPatients.vue](../src/views/MedicalRecord/AssignedPatients.vue) | [→](#bug-9病历详情中图片查看按钮无响应) |
| 10 | 完成就诊取消后出现 error | [DoctorQueue.vue](../src/views/Registration/DoctorQueue.vue) | [→](#bug-10完成就诊取消后出现-error) |
| 11 | 病历模板缺少默认选项 + 取消按钮无响应 | [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) | [→](#bug-11病历模板缺少默认选项--取消按钮无响应) |
| 12 | 挂号管理列表排序不对 | [Registration.xml](../springboot/src/main/resources/mapper/Registration.xml) | [→](#bug-12挂号管理列表排序不对) |
| 13 | 个人中心性别显示为英文 | [PersonalCenter.vue](../src/views/person/PersonalCenter.vue) | [→](#bug-13个人中心性别显示为英文) |
| 14 | 编辑病历上传图片后不显示 | [MedicalRecord.xml](../springboot/src/main/resources/mapper/MedicalRecord.xml) | [→](#bug-14编辑病历上传图片后不显示) |
| 15 | 编辑时未自动录入草稿信息 | [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) · [MedicalRecordController.java](../springboot/src/main/java/usc/emrsytem/springboot/controller/MedicalRecordController.java) | [→](#bug-15编辑时未自动录入草稿信息) |
| 16 | 病历详情图片查看消失 | [ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue) | [→](#bug-16病历详情图片查看消失) |
| 17 | 医患关系表部分患者显示为空 | [BindPatientAndDoctor.vue](../src/views/MedicalRecord/BindPatientAndDoctor.vue) | [→](#bug-17医患关系表部分患者显示为空) |
| 18 | 保存病历/编辑后不返回原页面 | [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue) | [→](#bug-18保存病历编辑后不返回原页面) |

---

## Bug 1：查看名下患者偶现重复数据

**涉及文件：** [src/views/MedicalRecord/AssignedPatients.vue](../src/views/MedicalRecord/AssignedPatients.vue)

### 问题描述

在"查看名下患者"页面，有时会出现两个完全相同的患者信息。

### 根因分析

`created()` 中 `listMedicalRecord()` 的回调里多余地调用了 `this.loadPatients()`：

```javascript
created() {
    this.load()              // ①
    this.loadPatients()      // ② → 成功后调用 loadRelation()
    this.listMedicalRecord() // ③ → 成功后调用 this.loadPatients() → loadRelation()
}
```

导致 `loadRelation()` 被触发两次，两次异步 API 返回后都 push 数据到同一个数组，产生重复。"有时候"出现取决于 API 返回的时序。

### 修复内容

1. 移除 `listMedicalRecord()` 中多余的 `this.loadPatients()` 调用
2. `loadRelation()` 中 push 前加去重检查

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | 仅"查看名下患者"页面 |
| 副作用 | 无 |

---

## Bug 2：管理员账号可被删除

**涉及文件：**
- [src/views/user/UserTable.vue](../src/views/user/UserTable.vue)
- [springboot/…/service/impl/UserService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/impl/UserService.java)

### 问题描述

在"查询用户"页面中，管理员账号与其他用户一样显示"删除"按钮，点击即可删除管理员。

### 根因分析

前端操作列删除按钮无角色判断，后端 `UserService.deleteUserById()` 也未拦截管理员角色。

### 修复内容

**前端** — 删除按钮加 `v-if="scoped.row.role !== '管理员' && user.role === 'admin'"`

**后端** — 角色转换后增加：
```java
if(Objects.equals(user.getRole(), "admin")) {
    throw new ServiceException("管理员账号不可删除");
}
```

### 影响评估

| 项目 | 说明 |
|------|------|
| 安全层级 | 前端隐藏 + 后端拦截，双重保护 |
| 副作用 | 无 |

---

## Bug 3：查询用户页面操作列 UI 拥挤

**涉及文件：** [src/views/user/UserTable.vue](../src/views/user/UserTable.vue)

### 问题描述

"查询用户"页面操作列 200px 内挤了 4 个 `el-link`，间距不足，`disabled` 属性无效。

### 根因分析

`el-link` 组件在窄列宽下拥挤，`disabled` 不阻止点击事件。

### 修复内容

`el-link` 改为 `el-button type="text" size="small"`，用 flexbox 布局（`gap: 4px`），列宽增至 280px，去掉无效的 `disabled`。

### 影响评估

| 项目 | 说明 |
|------|------|
| 操作列宽度 | 200px → 280px |
| 副作用 | 无 |

---

## Bug 4：updated() 无限循环导致密码被破坏

**涉及文件：** [src/views/user/UserTable.vue](../src/views/user/UserTable.vue)

### 问题描述

医生和管理员账号密码被意外覆盖，登录时报"用户名或密码错误"。

### 根因分析

`updated()` 钩子中调用了 `loadPatients/Doctors/Admins()` + `handleDisplayTable()` + `handleDisplaySelectedUser()`，形成"API 返回 → data 变化 → 渲染 → updated() → API 请求 → …"的死循环。`handleDisplayTable()` 直接 mutate 响应式数据，极端时序下可能将异常密码数据提交到后端覆盖原密码。

### 修复内容

1. 删除 `updated()` 钩子
2. `handleDisplayTable()` 移到 `load()` 回调中，仅在获取新数据时执行一次
3. `handleDisplaySelectedUser()` 移到 `viewDetails()` 末尾，仅在查看详情时执行

### 影响评估

| 项目 | 说明 |
|------|------|
| API 请求量 | 从每秒数十次降至正常 |
| 密码安全性 | 消除意外覆盖风险 |
| 性能 | 页面不再持续消耗 CPU 和网络资源 |

---

## Bug 5：医生角色在查询用户页面存在越权操作

**涉及文件：** [src/views/user/UserTable.vue](../src/views/user/UserTable.vue)

### 问题描述

医生登录后：
1. 能修改和保存管理员账号信息
2. 能看到其他用户的"修改密码"按钮
3. 自己账号的"修改密码"按钮点击无反应

### 根因分析

1. 操作列按钮未做角色权限控制
2. 修改密码的 `el-dialog` 模板完全缺失——有变量和方法，但没有对话框 HTML

### 修复内容

1. 操作按钮增加权限判断：修改仅管理员可见，删除仅管理员可见且不可删管理员，修改密码仅自己行或管理员可见
2. 补上缺失的修改密码 `el-dialog`

权限模型：

| 操作 | 管理员 | 医生 |
|------|--------|------|
| 详细 | 所有人 | 所有人 |
| 修改 | 仅管理员 | 隐藏 |
| 修改密码 | 所有人 + 可改任何人 | 仅自己行 |
| 删除 | 仅管理员，不可删管理员 | 隐藏 |

### 影响评估

| 项目 | 说明 |
|------|------|
| 越权修改 | 医生不再能看到修改和删除按钮 |
| 副作用 | 管理员权限不受影响 |

---

## Bug 6：修改用户信息保存时报服务器异常

**涉及文件：** [src/views/user/UserTable.vue](../src/views/user/UserTable.vue)

### 问题描述

管理员修改用户信息后点击保存，后端返回"服务器异常，请联系管理员"。

### 根因分析

后端日志：`HttpRequestMethodNotSupportedException: Request method 'POST' is not supported`。

`updateUserRequest()` 中角色比对用了中文（`'患者'`/`'医生'`/`'管理员'`），但 `selectedUser.user.role` 来自 API 原始数据是英文（`'patient'`/`'doctor'`/`'admin'`）。三个 if 全都不匹配，`requestUrl` 为 `undefined`。

此 bug 在 Bug 4 修复前被 `updated()` 中的 `handleDisplaySelectedUser()` 掩盖（该方法会将角色转成中文）。

### 修复内容

角色比对从中文改为英文：`'患者'` → `'patient'`, `'医生'` → `'doctor'`, `'管理员'` → `'admin'`。

### 影响评估

| 项目 | 说明 |
|------|------|
| 修复范围 | 管理员修改所有角色的保存操作 |
| 根本原因 | Bug 4 修复后暴露的隐藏 bug |

---

## Bug 7：修改弹窗中性别单选不显示上次保存的值

**涉及文件：** [src/views/user/UserTable.vue](../src/views/user/UserTable.vue)

### 问题描述

管理员修改用户信息时，性别单选按钮不会勾选已保存的值。

### 根因分析

`viewUpdateForm()` 中**先打开对话框、后填充数据**：

```javascript
this.updateDialogFormVisible = true;  // ← 先打开
for (...) {
    this.tempData = {...this.patientData[i]};  // ← 后赋值
}
```

对话框打开时 `tempData` 还是默认值（无 `gender` 字段），`el-radio` 的 `v-model` 为 `undefined`，不匹配任何选项。

**附带修复：** `handleDisplaySelectedUser()` 中 gender 访问路径 `selectedUser.user.gender` 应为 `selectedUser.gender`（gender 是 Patient/Doctor 的顶层字段）。

### 修复内容

1. `viewUpdateForm()` 三个分支中 `updateDialogFormVisible = true` 移到循环赋值之后
2. 修正 gender 访问路径

### 影响评估

| 项目 | 说明 |
|------|------|
| 修复范围 | 修改弹窗中性别单选正确回显 |
| 根因 | 对话框先渲染后赋值 |

---

## Bug 8：医生/管理员查询病历缺少就诊时间

**涉及文件：** [src/views/MedicalRecord/ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue)

### 问题描述

医生/管理员在"查询病历"页面的表格和详情弹窗中均看不到就诊时间。

### 根因分析

`MedicalRecord` 实体的 `createdAt` 字段在后端返回中一直存在，但前端 `ViewMedicalRecord.vue` 的表格列和详情弹窗中都没有渲染该字段。

### 修复内容

1. 新增 `formatDate()` 方法，格式化 ISO 时间为 `YYYY-MM-DD HH:mm`
2. 表格新增"就诊时间"列（宽 160px）
3. 详情弹窗新增"就诊时间"行

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | ViewMedicalRecord.vue（医生和管理员共用） |
| 副作用 | 无 |

---

## Bug 9：病历详情中图片查看按钮无响应

**涉及文件：**
- [src/views/MedicalRecord/ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue)
- [src/views/MedicalRecord/AssignedPatients.vue](../src/views/MedicalRecord/AssignedPatients.vue)

### 问题描述

在病历详情弹窗中点击"查看"图片按钮没有任何反应。

### 根因分析

同 Bug 5 的"修改密码无响应"——`imageDialogVisible` 变量和 `viewImage()` 方法都已定义，但模板中完全缺失了对应的 `el-dialog` 组件。点击按钮只是把变量设为 `true`，没有对话框可以渲染。

### 修复内容

补上缺失的图片弹窗模板：

```html
<el-dialog :visible.sync="imageDialogVisible" width="50%" title="查看图像">
  <div v-if="selectedRow?.chart">
    <img v-for="(img, index) in selectedRow.chart.split(',')"
         :key="index" :src="img"
         style="width: 100%; margin-bottom: 10px;"
         alt="病历图片" />
  </div>
  <span v-else>无图片</span>
</el-dialog>
```

同时改为支持多图展示（`v-for` 遍历逗号分隔的图片路径）。

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | ViewMedicalRecord.vue + AssignedPatients.vue |
| 修复前 | 点击"查看"无任何反应 |
| 修复后 | 弹窗展示所有病历图片 |
| 附带改进 | 支持多图显示，兼容单图 |

---

## Bug 10：完成就诊取消后出现 error

**涉及文件：** [DoctorQueue.vue](../src/views/Registration/DoctorQueue.vue)

### 问题描述

医生在接诊排队页点击"完成就诊"，弹出确认框后点取消，页面显示 error。

### 根因分析

`$confirm` 的 Promise 在点击取消时 reject，只有 `.then()` 没有 `.catch()`，未捕获的异常导致 error。

### 修复内容

```diff
       }).then(() => {
         request.put('/registration/complete/' + row.registrationId).then(res => {
           ...
         })
-      })
+      }).catch(() => {})
     },
```

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | DoctorQueue.vue |
| 修复前 | 点取消报 error |
| 修复后 | 点取消静默关闭 |

---

## Bug 11：病历模板缺少默认选项 + 取消按钮无响应

**涉及文件：** [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue)

### 问题描述

1. 病历模板下拉框没有"无"选项，选中后无法取消
2. 新增病历页面底部"取消"按钮点击无反应

### 根因分析

1. 模板 `el-select` 缺少默认选项，且 `filterable` 会过滤掉空选项
2. 取消按钮没有绑定 `@click` 事件

### 修复内容

1. 模板选择器新增 `el-option label="无（手动填写）" :value="null"` 作为默认选项，移除 `filterable`
2. 取消按钮加 `@click="$router.back()"`

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | AddMedicalRecord.vue |
| 副作用 | 无 |

---

## Bug 12：挂号管理列表排序不对

**涉及文件：** [Registration.xml](../springboot/src/main/resources/mapper/Registration.xml)

### 问题描述

挂号管理页面列表按挂号时间倒序排列，不按就诊优先级排序。

### 根因分析

`listRegistrations` 查询使用 `ORDER BY r.registration_time DESC`，待诊和已完成的记录混在一起。

### 修复内容

改为按状态优先级 + 排队序号排序：

```sql
ORDER BY field(r.status, 'waiting', 'in_progress', 'completed', 'cancelled'), r.queue_number
```

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | 挂号管理页 + 挂号查询 API |
| 副作用 | 无 |

---

## Bug 13：个人中心性别显示为英文

**涉及文件：** [PersonalCenter.vue](../src/views/person/PersonalCenter.vue)

### 问题描述

个人中心页面的性别字段直接显示数据库原值 `male`/`female`，而非中文。

### 根因分析

模板中 `{{ personData?.gender }}` 直接输出原始值，未做显示转换。

### 修复内容

患者和医生两处性别显示改为：

```html
{{ personData?.gender === 'male' ? '男' : '女' }}
```

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | PersonalCenter.vue（患者+医生） |
| 副作用 | 无 |

---

## Bug 14：编辑病历上传图片后不显示

**涉及文件：** [MedicalRecord.xml](../springboot/src/main/resources/mapper/MedicalRecord.xml)

### 问题描述

编辑病历草稿时上传了新图片并保存，但再次查看详情时"图像"字段消失。

### 根因分析

`updateMedicalRecord` 的 MyBatis 动态 SQL 中缺少 `chart` 字段的更新条件。前端把图片路径传过来了，后端 mapper 直接跳过没更新。

### 修复内容

```diff
             <if test="symptoms != null">symptoms = #{symptoms},</if>
+            <if test="chart != null">chart = #{chart},</if>
             <if test="prescription != null">prescription = #{prescription},</if>
```

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | 编辑病历时上传图片的保存 |
| 副作用 | 无 |

---

## Bug 15：编辑时未自动录入草稿信息

**涉及文件：**
- [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue)
- [MedicalRecordController.java](../springboot/src/main/java/usc/emrsytem/springboot/controller/MedicalRecordController.java)
- [IMedicalRecordService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/IMedicalRecordService.java)
- [MedicalRecordService.java](../springboot/src/main/java/usc/emrsytem/springboot/service/impl/MedicalRecordService.java)

### 问题描述

从病历列表点"编辑"跳转到新增病历页面后，表单为空白，没有自动填入该草稿的已有信息。

### 根因分析

前端 `loadExistingRecord` 使用 `/medicalRecord/list?pageSize=1` 查询，默认只返回 1 条活跃记录，几乎不可能匹配到目标病历。

### 修复内容

1. 后端新增 `GET /medicalRecord/getById/{recordId}` 接口
2. 前端改用 `request.get('/medicalRecord/getById/' + id)` 精确查询
3. 加载后填充所有表单字段（含图片列表还原）

### 影响评估

| 项目 | 说明 |
|------|------|
| 修复前 | 编辑页面表单空白 |
| 修复后 | 完整预填草稿信息 |

---

## Bug 16：病历详情图片查看消失

**涉及文件：** [ViewMedicalRecord.vue](../src/views/MedicalRecord/ViewMedicalRecord.vue)

### 问题描述

病历详情中"图像：查看"链接不显示，即使该病历有上传图片。

### 根因分析

`viewDetail` 中判断图片是否存在的条件为 `chart === '' || chart === null`，未覆盖 `chart === undefined` 的情况。当 API 返回的 JSON 中缺少该字段时值为 `undefined`，被误判为有图片但链接不渲染。

### 修复内容

```diff
- if (this.selectedRow.chart === '' || this.selectedRow.chart === null) {
+ if (!this.selectedRow.chart || this.selectedRow.chart === '') {
```

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | ViewMedicalRecord.vue |
| 副作用 | 无 |

---

## Bug 17：医患关系表部分患者显示为空

**涉及文件：** [BindPatientAndDoctor.vue](../src/views/MedicalRecord/BindPatientAndDoctor.vue)

### 问题描述

医患关系管理页面中，部分绑定关系的患者姓名显示为空。

### 根因分析

加载关系数据时 `request.get('/user/patient')` 未传 `pageSize`，使用默认分页大小。当患者数量超过默认页大小时，后面的患者不在返回列表中，导致姓名匹配失败。

### 修复内容

```diff
- request.get('/user/patient')
+ request.get('/user/patient', { params: { pageSize: 999 } })
```

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | BindPatientAndDoctor.vue |
| 副作用 | 无 |

---

## Bug 18：保存病历/编辑后不返回原页面

**涉及文件：** [AddMedicalRecord.vue](../src/views/MedicalRecord/AddMedicalRecord.vue)

### 问题描述

接诊排队点击"书写病历"→保存草稿后停留在当前页面，不跳回排队页。查询病历点击"编辑"→保存修改后也同样不返回。

### 根因分析

`onSubmit` 中返回上一页的逻辑被 `if (this.editMode)` 条件包裹：

```javascript
if (this.editMode) this.$router.back();
```

新增病历时 `editMode` 为 `false`，`$router.back()` 不执行。

### 修复内容

去掉条件，保存成功后统一返回：

```diff
- if (this.editMode) this.$router.back();
+ this.$router.back();
```

### 影响评估

| 项目 | 说明 |
|------|------|
| 影响范围 | 新增病历 + 编辑病历 |
| 副作用 | 无 |
