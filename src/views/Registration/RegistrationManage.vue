<script>
import request from "@/utils/request";
import Cookies from "js-cookie";

export default {
  name: 'RegistrationManage',
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {},
      form: { patientUserId: null, doctorId: null, department: '', chiefComplaint: '' },
      registrations: [],
      patientOptions: [],
      doctorOptions: [],
      rules: {
        patientUserId: [{ required: true, message: '请选择患者', trigger: 'change' }],
        doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
      },
      dialogVisible: false,
    }
  },
  created() {
    this.loadOptions();
    this.loadRegistrations();
  },
  methods: {
    loadOptions() {
      request.get('/user/patient').then(res => {
        if (res.code === '200') {
          this.patientOptions = (res.data.list || []).map(p => ({
            value: p.userId, label: p.user.username + ' (ID:' + p.userId + ')'
          }));
        }
      });
      request.get('/user/doctor').then(res => {
        if (res.code === '200') {
          this.doctorOptions = (res.data || []).map(d => ({
            value: d.doctorId, label: d.user.username + ' - ' + (d.department || '')
          }));
        }
      });
    },
    loadRegistrations() {
      request.get('/registration/list', { params: { status: '' } }).then(res => {
        if (res.code === '200') this.registrations = res.data;
      });
    },
    onSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return;
        // 从 doctorOptions 中找到选中的医生获取其 department
        const doctor = this.doctorOptions.find(d => d.value === this.form.doctorId);
        this.form.department = doctor ? doctor.label.split(' - ')[1] || '' : '';
        // 找到用户的 userId
        const patient = this.patientOptions.find(p => p.value === this.form.patientUserId);
        if (patient) this.form.patientUserId = patient.value;

        request.post('/registration/add', this.form).then(res => {
          if (res.code === '200') {
            this.$message.success('挂号成功');
            this.dialogVisible = false;
            this.form = { patientUserId: null, doctorId: null, department: '', chiefComplaint: '' };
            this.loadRegistrations();
          } else {
            this.$message.error(res.msg);
          }
        });
      });
    },
    cancelReg(row) {
      this.$confirm('确定取消该挂号?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        .then(() => {
          request.put('/registration/cancel/' + row.registrationId).then(res => {
            if (res.code === '200') {
              this.$message.success('已取消');
              this.loadRegistrations();
            } else {
              this.$message.error(res.msg);
            }
          })
        });
    },
    statusLabel(status) {
      const map = { waiting: '待诊', in_progress: '接诊中', completed: '已完成', cancelled: '已取消' };
      return map[status] || status;
    },
    statusType(status) {
      const map = { waiting: 'warning', in_progress: 'primary', completed: 'success', cancelled: 'info' };
      return map[status] || 'info';
    },
  }
}
</script>

<template>
  <div class="reg-page">
    <h2 class="page-title">挂号管理</h2>

    <el-button type="primary" icon="el-icon-plus" @click="dialogVisible = true" style="margin-bottom: 20px;">新建挂号</el-button>
    <el-button type="success" icon="el-icon-s-data" style="margin-bottom: 20px; margin-left: 10px;">挂号实时录入</el-button>

    <el-table :data="registrations" stripe border>
      <el-table-column prop="registrationId" label="编号" width="70" />
      <el-table-column prop="patientName" label="患者" />
      <el-table-column prop="doctorName" label="医生" />
      <el-table-column prop="department" label="科室" />
      <el-table-column prop="chiefComplaint" label="主诉" />
      <el-table-column prop="queueNumber" label="序号" width="50" />
      <el-table-column label="状态" width="90">
        <template v-slot="s">
          <el-tag :type="statusType(s.row.status)" size="small">{{ statusLabel(s.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template v-slot="s">
          <el-button v-if="s.row.status === 'waiting'" type="danger" size="mini" @click="cancelReg(s.row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :visible.sync="dialogVisible" title="新建挂号" width="40%">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="患者" prop="patientUserId">
          <el-select v-model="form.patientUserId" filterable placeholder="选择患者">
            <el-option v-for="p in patientOptions" :key="p.value" :label="p.label" :value="p.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生" prop="doctorId">
          <el-select v-model="form.doctorId" placeholder="选择医生">
            <el-option v-for="d in doctorOptions" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="主诉">
          <el-input v-model="form.chiefComplaint" placeholder="挂号原因/主诉" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">确认挂号</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<style scoped>
.reg-page { padding: 20px; }
.page-title { font-size: 24px; color: #333; margin-bottom: 20px; }
</style>
