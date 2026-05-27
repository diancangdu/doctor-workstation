<script>
import request from "@/utils/request";
import Cookies from "js-cookie";

export default {
  name: "MyMedicalRecord",
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {},
      params: {
        symptoms: '',
        diagnosis: '',
        pageSize: 13,
        pageNum: 1
      },
      medicalRecordData: [],
      doctorData: [],
      prescriptionData: [],
      total: 0,
      detailVisible: false,
      imageDialogVisible: false,
      selectedRow: null,
      selectedPrescription: [],
    }
  },
  created() {
    this.listPrescription()
    this.listMedicalRecord()
  },
  methods: {
    listMedicalRecord() {
      request({
        url: '/medicalRecord/list',
        method: 'get',
        params: {
          ...this.params,
          userId: this.user.userId
        }
      }).then(res => {
        if (res.code === '200') {
          this.medicalRecordData = res.data.list
          this.total = res.data.total
          this.loadDoctors()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadDoctors() {
      request.get('/user/doctor').then(res => {
        if (res.code === '200') {
          this.doctorData = res.data
          for (let i = 0; i < this.medicalRecordData.length; i++) {
            for (let j = 0; j < this.doctorData.length; j++) {
              if (this.medicalRecordData[i].doctorId === this.doctorData[j].doctorId) {
                this.$set(this.medicalRecordData[i], 'doctorName', this.doctorData[j].user.username)
              }
            }
          }
        }
      })
    },
    listPrescription() {
      request({
        url: '/prescription/list',
        method: 'get',
        params: {}
      }).then(res => {
        if (res.code === '200') {
          this.prescriptionData = res.data.list
        }
      })
    },
    viewDetail(row) {
      this.selectedPrescription = []
      this.selectedRow = row
      if (this.selectedRow.chart === '' || this.selectedRow.chart === null) {
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
      if (this.selectedPrescription.length === 0) {
        this.$set(this.selectedRow, 'existPrescription', false)
      } else {
        this.$set(this.selectedRow, 'existPrescription', true)
      }
      this.detailVisible = true
    },
    viewImage() {
      this.imageDialogVisible = true
    },
    reset() {
      this.params = {
        symptoms: '',
        diagnosis: '',
        pageSize: 13,
        pageNum: 1
      }
      this.listMedicalRecord()
    },
    handleCurrentPageChange(pageNum) {
      this.params.pageNum = pageNum
      this.listMedicalRecord()
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    }
  }
}
</script>

<template>
  <div class="my-record-page">
    <h2 class="page-title">我的病历</h2>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-input class="search-input" placeholder="搜索症状" v-model="params.symptoms" />
      <el-input class="search-input" placeholder="搜索诊断" v-model="params.diagnosis" />
      <el-button class="search-button" type="primary" @click="listMedicalRecord">
        <i class="el-icon-search"></i>搜索
      </el-button>
      <el-button class="search-button" type="warning" @click="reset">
        <i class="el-icon-refresh"></i>重置
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="medicalRecordData" stripe border class="record-table">
      <el-table-column prop="recordId" label="病历编号" width="90" />
      <el-table-column prop="doctorName" label="主治医生" />
      <el-table-column prop="diagnosis" label="诊断" />
      <el-table-column prop="symptoms" label="症状" />
      <el-table-column label="就诊日期" width="120">
        <template v-slot="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template v-slot="scope">
          <el-link type="primary" @click="viewDetail(scope.row)">查看详情</el-link>
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
        <el-descriptions-item label="主治医生">{{ selectedRow?.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="诊断信息">{{ selectedRow?.diagnosis }}</el-descriptions-item>
        <el-descriptions-item label="症状">{{ selectedRow?.symptoms }}</el-descriptions-item>
        <el-descriptions-item label="处方">{{ selectedRow?.prescription }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ selectedRow?.remarks }}</el-descriptions-item>
        <el-descriptions-item label="就诊日期">
          {{ formatDate(selectedRow?.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="图像" v-if="selectedRow?.existChart">
          <el-link @click="viewImage">查看<i class="el-icon-view el-icon--right"></i></el-link>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 关联处方详情 -->
      <div v-if="selectedRow?.existPrescription" class="prescription-detail">
        <el-divider content-position="left">关联处方</el-divider>
        <el-descriptions>
          <el-descriptions-item label="药品名称">{{ selectedPrescription[0]?.medicationName }}</el-descriptions-item>
          <el-descriptions-item label="剂量">{{ selectedPrescription[0]?.dosage }}</el-descriptions-item>
          <el-descriptions-item label="频次">{{ selectedPrescription[0]?.frequency }}</el-descriptions-item>
          <el-descriptions-item label="疗程">{{ selectedPrescription[0]?.duration }}</el-descriptions-item>
          <el-descriptions-item label="用法说明">{{ selectedPrescription[0]?.instructions }}</el-descriptions-item>
          <el-descriptions-item label="处方备注">{{ selectedPrescription[0]?.remarks }}</el-descriptions-item>
        </el-descriptions>
      </div>
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
.my-record-page {
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

.record-table {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.prescription-detail {
  margin-top: 20px;
}
</style>
