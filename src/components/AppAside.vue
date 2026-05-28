<script>
import Cookies from "js-cookie";

export default {
  name: 'AppAside',
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {},
      now: new Date(),
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
    this.timer = setInterval(() => { this.now = new Date(); }, 1000);
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
  beforeDestroy() {
    clearInterval(this.timer);
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
  <div :style="{ width: width + 'px' }" ref="asideContainer" class="aside-wrapper">

    <div class="sidebar-clock" :class="{ collapsed: isCollapse }">
      <template v-if="!isCollapse">
        {{ String(now.getHours()).padStart(2,'0') }}:{{ String(now.getMinutes()).padStart(2,'0') }}:{{ String(now.getSeconds()).padStart(2,'0') }}
        <br/>
        <span class="clock-date">{{ now.getFullYear() }}-{{ String(now.getMonth()+1).padStart(2,'0') }}-{{ String(now.getDate()).padStart(2,'0') }}</span>
      </template>
      <template v-else>
        {{ String(now.getHours()).padStart(2,'0') }}:{{ String(now.getMinutes()).padStart(2,'0') }}
      </template>
    </div>

    <div class="aside-menu-wrapper">
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
  </div>
</template>

<style scoped>
.aside-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #a0cfff;
  border-right: 1px solid #e0e0e0;
}
.sidebar-clock {
  text-align: center;
  height: 80px;
  background: #53a8ff;
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  font-family: monospace;
  letter-spacing: 2px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.clock-date {
  font-size: 12px;
  font-weight: normal;
  letter-spacing: 0;
  opacity: 0.8;
}
.sidebar-clock.collapsed {
  font-size: 12px;
  height: 80px;
  letter-spacing: 0;
  background: #a0cfff;
}
.aside-menu-wrapper {
  flex: 1;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.aside-menu-wrapper::-webkit-scrollbar {
  display: none;
}
.aside-menu-wrapper .el-menu {
  border-right: none;
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
