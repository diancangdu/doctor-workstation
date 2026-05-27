<script>
import Cookies from "js-cookie";

export default {
  name: "AppHeader",
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {}
    }
  },
  methods: {
    logout() {
      Cookies.remove('user')
      this.$router.push("/login")
    }
  }

}
</script>

<template>
  <div>
    <div style="height: 80px; line-height: 80px; background-color: #53a8ff; width: 100%; display: flex">
      <div style="width: 400px">
        <img src="@/assets/logo.png" alt="logo" style="width: 40px; position: relative; top: 10px; left: 20px">
        <span style="margin-left: 25px; font-size: 24px;">医生工作站系统</span>
      </div>
      <div style="flex: 1; text-align: right; padding-right: 20px">
        <el-dropdown placement="bottom">
            <span class="el-dropdown-link user-label" style="text-align: right; font-size: 18px; cursor: pointer">
              {{ user.username }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
          <el-dropdown-menu slot="dropdown" style="margin-top: -5px">
            <el-dropdown-item><div style="width: 50px; text-align: center" @click="logout">退出</div></el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<style scoped>


.el-dropdown-link {
  cursor: pointer;
  color: #ffffff;
}
.el-icon-arrow-down {
  font-size: 12px;
}
</style>
