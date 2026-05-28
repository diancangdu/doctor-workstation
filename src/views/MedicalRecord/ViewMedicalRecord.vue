<script>

import request from "@/utils/request";
import Cookies from "js-cookie";


export default {
  name: "ViewMedicalRecord",
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {},
      params: {
        patientId: '',
        symptoms: '',
        diagnosis: '',
        status: '',
        pageSize: 13,
        pageNum: 1
      },
      showArchived: false, // 是否查看已归档病历
      // 搜索病人选项
      patientOptions: [],
      // 病历数据
      medicalRecordData: [],
      // 病人数据
      patientData: [],
      // 医生数据
      doctorData: [],
      total: 0, // 总条数
      detailVisible: false, // 详情弹窗
      editVisible: false, // 编辑弹窗
      selectedRow: null, // 选中的行
      imageDialogVisible: false, // 图片弹窗
      prescriptionData: [],
      selectedPrescription: [],
      auditLogs: [],
      editForm: {},
      selectFromTable: true,

      prescriptionId: null,
      prescriptionOptions: [],
      rules: {
        diagnosis: [{ required: true, message: '请输入诊断结果', trigger: 'blur' }],
      }
    }
  },
  created() {
    this.listMedicalRecord()
    this.listPrescription()
  },
  methods: {
    listMedicalRecord() {
      // console.log(this.params)
      request({
        url: '/medicalRecord/list',
        method: 'get',
        params: this.params
      }).then(res => {
        if (res.code === '200') {
          this.medicalRecordData = res.data.list
          this.loadPatients()
          this.loadDoctors()
          // console.log(this.medicalRecordData)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reset() {
      this.showArchived = false
      this.params = {
        patientId: '',
        symptoms: '',
        diagnosis: '',
        status: '',
        pageSize: 13,
        pageNum: 1
      }
      this.listMedicalRecord()
    },
    // 处理分页
    handleCurrentPageChange(pageNum) {
      this.params.pageNum = pageNum
    },
    viewDetail(row) {
      this.selectedPrescription = []
      this.selectedRow = row
      // console.log(this.selectedRow)
      if (!this.selectedRow.chart || this.selectedRow.chart === '') {
        this.$set(this.selectedRow, 'existChart', false)
      } else {
        this.$set(this.selectedRow, 'existChart', true)
      }
      for (let i = 0; i < this.prescriptionData.length; i++) {
        if (this.prescriptionData[i].prescriptionId === this.selectedRow.prescriptionId) {
          this.selectedPrescription.push(this.prescriptionData[i])
          break
        }
      }
      // console.log(this.selectedPrescription)
      if (this.selectedPrescription.length === 0) {
        this.$set(this.selectedRow, 'existPrescription', false)
      } else {
        this.$set(this.selectedRow, 'existPrescription', true)
      }
      this.loadAuditLogs(row.recordId);
      this.detailVisible = true
    },
    loadAuditLogs(recordId) {
      this.auditLogs = [];
      request.get('/medicalRecord/auditLogs/' + recordId).then(res => {
        if (res.code === '200') this.auditLogs = res.data || [];
      });
    },
    edit(row) {
      this.$router.push({ path: '/AddMedicalRecord', query: { edit: row.recordId } });
    },
    archiveRecord(row) {
      this.$confirm('归档后病历将追加到 docs/archives/archives.csv，并在常规列表中隐藏。是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: '/medicalRecord/archive/' + row.recordId,
          method: 'put'
        }).then(res => {
          if (res.code === '200') {
            this.$message.success('归档成功')
            this.listMedicalRecord()
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消归档' })
      })
    },
    restoreRecord(row) {
      this.$confirm('确定恢复该病历到活跃列表?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        request({
          url: '/medicalRecord/restore/' + row.recordId,
          method: 'put'
        }).then(res => {
          if (res.code === '200') {
            this.$message.success('恢复成功')
            this.listMedicalRecord()
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消恢复' })
      })
    },
    deleteRecord(row) {
      this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: 'medicalRecord/delete/' + row.recordId,
          method: 'delete'
        }).then(res => {
          if (res.code === '200') {
            this.$message({ type: 'success', message: '删除成功!' })
            this.listMedicalRecord()
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消删除' })
      })
    },
    toggleArchived() {
      this.showArchived = !this.showArchived
      this.params.status = this.showArchived ? 'archived' : ''
      this.listMedicalRecord()
    },
    // 获取患者信息
    loadPatients() {
      request.get('/user/patient').then(res => {
        // console.log(res)
        if (res.code === '200') {
          this.patientData = res.data.list
          // console.log(this.patientData)
          this.generatePatientOptions()
          try {
            for (let i = 0; i < this.medicalRecordData.length; i++) {
              for (let j = 0; j < this.patientData.length; j++) {
                if (this.medicalRecordData[i].patientId === this.patientData[j].patientId) {
                  // this.medicalRecordData[i].patientName = this.patientData[j].user.username
                  this.$set(this.medicalRecordData[i], 'patientName', this.patientData[j].user.username)
                }
              }
            }
            // console.log(this.medicalRecordData)
          } catch (e) {
            this.$message.error(e)
          }
        }

      })
    },
    // 生成病人选项
    generatePatientOptions() {
      this.patientOptions = this.patientData.map(patient => ({
        value: patient.patientId,
        label: `${patient.user.username} Id: ${patient.patientId}`
      }))
    },
    // 获取医生信息
    loadDoctors() {
      request.get('/user/doctor').then(res => {
        // console.log("res:" + JSON.stringify(res, null, 2))
        if (res.code === '200') {
          this.doctorData = res.data
          // console.log(this.doctorData)
          try {
            for (let i = 0; i < this.medicalRecordData.length; i++) {
              for (let j = 0; j < this.doctorData.length; j++) {
                if (this.medicalRecordData[i].doctorId === this.doctorData[j].doctorId) {
                  // this.medicalRecordData[i].doctorName = this.doctorData[j].user.username
                  this.$set(this.medicalRecordData[i], 'doctorName', this.doctorData[j].user.username)
                }
              }
            }
            // console.log(this.medicalRecordData)
          } catch (error) {
            console.error(error)
          }
        }
      })
    },
    // 查看图片
    viewImage() {
      this.imageDialogVisible = true; // 打开
    },
    listPrescription() {
      // console.log(this.params)
      request({
        url: '/prescription/list',
        method: 'get',
        params: this.params
      }).then(res => {
        if (res.code === '200') {
          this.prescriptionData = res.data.list
          // console.log(this.prescriptionData)
          this.generateOptions(this.prescriptionData, 'prescription')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    // 处理是否从表选择
    handleChange() {
      if (this.selectFromTable === false) {
        this.editForm.prescriptionId = null;
      }
    },
    generateOptions(data, type) {
      if (type === 'prescription') {
        this.prescriptionOptions = data.map(item => {
          return {
            value: item.prescriptionId.toString(),
            label: `药品名称：${item.medicationName} 开具医生： ${item.doctorName} 备注：${item.remarks}` // 拼接username和userId
          }
        })
        console.log(this.prescriptionOptions)
      }
    },
    onSubmit() {
      this.$confirm('确认新增处方吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$refs.form.validate(valid => {
          if (valid) {
            // console.log(this.editForm)
            request({
              url: '/medicalRecord/update',
              method: 'post',
              data: this.editForm
            }).then(res => {
              if (res.code === '200') {
                this.$message.success('编辑成功')
                this.editVisible = false
                this.listMedicalRecord()
              } else {
                this.$message.error(res.msg)
              }
            })
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消编辑'
        })
      })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const h = String(d.getHours()).padStart(2, '0')
      const min = String(d.getMinutes()).padStart(2, '0')
      return `${y}-${m}-${day} ${h}:${min}`
    },
    confirmRecord(row) {
      this.$confirm('确认后病历将被锁定，不可再修改。是否确认?', '确认病历', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        request.put('/medicalRecord/confirm/' + row.recordId + '?userId=' + this.user.userId + '&username=' + encodeURIComponent(this.user.username)).then(res => {
          if (res.code === '200') { this.$message.success('病历已确认'); this.listMedicalRecord(); }
          else this.$message.error(res.msg);
        })
      }).catch(() => {})
    },
    voidRecord(row) {
      this.$prompt('请输入作废原因', '作废病历', {
        confirmButtonText: '确定', cancelButtonText: '取消',
        inputType: 'textarea', inputValidator: v => v ? true : '原因不能为空'
      }).then(({ value }) => {
        request.put('/medicalRecord/void/' + row.recordId + '?voidReason=' + encodeURIComponent(value) + '&userId=' + this.user.userId + '&username=' + encodeURIComponent(this.user.username)).then(res => {
          if (res.code === '200') { this.$message.success('病历已作废'); this.listMedicalRecord(); }
          else this.$message.error(res.msg);
        })
      }).catch(() => {})
    },
    supplementRecord(row) {
      this.$prompt('请输入补增医嘱内容', '补增医嘱', {
        confirmButtonText: '确定', cancelButtonText: '取消',
        inputType: 'textarea'
      }).then(({ value }) => {
        request.put('/medicalRecord/supplement/' + row.recordId + '?userId=' + this.user.userId + '&username=' + encodeURIComponent(this.user.username), { supplement: value }).then(res => {
          if (res.code === '200') { this.$message.success('补增成功'); this.listMedicalRecord(); }
          else this.$message.error(res.msg);
        })
      }).catch(() => {})
    },
    printRecord(row) {
      this.viewDetail(row);
      this.$nextTick(() => window.print());
    },
  }
}
</script>

<template>
  <div class="medical-record-page">
    <h2 class="page-title">查询病历</h2>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-input class="search-input" placeholder="搜索症状" v-model="params.symptoms" />
      <el-input class="search-input" placeholder="搜索诊断" v-model="params.diagnosis" />
      <el-select v-model="params.patientId" filterable placeholder="搜索病人" class="search-input">
        <el-option
            v-for="item in patientOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            :disabled="item.disabled"
        />
      </el-select>
      <el-button class="search-button" type="primary" @click="listMedicalRecord">
        <i class="el-icon-search"></i>搜索
      </el-button>
      <el-button class="search-button" type="warning" @click="reset">
        <i class="el-icon-refresh"></i>重置
      </el-button>
      <el-button class="search-button" :type="showArchived ? 'info' : 'default'" @click="toggleArchived">
        <i class="el-icon-folder"></i>{{ showArchived ? '查看活跃' : '查看已归档' }}
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="medicalRecordData" stripe border class="medical-record-table">
      <el-table-column prop="recordId" label="病历ID" width="70" />
      <el-table-column prop="patientId" label="患者ID" width="70" />
      <el-table-column prop="patientName" label="患者姓名" />
      <el-table-column prop="doctorId" label="医生ID" width="70" />
      <el-table-column prop="doctorName" label="医生姓名" />
      <el-table-column prop="diagnosis" label="诊断" />
      <el-table-column prop="symptoms" label="症状" />
      <el-table-column prop="remarks" label="备注" />
      <el-table-column label="状态" width="90">
        <template v-slot="scope">
          <el-tag size="small" :type="scope.row.recordStatus === 'confirmed' ? 'primary' : scope.row.recordStatus === 'voided' ? 'danger' : scope.row.status === 'archived' ? 'info' : 'warning'">
            {{ scope.row.recordStatus === 'confirmed' ? '已确认' : scope.row.recordStatus === 'voided' ? '已作废' : scope.row.status === 'archived' ? '已归档' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="就诊时间" width="160">
        <template v-slot="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300">
        <template v-slot="scoped">
          <el-link type="primary" class="action-link" @click="viewDetail(scoped.row)">详情</el-link>
          <el-link v-if="scoped.row.recordStatus === 'draft'" style="color:#E6A23C" class="action-link" @click="edit(scoped.row)">编辑</el-link>
          <el-link v-if="scoped.row.recordStatus === 'draft'" type="success" class="action-link" @click="confirmRecord(scoped.row)">确认</el-link>
          <el-link v-if="scoped.row.recordStatus === 'confirmed'" type="danger" class="action-link" @click="voidRecord(scoped.row)">作废</el-link>
          <el-link v-if="scoped.row.recordStatus === 'confirmed'" style="color:#67C23A" class="action-link" @click="supplementRecord(scoped.row)">补增</el-link>
          <el-link v-if="scoped.row.status !== 'archived'" type="warning" class="action-link" @click="archiveRecord(scoped.row)">归档</el-link>
          <el-link v-else type="success" class="action-link" @click="restoreRecord(scoped.row)">恢复</el-link>
          <el-link type="info" class="action-link" @click="printRecord(scoped.row)">打印</el-link>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          layout="prev, pager, next"
          @current-change="handleCurrentPageChange"
          :total="total"
      />
    </div>

    <!-- 查看详情弹窗 -->
    <el-dialog :visible.sync="detailVisible" width="80%" class="detail-dialog">
      <el-descriptions title="病历详情" class="margin-top">
        <el-descriptions-item label="患者姓名">{{ selectedRow?.patientName }}</el-descriptions-item>
        <el-descriptions-item label="主治医生">{{ selectedRow?.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="就诊时间">{{ formatDate(selectedRow?.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag size="small" :type="selectedRow?.recordStatus === 'confirmed' ? 'primary' : selectedRow?.recordStatus === 'voided' ? 'danger' : selectedRow?.status === 'archived' ? 'info' : 'warning'">
            {{ selectedRow?.recordStatus === 'confirmed' ? '已确认' : selectedRow?.recordStatus === 'voided' ? '已作废' : selectedRow?.status === 'archived' ? '已归档' : '草稿' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="主诉">{{ selectedRow?.chiefComplaint || '无' }}</el-descriptions-item>
        <el-descriptions-item label="现病史">{{ selectedRow?.presentIllness || '无' }}</el-descriptions-item>
        <el-descriptions-item label="既往史">{{ selectedRow?.pastHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="体格检查">{{ selectedRow?.physicalExam || '无' }}</el-descriptions-item>
        <el-descriptions-item label="西医诊断">{{ selectedRow?.diagnosis }}</el-descriptions-item>
        <el-descriptions-item label="症状">{{ selectedRow?.symptoms || '无' }}</el-descriptions-item>
        <el-descriptions-item label="处方">{{ selectedRow?.prescription || '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ selectedRow?.remarks || '无' }}</el-descriptions-item>
        <el-descriptions-item label="作废原因" v-if="selectedRow?.recordStatus === 'voided'">{{ selectedRow?.voidReason }}</el-descriptions-item>
        <el-descriptions-item label="补增医嘱" v-if="selectedRow?.supplement">{{ selectedRow?.supplement }}</el-descriptions-item>
        <el-descriptions-item label="图像" v-if="selectedRow?.existChart">
          <el-link @click="viewImage">查看<i class="el-icon-view el-icon--right"></i></el-link>
        </el-descriptions-item>
      </el-descriptions>

      <div v-if="auditLogs.length > 0" style="margin-top:20px">
        <el-divider content-position="left">操作记录</el-divider>
        <el-timeline>
          <el-timeline-item v-for="log in auditLogs" :key="log.logId"
            :timestamp="formatDate(log.createdAt)" placement="top">
            <el-card shadow="hover">
              <p><strong>{{ log.username }}</strong> — {{ log.action }}</p>
              <p v-if="log.detail" style="color:#999;font-size:13px">{{ log.detail }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog :visible.sync="editVisible" width="60%" title="编辑病历（草稿）" class="edit-dialog">
      <el-form ref="form" :model="editForm" :rules="rules" label-width="90px">
        <el-form-item label="主诉">
          <el-input type="textarea" v-model="editForm.chiefComplaint" :rows="2" />
        </el-form-item>
        <el-form-item label="现病史">
          <el-input type="textarea" v-model="editForm.presentIllness" :rows="3" />
        </el-form-item>
        <el-form-item label="既往史">
          <el-input type="textarea" v-model="editForm.pastHistory" :rows="2" />
        </el-form-item>
        <el-form-item label="体格检查">
          <el-input type="textarea" v-model="editForm.physicalExam" :rows="2" />
        </el-form-item>
        <el-form-item label="诊断" prop="diagnosis">
          <el-input type="textarea" v-model="editForm.diagnosis" :rows="2" />
        </el-form-item>
        <el-form-item label="症状">
          <el-input type="textarea" v-model="editForm.symptoms" :rows="2" />
        </el-form-item>
        <el-form-item label="处方">
          <el-input type="textarea" v-model="editForm.prescription" :rows="3" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="editForm.remarks" :rows="2" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">保存修改</el-button>
          <el-button @click="editVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 图片弹窗 -->
    <el-dialog :visible.sync="imageDialogVisible" width="50%" title="查看图像">
      <div v-if="selectedRow?.chart">
        <img
          v-for="(img, index) in selectedRow.chart.split(',')"
          :key="index"
          :src="img"
          style="width: 100%; margin-bottom: 10px;"
          alt="病历图片" />
      </div>
      <span v-else>无图片</span>
    </el-dialog>
  </div>
</template>

<style scoped>
.medical-record-page {
  padding: 20px;
  background-color: #f9f9f9;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 240px;
}

.search-button {
  margin-left: 10px;
}

.medical-record-table {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-link {
  margin: 0 5px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.detail-dialog,
.edit-dialog {
  .el-dialog__body {
    padding: 20px;
  }
}

@media print {
  .search-form, .pagination-container, .medical-record-table,
  .el-aside, .el-header, .el-table-column label:last-child,
  .el-button, .search-input, .search-button { display: none !important; }
  .el-dialog__wrapper { position: static !important; }
  .el-dialog { max-width: 100% !important; margin: 0 !important; box-shadow: none !important; }
}
</style>
