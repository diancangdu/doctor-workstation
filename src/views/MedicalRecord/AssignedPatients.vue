<script>
import Cookies from "js-cookie";
import request from "@/utils/request";

export default {
  name: 'AssignedPatients',
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {},
      tableData: [],// 所有用户信息
      patientData: [],//所有患者信息
      relationData: [],//患者关系
      assignedPatientsData: [],// 名下患者
      assignedPatientsShow: [],// 名下患者展示
      selectedUser: [],//选中的用户
      detailDialogVisible: false,//详情对话框
      total: 0,
      selectedPrescription: null,
      selectedRow: null,
      prescriptionData: [],
      detailVisible: false,
      imageDialogVisible: false,
      medicalRecordData: null,
      params: {
        username: '',
        phoneNumber: '',
        role: '',
        email: '',
        pageSize: 13,
        pageNum: 1
      },
    }
  },
  created() {
    this.load()
    this.loadPatients()
    this.listMedicalRecord()
    this.listPrescription()
  },
  methods: {
    load() {
      request.get('/user/page', {params: this.params}).then(res => {

        if (res.code === '200') {
          this.tableData = res.data.list
          this.total = res.data.total
          // console.log(JSON.stringify(this.tableData))
          // this.loadRelation()
        }
      })
    },
    // 获取患者信息
    loadPatients() {
      request.get('/user/patient', {params: this.params}).then(res => {
        if (res.code === '200') {
          this.patientData = res.data.list
          this.loadRelation()
          // console.log(JSON.stringify(this.patientData))
        }
      })
    },
    // 获取医患关系信息
    loadRelation() {
      this.assignedPatientsData = []
      // console.log(this.user.userId)
      let userId = Number(this.user.userId)
      // console.log(userId)
      request({
        url: '/DoctorPatient/getByUserId',
        method: 'get',
        params: { userId }
      }).then(res => {
        // console.log("res:" + JSON.stringify(res, null, 2))
        if (res.code === '200') {
          this.relationData = res.data
          // console.log(this.relationData)
        } else {
          this.$message.error(res.msg)
        }
      }).then(() => {
        for (let i = 0; i < this.relationData.length; i++) {
          for (let j = 0; j < this.patientData.length; j++) {
            if (this.relationData[i].patientId === this.patientData[j].patientId) {
              // 去重：同一 patientId 只 push 一次，防止重复调用 loadRelation() 导致数据重复
              if (!this.assignedPatientsData.some(p => p.patientId === this.patientData[j].patientId)) {
                this.assignedPatientsData.push(this.patientData[j])
              }
            }
          }
        }
        this.handleShow()
        // console.log(this.assignedPatientsData)
      })
    },

    listMedicalRecord() {
      // console.log(this.params)
      request({
        url: '/medicalRecord/list',
        method: 'get',
        params: this.params
      }).then(res => {
        if (res.code === '200') {
          this.medicalRecordData = res.data.list
          // console.log(this.medicalRecordData)
        } else {
          this.$message.error(res.msg)
        }
      })
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
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    // 查看病历
    viewMedicalRecord(row) {
      this.selectedPrescription = []
      this.selectedRow = row
      // console.log(this.selectedRow)
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
      // console.log(this.selectedPrescription)
      if (this.selectedPrescription.length === 0) {
        this.$set(this.selectedRow, 'existPrescription', false)
      } else {
        this.$set(this.selectedRow, 'existPrescription', true)
      }
      this.detailVisible = true
    },
    // 删除角色请求
    deleteUserRequest(rowUser) {
      this.$confirm('此操作将从名下解除绑定该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let doctorUserId = this.user.userId
        let patientUserId = rowUser.userId
        request({
          url: `/DoctorPatient/delete/${doctorUserId}/${patientUserId}`,
          method: 'delete',
        }).then(res => {
          if (res.code === '200') {
            this.$message.success("删除成功")
          } else {
            this.$message.error(res.msg)
          }
          this.loadRelation();
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    // 处理展示
    handleShow() {
      this.assignedPatientsShow = JSON.parse(JSON.stringify(this.assignedPatientsData))
      // 处理性别展示，female->女，male->男
      for (let i = 0; i < this.assignedPatientsShow.length; i++) {
        if (this.assignedPatientsShow[i].gender === 'female') {
          this.assignedPatientsShow[i].gender = '女'
        } else if (this.assignedPatientsShow[i].gender === 'male') {
          this.assignedPatientsShow[i].gender = '男'
        }
      }
      // 处理年龄展示，根据dateOfBirth计算年龄
      const currentDate = new Date(); // 当前日期
      this.assignedPatientsShow = this.assignedPatientsShow.map(patient => {
        if (patient.dateOfBirth) {
          const birthDate = new Date(patient.dateOfBirth); // 解析出生日期
          const age = currentDate.getFullYear() - birthDate.getFullYear();
          const isBirthdayPassed =
              currentDate.getMonth() > birthDate.getMonth() ||
              (currentDate.getMonth() === birthDate.getMonth() &&
                  currentDate.getDate() >= birthDate.getDate());
          // 如果生日还没过，则年龄减一
          patient.age = isBirthdayPassed ? age : age - 1;
        } else {
          patient.age = null; // 如果没有 dateOfBirth，则设置为 null 或其他默认值
        }
        return patient;
      });
    },
    reset() {
      this.params = {
        username: '',
        phoneNumber: '',
        pageSize: 13,
        pageNum: 1
      }
      this.load()
      this.loadPatients()
    },
    // 处理分页
    handleCurrentPageChange(pageNum) {
      this.params.pageNum = pageNum
      this.loadPatients()
    },

    // 查看图片
    viewImage() {
      this.imageDialogVisible = true; // 打开
    },
  }
}
</script>

<template>
<div class="container">
  <h2 class="tittle">名下患者</h2>
  <!-- 表格 -->
  <!-- 搜索表单 -->
  <div style="margin-bottom: 20px">
    <el-input style="width: 240px;" placeholder="输入名称" v-model="params.username" ></el-input>
    <el-input style="width: 240px; margin-left: 5px;" placeholder="请输入电话" v-model="params.phoneNumber"></el-input>
    <el-input style="width: 240px; margin-left: 5px;" placeholder="请输入邮件" v-model="params.email"></el-input>
    <el-button style="margin-left: 5px;" type="primary" @click="loadPatients"><i class="el-icon-search"></i>搜索</el-button>
    <el-button style="margin-left: 5px;" type="warning" @click="reset"><i class="el-icon-refresh"></i>重置</el-button>
  </div>

  <!--表格-->
  <el-table :data="assignedPatientsShow" stripe border>
    <el-table-column
        type="index"
        width="50">
    </el-table-column>
    <el-table-column prop='userId' label="id" width="70"></el-table-column>
    <el-table-column prop='user.username' label="患者姓名" width="80"></el-table-column>
    <el-table-column prop='gender' label="性别" width="50"></el-table-column>
    <el-table-column prop='age' label="年龄" width="50"></el-table-column>
    <el-table-column prop='user.phoneNumber' label="电话" ></el-table-column>
    <el-table-column prop='medicalHistory' label="用药历史" ></el-table-column>
    <el-table-column prop='allergies' label="过敏反应" ></el-table-column>
    <el-table-column prop='emergencyContact' label="紧急联系人" ></el-table-column>
    <el-table-column prop='contactPhone' label="紧急联系电话" ></el-table-column>
    <el-table-column label="操作">
      <template v-slot="scoped">
<!--        <el-link type="primary" style="margin: 2px" @click="viewMedicalRecord(scoped.row)">查看病历</el-link>-->
        <el-link type="danger" style="margin: 2px" @click="deleteUserRequest(scoped.row)">从名下删除</el-link>
      </template>
    </el-table-column>
  </el-table>

  <!--分页条-->
  <!-- 分页 -->
  <div style="margin-top: 20px">
    <el-pagination
        background
        :current-page="params.pageNum"
        :page-size="params.pageSize"
        layout="prev, pager, next"
        @current-change="handleCurrentPageChange"
        :total="total">
    </el-pagination>
  </div>
  <!-- 查看详情弹窗 -->
  <el-dialog :visible.sync="detailVisible" width="80%" class="detail-dialog">
    <el-descriptions title="病历详情" class="margin-top">
      <el-descriptions-item label="患者姓名">{{ selectedRow?.patientName }}</el-descriptions-item>
      <el-descriptions-item label="主治医生">{{ selectedRow?.doctorName }}</el-descriptions-item>
      <el-descriptions-item label="备注">{{ selectedRow?.remarks }}</el-descriptions-item>
      <el-descriptions-item label="诊断信息">{{ selectedRow?.diagnosis }}</el-descriptions-item>
      <el-descriptions-item label="症状">{{ selectedRow?.symptoms }}</el-descriptions-item>
      <el-descriptions-item label="处方">{{ selectedRow?.prescription }}</el-descriptions-item>
      <el-descriptions-item label="图像" v-if="selectedRow?.existChart">
        <el-link @click="viewImage">查看<i class="el-icon-view el-icon--right"></i></el-link>
      </el-descriptions-item>
    </el-descriptions>
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

</style>
