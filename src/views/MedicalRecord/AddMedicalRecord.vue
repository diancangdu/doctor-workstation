<script>
import request from "@/utils/request";
import Cookies from "js-cookie";

export default {
  name: 'AddMedicalRecord',
  data() {
    return {
      user: Cookies.get("user") ? JSON.parse(Cookies.get("user")) : {},
      patientOptions: [{
        value: '病人 Id: xx',
        label: 'Id'
      }],
      patientValue: '',
      selectedPatient: [],
      total: 0,
      params: {
        doctorId: '',
        medicationName: '',
        remarks: '',
        pageSize: 13,
        pageNum: 1
      },
      patientData: [],
      patientTableData: [],
      doctorData: [],
      isDoctor: false,
      form: {
        chart: '',
        chiefComplaint: '',
        presentIllness: '',
        pastHistory: '',
        physicalExam: '',
        diagnosis: '',
        symptoms: '',
        prescription: '',
        remarks: '',
      },
      chartList: [],
      templates: [],
      selectedTemplate: null,
      editMode: false,
      editRecordId: null,
      doctorInfo: {},
      drugAlerts: [],
      isSelectPatient: false,
      prescriptionOptions: [],
      selectFromTable: false, // 是否从表格中选择
      prescriptionId: null,
      rules: {
        diagnosis: [{ required: true, message: '请输入诊断', trigger: 'blur' }],
      }
    }
  },
  created() {
    if(this.user.role === 'doctor') {
      this.isDoctor = true;
      this.load();
      this.loadDoctors();
      this.loadDoctorInfo();
      this.loadTemplates();
      this.listPrescription();
      // 编辑模式
      const editId = this.$route.query.edit;
      if (editId) {
        this.editMode = true;
        this.editRecordId = Number(editId);
      }
      const preSelectId = this.$route.query.patientId;
      this.loadPatientsTable(() => {
        if (editId) this.loadExistingRecord();
        if (preSelectId) {
          this.patientValue = preSelectId;
          this.selectPatient();
        }
      });
    } else {
      this.$message.error("您非医生，无法添加病历")
    }
  },
  methods: {
    // 获取医生信息
    loadDoctors() {
      request.get('/user/doctor').then(res => {
        // console.log("res:" + JSON.stringify(res, null, 2))
        if (res.code === '200') {
          this.doctorData = res.data
          // console.log(this.doctorData)
        }
      })
    },
    // 读取患者信息
    loadPatients() {
      this.patientData = this.userData.filter(user => user.role === 'patient');
    },
    load() {
      request.get('/user/page', {params: this.params}).then(res => {
        // console.log("res:" + JSON.stringify(res, null, 2))
        if (res.code === '200') {
          this.userData = res.data.list
          this.total = res.data.total
          // console.log(JSON.stringify(this.userData))
          this.loadPatients()
          this.generateOptions(this.patientData, 'patient')
        }
      })
    },
    // 获取患者信息
    loadPatientsTable(callback) {
      request.get('/user/patient', {params: this.params}).then(res => {
        if (res.code === '200') {
          this.patientTableData = res.data.list
          if (callback) callback();
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    // 处理选项
    generateOptions(data, type) {
      if (type === 'doctor') {
        this.doctorOptions = data.map(item => {
          return {
            value: item.userId.toString(),
            label: `${item.username} Id: ${item.userId}` // 拼接username和userId
          }
        })
      } else if (type === 'patient') {
        this.patientOptions = data.map(item => {
          return {
            value: item.userId.toString(),
            label: `${item.username} Id: ${item.userId}` // 拼接username和userId
          }
        })
      } else if (type === 'prescription') {
        this.prescriptionOptions = data.map(item => {
          return {
            value: item.prescriptionId.toString(),
            label: `药品名称：${item.medicationName} 开具医生： ${item.doctorName} 备注：${item.remarks}` // 拼接username和userId
          }
        })
        console.log(this.prescriptionOptions)
      }
    },
    confirmAdd() {
      // 将 patientOptions 的 value 转换成数字型数组
      console.log(this.patientValue)
      const patientValues = this.patientValue.map(item => parseInt(item, 10));
      console.log(patientValues)
      // 发送医生的userId和病人的id数组给后端
      request({
        url: '/DoctorPatient/add',
        method: 'put',
        data: {
          doctorValue: this.doctorValue,
          patientValues: patientValues
        }
      }).then(res => {

        if (res.code === '200') {
          this.$message.success('绑定成功')
        } else {
          this.$message.error(res.msg)
        }
        this.load()
      })
    },
    selectPatient() {
      this.selectedPatient = [];
      let num = Number(this.patientValue)
      try {
        for(let i = 0; i < this.patientTableData.length; i++) {
          if(this.patientTableData[i].userId === num) {
            this.selectedPatient.push(this.patientTableData[i])
          }
        }
        // console.log(this.selectedPatient)
        this.selectedPatient.forEach(item => {
          // 处理患者性别，female->女，male->男
          if(item.gender === 'female') {
            item.gender = '女'
          } else if(item.gender === 'male') {
            item.gender = '男'
          }
          // 处理患者年龄，根据dateOfBirth计算年龄
          let dateOfBirth = new Date(item.dateOfBirth)
          let now = new Date()
          item.age = now.getFullYear() - dateOfBirth.getFullYear()
          this.isSelectPatient = true;
        })
      } catch (e) {
        this.$message.error(e)
      }
    },
    // 上传前检验文件类型和大小
    beforeUpload(file){
      const isImage = file.type.startsWith('image/');
      const isLt8M = file.size / 1024 / 1024 < 8;
      if (!isImage) {
        this.$message.error('只能上传图片文件!');
        return false;
      }
      if (!isLt8M) {
        this.$message.error('上传文件大小不能超过 8MB!');
        return false;
      }
      return true;
    },
    // 清除上传文件
    handleFileRemove(file) {
      this.chartList = this.chartList.filter(item => item.uid !== file.uid);
    },
    // 上传成功
    handleSuccess(response, file) {
      this.chartList.push({ name: file.name, url: response.data.filePath, uid: file.uid });
      this.$message.success('上传成功');
    },
    // 上传失败
    handleError(err) {
      this.$message.error('上传失败');
    },
    listPrescription() {
      // console.log(this.doctorId)
      if(this.doctorId !== '' || this.doctorId !== null) {
        this.params.doctorId = this.doctorId
      }
      // console.log(this.params)
      request({
        url: '/prescription/list',
        method: 'get',
        params: this.params
      }).then(res => {
        if(res.code === '200') {
          this.prescriptionTable = res.data.list
          this.setDoctorName()
          this.generateOptions(this.prescriptionTable, 'prescription')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    setDoctorName() {
      // console.log(this.doctorData)
      try {
        for (let i=0; i < this.doctorData.length; i++) {
          for (let j=0; j < this.prescriptionTable.length; j++) {
            if (this.doctorData[i].doctorId === this.prescriptionTable[j].doctorId) {
              this.prescriptionTable[j].doctorName = this.doctorData[i].user.username
            }
          }
        }
        // console.log(this.prescriptionTable)
      } catch (e) {
        this.$message.error(e)
      }
    },
    // 提交表单
    onSubmit() {
      let patientUserId = Number(this.patientValue)
      if (this.prescriptionId != null && this.prescriptionId !== '') {
        this.form.prescriptionId = Number(this.prescriptionId)
      }

      try {
        for (let i = 0; i < this.doctorData.length; i++) {
          if (this.user.userId === this.doctorData[i].user.userId) {
            this.form.doctorId = this.doctorData[i].doctorId
          }
        }
        for (let i = 0; i < this.patientTableData.length; i++) {
          if (patientUserId === this.patientTableData[i].user.userId) {
            this.form.patientId = this.patientTableData[i].patientId;
          }
        }
      } catch (e) {
        this.$message.error(e)
      }

      // 多图路径拼接为逗号分隔字符串
      this.form.chart = this.chartList.map(item => item.url).join(',');

      const url = this.editMode ? '/medicalRecord/update' : '/medicalRecord/add';
      const method = this.editMode ? 'post' : 'put';
      if (this.editMode) this.form.recordId = this.editRecordId;

      request({ url, method, data: this.form }).then(res => {
        if(res.code === '200') {
          this.$message.success(this.editMode ? '修改成功' : '添加成功');
          this.$router.back();
        } else {
          this.$message.error(res.msg);
        }
      })
      console.log(this.form)
    },
    handleChange() {
      if (this.selectFromTable === false) {
        this.prescriptionId = null;
      }
    },
    loadTemplates() {
      request.get('/common/templates').then(res => {
        if (res.code === '200') this.templates = res.data;
      });
    },
    loadDoctorInfo() {
      request.get('/user/doctor').then(res => {
        if (res.code === '200') {
          const self = (res.data || []).find(d => d.user && d.user.userId === this.user.userId);
          if (self) this.doctorInfo = self;
        }
      });
    },
    applyTemplate(templateId) {
      const t = this.templates.find(tp => tp.templateId === templateId);
      if (!t) return;
      this.form.diagnosis = t.diagnosisName || '';
      this.form.symptoms = t.symptoms || '';
      this.form.prescription = t.prescriptionHint || '';
    },
    checkDrugInteraction() {
      this.drugAlerts = [];
      if (!this.form.prescription) return;
      const drugs = this.form.prescription.split('\n').filter(Boolean);
      drugs.forEach(line => {
        const name = line.split(/[\s\d]/)[0];
        if (name && name.length > 1) {
          request.get('/common/drug', { params: { name } }).then(res => {
            if (res.code === '200' && res.data) {
              const d = res.data;
              this.drugAlerts.push(d.drugName + ' | ' + (d.dosageHint || '') + ' | ' + (d.contraindications || ''));
            }
          });
        }
      });
    },
    loadExistingRecord() {
      request.get('/medicalRecord/getById/' + this.editRecordId).then(res => {
        if (res.code === '200' && res.data) {
          const record = res.data;
          this.form = {
            diagnosis: record.diagnosis || '',
            symptoms: record.symptoms || '',
            prescription: record.prescription || '',
            remarks: record.remarks || '',
            chart: record.chart || '',
            chiefComplaint: record.chiefComplaint || '',
            presentIllness: record.presentIllness || '',
            pastHistory: record.pastHistory || '',
            physicalExam: record.physicalExam || '',
            patientId: record.patientId,
            doctorId: record.doctorId,
          };
          if (record.chart) {
            this.chartList = record.chart.split(',').filter(Boolean).map((url, i) => ({
              name: 'image' + i, url: url, uid: i
            }));
          }
          const pt = this.patientTableData.find(p => p.patientId === record.patientId);
          if (pt) {
            this.patientValue = String(pt.userId);
            this.selectPatient();
          }
        }
      });
    },
  }
}
</script>

<template>
  <div class="container">
    <h2 class="tittle">{{ editMode ? '编辑病历' : '新增病历' }}</h2>
    <div v-if="isDoctor">
      <!-- 患者选择 -->
      <div class="section">
        <el-form class="form">
          <el-form-item label="选择患者：" class="form-item">
            <el-select
                v-model="patientValue"
                filterable
                @change="selectPatient"
                placeholder="请选择患者"
                class="select-box">
              <el-option
                  v-for="item in patientOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <!-- 表格展示选择的患者信息以便确认 -->
      <div class="section">
        <el-table
            :data="selectedPatient"
            class="table"
        >
          <el-table-column prop="userId" label="用户ID"></el-table-column>
          <el-table-column prop="user.username" label="患者姓名"></el-table-column>
          <el-table-column prop="gender" label="性别"></el-table-column>
          <el-table-column prop="age" label="患者年龄（岁）"></el-table-column>
        </el-table>
      </div>
      <!--填写新增病历表单-->
      <el-form ref="form" v-if="isSelectPatient" :model="form" :rules="rules" label-width="100px" class="form">
        <!-- 医生信息 -->
        <el-divider content-position="left">医生信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="医生姓名"><el-input :value="user.username" disabled /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="科室"><el-input :value="doctorInfo.department || ''" disabled /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="职称"><el-input :value="doctorInfo.title || ''" disabled /></el-form-item>
          </el-col>
        </el-row>

        <!-- 病历模板 -->
        <el-divider content-position="left">病历模板</el-divider>
        <el-form-item label="选择模板">
          <el-select v-model="selectedTemplate" placeholder="选择诊断模板快速填充" @change="applyTemplate" clearable style="width: 100%">
            <el-option label="无（手动填写）" :value="null" />
            <el-option v-for="t in templates" :key="t.templateId" :label="t.diagnosisName" :value="t.templateId" />
          </el-select>
        </el-form-item>

        <!-- 诊疗信息 -->
        <el-divider content-position="left">诊疗信息</el-divider>
        <el-form-item label="主诉">
          <el-input type="textarea" v-model="form.chiefComplaint" placeholder="患者主要不适及持续时间" :rows="2" />
        </el-form-item>
        <el-form-item label="现病史">
          <el-input type="textarea" v-model="form.presentIllness" placeholder="发病经过、诊疗经过、一般情况" :rows="3" />
        </el-form-item>
        <el-form-item label="既往史">
          <el-input type="textarea" v-model="form.pastHistory" placeholder="既往疾病史、手术史、过敏史等" :rows="2" />
        </el-form-item>
        <el-form-item label="体格检查">
          <el-input type="textarea" v-model="form.physicalExam" placeholder="T/BP/HR/RR，各系统检查所见" :rows="2" />
        </el-form-item>
        <el-form-item label="西医诊断" prop="diagnosis">
          <el-input type="textarea" v-model="form.diagnosis" :rows="2" />
        </el-form-item>
        <el-form-item label="症状描述">
          <el-input type="textarea" v-model="form.symptoms" :rows="2" />
        </el-form-item>

        <!-- 处方 -->
        <el-divider content-position="left">处方与用药</el-divider>
        <el-form-item label="开具处方">
          <el-input type="textarea" v-model="form.prescription" :rows="3" placeholder="药品名称 剂量 频次 疗程，每行一个" />
        </el-form-item>
        <el-form-item label="用药提醒" v-if="drugAlerts.length > 0">
          <el-alert v-for="a in drugAlerts" :key="a" :title="a" type="warning" show-icon :closable="false" style="margin-bottom:5px" />
        </el-form-item>

        <!-- 图像 -->
        <el-divider content-position="left">附件</el-divider>
        <el-form-item label="上传图像">
          <el-upload
              action="http://localhost:9090/api/upload"
              ref="upload" drag multiple
              :file-list="chartList"
              :before-upload="beforeUpload"
              :on-success="handleSuccess"
              :on-error="handleError"
              :on-remove="handleFileRemove"
              name="file" accept="image/*"
              :headers="{ token: this.user.token}">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">可上传多张图像，单张不超过8mb</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remarks" :rows="2" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="onSubmit">{{ editMode ? '保存修改' : '保存病历（草稿）' }}</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>

  </div>
</template>

<style scoped>
/* 容器样式 */
.container {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 标题样式 */
.title {
  text-align: center;
  font-size: 2rem;
  color: #409eff;
  margin-bottom: 20px;
  font-weight: bold;
}

/* 每个部分的样式 */
.section {
  margin-bottom: 30px;
}

/* 表单项样式 */
.form-item {
  margin-bottom: 20px;
}

/* 输入框样式 */
.el-input,
.el-select {
  width: 100%;
  border-radius: 4px;
  box-sizing: border-box;
}

/* 按钮样式 */
.el-button {
  margin-right: 10px;
  font-size: 1rem;
}

/* 表格样式 */
.table {
  width: 100%;
  margin-top: 20px;
  border-radius: 8px;
}

.el-table {
  background-color: #fff;
  border-radius: 8px;
}

.el-table-column {
  text-align: center;
}

/* 选择框样式 */
.select-box {
  width: 100%;
}
</style>
