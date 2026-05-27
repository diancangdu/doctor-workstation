<script>
import request from "@/utils/request";

export default {
  name: "BindPatientAndDoctor",
  data() {
    return {
      doctorOptions: [],
      patientOptions: [],
      doctorValue: null,
      patientValues: [],
      relations: [],
      loading: false,
    }
  },
  created() {
    this.loadOptions();
    this.loadRelations();
  },
  methods: {
    loadOptions() {
      request.get('/user/doctor').then(res => {
        if (res.code === '200') {
          this.doctorOptions = (res.data || []).map(d => ({
            value: d.userId, label: d.user.username + ' - ' + (d.department || '')
          }));
        }
      });
      request.get('/user/patient').then(res => {
        if (res.code === '200') {
          this.patientOptions = (res.data.list || []).map(p => ({
            value: p.userId, label: p.user.username
          }));
        }
      });
    },
    loadRelations() {
      this.loading = true;
      let doctorList = [], patientList = [];
      Promise.all([
        request.get('/user/doctor'),
        request.get('/user/patient', { params: { pageSize: 999 } })
      ]).then(([dRes, pRes]) => {
        doctorList = dRes.code === '200' ? (dRes.data || []) : [];
        patientList = pRes.code === '200' ? (pRes.data.list || []) : [];
        const promises = doctorList.map(d =>
          request.get('/DoctorPatient/getByUserId', { params: { userId: d.userId } })
        );
        return Promise.all(promises).then(results => {
          this.relations = [];
          results.forEach((r, i) => {
            if (r.code === '200' && r.data) {
              (r.data || []).forEach(dp => {
                const patient = patientList.find(p => p.patientId === dp.patientId);
                this.relations.push({
                  ...dp,
                  doctorUserId: doctorList[i].userId,
                  patientUserId: patient ? patient.userId : null,
                  doctorName: doctorList[i].user ? doctorList[i].user.username : '',
                  patientName: patient ? patient.user.username : '',
                  doctorDepartment: doctorList[i].department || '',
                });
              });
            }
          });
        });
      }).finally(() => this.loading = false);
    },
    bindRelation() {
      if (!this.doctorValue) { this.$message.warning('请选择医生'); return; }
      if (!this.patientValues || this.patientValues.length === 0) { this.$message.warning('请选择至少一位患者'); return; }
      request({
        url: '/DoctorPatient/add',
        method: 'put',
        data: {
          doctorValue: this.doctorValue,
          patientValues: this.patientValues
        }
      }).then(res => {
        if (res.code === '200') {
          this.$message.success('绑定成功');
          this.patientValues = [];
          this.loadRelations();
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    unbind(row) {
      this.$confirm('确定解除该医患关系?', '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        request.delete('/DoctorPatient/delete/' + row.doctorUserId + '/' + row.patientUserId).then(res => {
          if (res.code === '200') {
            this.$message.success('已解除');
            this.loadRelations();
          } else {
            this.$message.error(res.msg);
          }
        })
      }).catch(() => {});
    },
  }
}
</script>

<template>
  <div class="bind-page">
    <h2 class="page-title">医患关系管理</h2>

    <!-- 绑定表单 -->
    <el-card shadow="hover" style="margin-bottom:20px">
      <div slot="header"><i class="el-icon-link"></i> 新建绑定</div>
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-select v-model="doctorValue" filterable placeholder="选择医生" style="width:100%">
            <el-option v-for="d in doctorOptions" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-col>
        <el-col :span="12">
          <el-select v-model="patientValues" multiple filterable placeholder="选择患者（可多选）" style="width:100%">
            <el-option v-for="p in patientOptions" :key="p.value" :label="p.label" :value="p.value" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" icon="el-icon-link" @click="bindRelation">绑定</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 已有关系 -->
    <el-card shadow="hover" v-loading="loading">
      <div slot="header"><i class="el-icon-connection"></i> 已有关系 ({{ relations.length }})</div>
      <el-table :data="relations" stripe border empty-text="暂无医患关系">
        <el-table-column prop="doctorName" label="医生" />
        <el-table-column prop="doctorDepartment" label="科室" />
        <el-table-column prop="patientName" label="患者" />
        <el-table-column label="操作" width="100">
          <template v-slot="s">
            <el-button type="danger" size="mini" icon="el-icon-delete" @click="unbind(s.row)">解除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.bind-page { padding: 20px; }
.page-title { font-size: 24px; color: #333; margin-bottom: 20px; }
</style>
