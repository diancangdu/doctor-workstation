<script>
import Cookies from "js-cookie";

export default {
  name: 'AppAside',
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {},
      isCollapse: false,
      showLeftButton: true, // 默认显示左侧按钮
      thisWidth: 0,
      openMenu: ['1', '2', '3', '4', '5', '6', '7', '8'],
      role: {
        doctor: false,
        patient: false,
        admin: false,
        doctorOrAdmin: false
      }
    };
  },
  created() {
    if (this.user.role === 'doctor') {
      this.role.doctor = true
      this.role.doctorOrAdmin = true
    } else if (this.user.role === 'patient') {
      this.role.patient = true
    } else if (this.user.role === 'admin') {
      this.role.doctorOrAdmin = true
      this.role.admin = true
    }
  },
  methods: {

  },
  props: {
    width: {
      type: Number,
      default: 250,
    },
    collapsedWidth: {
      type: Number,
      default: 60,
    },
  },
}
</script>

<template>
  <div :style="{ width: width + 'px' }" ref="asideContainer">
    <el-menu
        default-active="1"
        class="el-menu-vertical-demo"
        :collapse="isCollapse"
        collapse-transition
        style="flex: 0 0 auto;"
        router="router"
        :default-openeds="openMenu">
      <el-menu-item index="1" :route="{path:'/'}" v-if="false">
        <i class="el-icon-location icon"></i>
        <span slot="title">首页</span>
      </el-menu-item>
      <el-submenu index="2" >
        <template slot="title">
          <i class="el-icon-user-solid"></i>
          <span>个人中心</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="2-1" :route="{path:'/PersonalCenter'}">
            <i class="el-icon-user"></i>
            <span slot="title">个人中心</span>
          </el-menu-item>
          <el-menu-item index="2-2" :route="{path:'/ChangePassword'}">
            <i class="el-icon-key"></i>
            <span slot="title">修改密码</span>
          </el-menu-item>
          <el-menu-item index="2-3" :route="{path:'/MyMedicalRecord'}" v-if="role.patient">
            <i class="el-icon-notebook-2"></i>
            <span slot="title">我的病历</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-submenu>
      <el-submenu index="3" v-if="role.doctorOrAdmin">
        <template slot="title">
          <i class="el-icon-s-management"></i>
          <span>门诊病历管理</span>
        </template>
        <el-menu-item-group >
          <el-menu-item index="3-1" :route="{path: '/RegistrationManage'}" v-if="role.doctorOrAdmin">
            <i class="el-icon-s-order"></i>
            <span slot="title">挂号管理</span>
          </el-menu-item>
          <el-menu-item index="3-2" :route="{path:'/DoctorQueue'}" v-if="role.doctor">
            <i class="el-icon-s-data"></i>
            <span slot="title">接诊排队</span>
          </el-menu-item>
          <el-menu-item index="3-3" :route="{path: '/AddMedicalRecord'}" v-if="role.doctor">
            <i class="el-icon-plus"></i>
            <span slot="title">新增病历</span>
          </el-menu-item>
          <el-menu-item index="3-4" :route="{path:'/ViewMedicalRecord'}" v-if="role.doctorOrAdmin">
            <i class="el-icon-notebook-2"></i>
            <span slot="title">查询病历</span>
          </el-menu-item>
          <el-menu-item index="3-5" :route="{path:'/CompareMedicalRecord'}" v-if="role.doctorOrAdmin">
            <i class="el-icon-sort"></i>
            <span slot="title">病历对比</span>
          </el-menu-item>
          <el-menu-item index="3-6" :route="{path:'/AssignedPatients'}" v-if="role.doctor">
            <i class="el-icon-user"></i>
            <span slot="title">查看名下患者</span>
          </el-menu-item>
          <el-menu-item index="3-7" :route="{path: '/BindPatientAndDoctor'}" v-if="role.doctorOrAdmin">
            <i class="el-icon-edit-outline"></i>
            <span slot="title">注册医生病人关系</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-submenu>
      <el-submenu index="5" v-if="role.doctorOrAdmin">
        <template slot="title">
          <i class="el-icon-s-management"></i>
          <span>处方管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="5-1" :route="{path:'/ViewPrescription'}" v-if="role.doctorOrAdmin">
            <i class="el-icon-user"></i>
            <span slot="title">查询处方</span>
          </el-menu-item>
          <el-menu-item index="5-2" :route="{path:'/AddPrescription'}" v-if="role.doctor">
            <i class="el-icon-notebook-2"></i>
            <span slot="title">新增处方</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-submenu>
      <!--<el-submenu index="6">-->
      <!--  <template slot="title">-->
      <!--    <i class="el-icon-chat-square"></i>-->
      <!--    <span>公告管理</span>-->
      <!--  </template>-->
      <!--  <el-menu-item-group>-->
      <!--    <el-menu-item index="6-1">-->
      <!--      <i class="el-icon-notebook-2"></i>-->
      <!--      <span slot="title">查看公告</span>-->
      <!--    </el-menu-item>-->
      <!--    <el-menu-item index="6-2">-->
      <!--      <i class="el-icon-edit-outline"></i>-->
      <!--      <span slot="title">管理公告</span>-->
      <!--    </el-menu-item>-->
      <!--  </el-menu-item-group>-->
      <!--</el-submenu>-->
      <el-submenu index="7" v-if="role.doctorOrAdmin">
        <template slot="title">
          <i class="el-icon-s-custom"></i>
          <span>用户管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="7-1" :route="{path:'/users'}" v-if="role.doctorOrAdmin">
            <i class="el-icon-s-custom"></i>
            <span slot="title">查询用户</span>
          </el-menu-item>
          <el-menu-item index="7-2" :route="{path:'/AddUser'}" v-if="role.admin">
            <i class="el-icon-document-add"></i>
            <span slot="title">添加用户</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-submenu>
    </el-menu>
  </div>
</template>

<style scoped>
/* 设置侧边栏容器样式 */
div {
  display: flex;
  flex-direction: column; /* 纵向布局 */
  height: 100%; /* 高度填充父容器 */
  background-color: #a0cfff; /* 侧边栏背景色 */
  border-right: 1px solid #e0e0e0; /* 右侧边框 */

}

/* 侧边栏菜单 */
.el-menu-vertical-demo {
  flex-grow: 1; /* 菜单占满容器剩余高度 */
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  min-height: 400px;
}

.el-menu-item {
  height: 80px;
  /* 设置背景颜色为红色，透明度为 0.5 */


}



</style>
