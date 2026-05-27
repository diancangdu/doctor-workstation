<script>
import Cookies from "js-cookie";
import request from "@/utils/request";

export default {
  name: "PersonalCenter",
  data() {
    return {
      user: Cookies.get("user") ? JSON.parse(Cookies.get("user")) : {},
      role: {
        patient: false,
        doctor: false,
        admin: false
      },
      personData: {}
    }
  },
  created() {
    if (this.user.role === "patient") {
      this.role.patient = true
    } else if (this.user.role === "doctor") {
      this.role.doctor = true
    } else if (this.user.role === "admin") {
      this.role.admin = true
    }
    this.loadByUserId()
  },
  methods: {
    loadByUserId() {
      let userId = { userId: this.user.userId }
      request({
        url: "/user/getByUserId",
        method: "get",
        params: userId
      }).then(res => {
        if (res.code === '200') {
          console.log(res.data)
          this.personData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<template>
  <div class="profile-container">
    <div class="profile-section">
      <el-descriptions title="基本信息" border direction="vertical" class="description-box">
        <el-descriptions-item label="UID">{{ user?.userId}}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ user?.username }}</el-descriptions-item>
        <el-descriptions-item label="用户角色"><i class="el-icon-user" /> {{ user?.role }}</el-descriptions-item>
        <el-descriptions-item label="手机号"><i class="el-icon-phone" /> {{ user?.phoneNumber}}</el-descriptions-item>
        <el-descriptions-item label="邮件"><i class="el-icon-message" /> {{ user?.email }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div v-if="role.patient" class="profile-section">
      <el-descriptions border direction="vertical" class="description-box">
        <el-descriptions-item label="患者编号">{{ personData?.patientId }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ personData?.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="联系地址">{{ personData?.address }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ personData?.dateOfBirth }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ personData?.emergencyContact}}</el-descriptions-item>
        <el-descriptions-item label="紧急联系电话">{{ personData?.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="用药历史">{{ personData?.medicalHistory }}</el-descriptions-item>
        <el-descriptions-item label="过敏反应">{{ personData?.allergies }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div v-if="role.doctor" class="profile-section">
      <el-descriptions border direction="vertical" class="description-box">
        <el-descriptions-item label="医生编号">{{ personData?.doctorId }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ personData?.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="所属部门"><i class="el-icon-s-flag" /> {{ personData?.department }}</el-descriptions-item>
        <el-descriptions-item label="从业年限"><i class="el-icon-s-promotion" /> {{ personData?.experienceYears }}</el-descriptions-item>
        <el-descriptions-item label="专业领域"><i class="el-icon-star-on" /> {{ personData?.specialty }}</el-descriptions-item>
        <el-descriptions-item label="资格证书"><i class="el-icon-postcard" /> {{ personData?.qualification }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div v-if="role.admin" class="profile-section">
      <el-descriptions border direction="vertical" class="description-box">
        <el-descriptions-item label="管理员编号">{{ personData?.adminId }}</el-descriptions-item>
        <el-descriptions-item label="管理等级"><i class="el-icon-s-marketing" /> {{ personData?.adminLevel }}</el-descriptions-item>
        <el-descriptions-item label="所属部门"><i class="el-icon-s-flag" /> {{ personData?.department }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<style scoped>
/* 主容器样式 */
.profile-container {
  width: 85%;
  max-width: 1000px;
  margin: 30px auto;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 每个部分的样式 */
.profile-section {
  margin-bottom: 30px;
}

/* Descriptions容器样式 */
.description-box {
  background-color: #fafafa;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* Descriptions中的条目项 */
.el-descriptions-item {
  padding: 12px 0;
  font-size: 1rem;
  color: #555;
}

/* 标签的样式 */
.el-descriptions-item .el-descriptions-item__label {
  font-weight: bold;
  color: #409eff;
  width: 150px;
}

/* 图标的样式 */
.el-icon-user, .el-icon-phone, .el-icon-message {
  margin-right: 8px;
  color: #409eff;
}

/* 字体设置 */
.el-descriptions-item .el-descriptions-item__content {
  font-size: 1rem;
  color: #333;
}
</style>
