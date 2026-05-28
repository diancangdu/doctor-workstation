<script>
import request from "@/utils/request";

export default {
  name: 'CompareMedicalRecord',
  data() {
    return {
      patientOptions: [],
      patientId: null,
      patientRecords: [],
      selectedAId: null,
      selectedBId: null,
      selectedPatient: null,
    }
  },
  created() {
    this.loadPatients();
  },
  computed: {
    recordA() {
      return this.patientRecords.find(r => r.recordId === this.selectedAId) || null;
    },
    recordB() {
      return this.patientRecords.find(r => r.recordId === this.selectedBId) || null;
    },
    compareRows() {
      if (!this.recordA || !this.recordB) return [];
      const fields = [
        ['就诊时间', 'createdAt', this.formatDate],
        ['主治医生', 'doctorName', v => v],
        ['主诉', 'chiefComplaint', v => v],
        ['现病史', 'presentIllness', v => v],
        ['既往史', 'pastHistory', v => v],
        ['体格检查', 'physicalExam', v => v],
        ['西医诊断', 'diagnosis', v => v],
        ['症状', 'symptoms', v => v],
        ['处方', 'prescription', v => v],
        ['备注', 'remarks', v => v],
      ];
      return fields.map(([field, key, fmt]) => {
        const va = fmt(this.recordA[key]);
        const vb = fmt(this.recordB[key]);
        return { field, valueA: va, valueB: vb, changed: va !== vb };
      });
    }
  },
  methods: {
    loadPatients() {
      request.get('/user/patient').then(res => {
        if (res.code === '200') {
          this.patientOptions = (res.data.list || []).map(p => ({
            value: p.patientId, label: p.user.username, data: p
          }));
        }
      });
    },
    loadPatientRecords() {
      if (!this.patientId) return;
      this.selectedAId = null;
      this.selectedBId = null;
      const p = this.patientOptions.find(o => o.value === this.patientId);
      this.selectedPatient = p ? p.data : null;
      request.get('/medicalRecord/list', {
        params: { patientId: this.patientId, pageSize: 50, pageNum: 1 }
      }).then(res => {
        if (res.code === '200') {
          this.patientRecords = res.data.list || [];
          this.loadDoctorNames();
        }
      });
    },
    loadDoctorNames() {
      request.get('/user/doctor').then(res => {
        if (res.code === '200') {
          const docs = res.data || [];
          this.patientRecords.forEach(r => {
            const d = docs.find(dd => dd.doctorId === r.doctorId);
            if (d) this.$set(r, 'doctorName', d.user.username);
          });
        }
      });
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
    },
    recordLabel(r) {
      return `#${r.recordId} ${this.formatDate(r.createdAt)} ${r.doctorName || ''} ${r.diagnosis || ''}`.substring(0, 50);
    },
    statusLabel(status) {
      const map = { draft: '草稿', confirmed: '已确认', voided: '已作废' };
      return map[status] || status || '草稿';
    },
    statusType(status) {
      const map = { confirmed: 'primary', voided: 'danger', draft: 'warning' };
      return map[status] || 'warning';
    },
  }
}
</script>

<template>
  <div class="compare-page">
    <h2 class="page-title">病历对比</h2>

    <el-card shadow="hover" style="margin-bottom:20px">
      <el-row :gutter="20" align="middle">
        <el-col :span="8">
          <el-select v-model="patientId" filterable placeholder="选择患者" @change="loadPatientRecords" clearable style="width:100%">
            <el-option v-for="p in patientOptions" :key="p.value" :label="p.label" :value="p.value" />
          </el-select>
        </el-col>
        <el-col :span="16" v-if="selectedPatient">
          <el-tag>性别：{{ selectedPatient.gender === 'male' ? '男' : '女' }}</el-tag>
          <el-tag style="margin-left:8px" type="warning">过敏史：{{ selectedPatient.allergies || '无' }}</el-tag>
          <el-tag style="margin-left:8px" type="info">既往病史：{{ selectedPatient.medicalHistory || '无' }}</el-tag>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" v-if="patientRecords.length > 0" type="flex" align="top">
      <el-col :span="12">
        <el-card shadow="hover" style="height:100%">
          <div slot="header"><i class="el-icon-document"></i> 选择记录 A</div>
          <el-radio-group v-model="selectedAId" style="width:100%">
            <el-radio v-for="r in patientRecords" :key="'a'+r.recordId" :label="r.recordId" border style="width:100%; margin-bottom:5px">
              {{ recordLabel(r) }}
              <el-tag size="mini" :type="statusType(r.recordStatus)">{{ statusLabel(r.recordStatus) }}</el-tag>
            </el-radio>
          </el-radio-group>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" style="height:100%">
          <div slot="header"><i class="el-icon-document-copy"></i> 选择记录 B</div>
          <el-radio-group v-model="selectedBId" style="width:100%">
            <el-radio v-for="r in patientRecords" :key="'b'+r.recordId" :label="r.recordId" border style="width:100%; margin-bottom:5px">
              {{ recordLabel(r) }}
              <el-tag size="mini" :type="statusType(r.recordStatus)">{{ statusLabel(r.recordStatus) }}</el-tag>
            </el-radio>
          </el-radio-group>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" v-if="recordA && recordB" style="margin-top:20px">
      <div slot="header"><i class="el-icon-sort"></i> 对比结果</div>
      <el-table :data="compareRows" border stripe :show-header="false">
        <el-table-column prop="field" width="120" />
        <el-table-column label="记录 A">
          <template v-slot="s">{{ s.row.valueA || '无' }}</template>
        </el-table-column>
        <el-table-column label="记录 B">
          <template v-slot="s">{{ s.row.valueB || '无' }}</template>
        </el-table-column>
        <el-table-column label="变化" width="80" align="center">
          <template v-slot="s">
            <el-tag size="mini" :type="s.row.changed ? 'warning' : 'info'">{{ s.row.changed ? '有变化' : '相同' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty v-if="patientId && patientRecords.length === 0" description="该患者暂无就诊记录" />
  </div>
</template>

<style scoped>
.compare-page { padding: 20px; }
.page-title { font-size: 24px; color: #333; margin-bottom: 20px; }

/* 修复 radio border 第一个不对齐的问题 */
.el-radio.is-bordered + .el-radio.is-bordered {
  margin-top: 5px;
}
.el-radio.is-bordered {
  margin-left: 0 !important;
  display: flex;
  align-items: center;
  min-height: 40px;
}
</style>
