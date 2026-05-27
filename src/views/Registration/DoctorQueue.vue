<script>
import request from "@/utils/request";
import Cookies from "js-cookie";

export default {
  name: 'DoctorQueue',
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {},
      doctorId: null,
      queueData: [],
      inProgressData: [],
      loading: false,
    }
  },
  created() {
    this.loadDoctorId();
  },
  methods: {
    loadDoctorId() {
      request.get('/user/doctor').then(res => {
        if (res.code === '200') {
          const doctors = res.data;
          const self = doctors.find(d => d.user && d.user.userId === this.user.userId);
          if (self) {
            this.doctorId = self.doctorId;
            this.loadQueue();
          }
        }
      })
    },
    loadQueue() {
      this.loading = true;
      // 待诊队列
      request.get('/registration/queue', { params: { doctorId: this.doctorId } }).then(res => {
        if (res.code === '200') this.queueData = res.data;
      });
      // 接诊中
      request.get('/registration/list', { params: { doctorId: this.doctorId, status: 'in_progress' } }).then(res => {
        if (res.code === '200') this.inProgressData = res.data;
      }).finally(() => this.loading = false);
    },
    startConsultation(row) {
      this.$confirm('确认接诊该患者?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(() => {
        request.put('/registration/start/' + row.registrationId).then(res => {
          if (res.code === '200') {
            this.$message.success('接诊成功');
            this.loadQueue();
          } else {
            this.$message.error(res.msg);
          }
        })
      })
    },
    writeRecord(row) {
      this.$router.push({ path: '/AddMedicalRecord', query: { patientId: row.patientUserId, registrationId: row.registrationId } });
    },
    completeConsultation(row) {
      this.$confirm('确认完成该患者的就诊?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(() => {
        request.put('/registration/complete/' + row.registrationId).then(res => {
          if (res.code === '200') {
            this.$message.success('就诊完成');
            this.loadQueue();
          } else {
            this.$message.error(res.msg);
          }
        })
      }).catch(() => {})
    },
    formatTime(t) {
      if (!t) return '';
      const d = new Date(t);
      return d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0');
    },
  }
}
</script>

<template>
  <div class="queue-page">
    <h2 class="page-title">接诊排队</h2>

    <el-row :gutter="20">
      <!-- 等待队列 -->
      <el-col :span="14">
        <el-card shadow="hover">
          <div slot="header">
            <span><i class="el-icon-s-order"></i> 待诊患者 ({{ queueData.length }})</span>
            <el-button style="float:right" size="small" type="primary" @click="loadQueue">刷新</el-button>
          </div>
          <el-table :data="queueData" stripe v-loading="loading" empty-text="暂无待诊患者">
            <el-table-column prop="queueNumber" label="序号" width="50" />
            <el-table-column prop="patientName" label="患者" />
            <el-table-column prop="chiefComplaint" label="主诉" />
            <el-table-column label="挂号时间" width="100">
              <template v-slot="s">{{ formatTime(s.row.registrationTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template v-slot="s">
                <el-button type="primary" size="mini" @click="startConsultation(s.row)">接诊</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 接诊中 -->
      <el-col :span="10">
        <el-card shadow="hover">
          <div slot="header">
            <span><i class="el-icon-loading"></i> 当前接诊</span>
          </div>
          <div v-if="inProgressData.length === 0" style="color: #999; text-align: center; padding: 40px 0;">
            暂无进行中的接诊
          </div>
          <div v-for="item in inProgressData" :key="item.registrationId" style="padding: 10px;">
            <p><strong>{{ item.patientName }}</strong></p>
            <p>主诉：{{ item.chiefComplaint || '无' }}</p>
            <p>接诊时间：{{ formatTime(item.consultationTime) }}</p>
            <div style="margin-top: 10px;">
              <el-button type="success" size="small" @click="writeRecord(item)">书写病历</el-button>
              <el-button type="warning" size="small" @click="completeConsultation(item)">完成就诊</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.queue-page { padding: 20px; }
.page-title { font-size: 24px; color: #333; margin-bottom: 20px; }
</style>
