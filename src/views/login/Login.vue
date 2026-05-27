<script>
import request from '@/utils/request'
import Cookies from 'js-cookie'

export default {
  name: 'Login',
  data() {
    return {
      loginUser: {},
      user: {},
      loginRules: {
        phoneNumber: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            { min: 6, max: 18, message: '长度在 6 到 18 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    login() {
      this.$refs['loginForm'].validate((valid) => {
        if(valid) {
          // console.log(this.user)
          request.post('/user/login', this.user).then(res => {
            if(res.code === '200') {
              this.loginUser = res.data
              // 滑块验证组件
              if (res.data != null) {
                // console.log(JSON.stringify(res.data))
                // Cookies.set('token', res.data.token)
                Cookies.set('user', JSON.stringify(res.data), { expires: 7 }) // 设置 cookie，有效期 1 天
              }
              this.$notify.success('登录成功')
              this.$router.push('/')
            } else {
              this.$notify.error(res.msg)
            }
          })
        }
      })
    },
    onSuccess(){
      if (res.data != null) {
        // console.log(JSON.stringify(res.data))
        // Cookies.set('token', res.data.token)
        Cookies.set('user', JSON.stringify(res.data), { expires: 7 }) // 设置 cookie，有效期 1 天
      }
      this.$notify.success('登录成功')
      this.$router.push('/')
    },
    onFail(){
      this.msg = ''
    },
    onRefresh(){
      this.msg = ''
    }
  },
}
</script>

<template>
<div style="height: 100vh; overflow: hidden; position: relative">

  <!--<el-card class="cover" v-if="loginUser.userId">-->
  <!--  <slide-verify :l="42"-->
  <!--                :r="10"-->
  <!--                :w="310"-->
  <!--                :h="155"-->
  <!--                slider-text="向右滑动"-->
  <!--                @success="onSuccess"-->
  <!--                @fail="onFail"-->
  <!--                @refresh="onRefresh"-->
  <!--  ></slide-verify>-->
  <!--</el-card>-->

  <div style="width: 500px; height: 400px; background-color: #ecf5ff; border-radius: 10px;
  margin: 150px auto; padding: 50px;">
    <h1 style="text-align: center; margin: 30px; font-weight: bold; color: dodgerblue">医生工作站系统</h1>
    <el-form :model="user" ref="loginForm" style="margin: 50px" :rules="loginRules">
      <el-form-item  prop="phoneNumber">
        <el-input v-model="user.phoneNumber" placeholder="请输入手机号" size="medium" prefix-icon="el-icon-user"></el-input>
      </el-form-item>
      <el-form-item  prop="password">
        <el-input v-model="user.password" show-password placeholder="请输入密码" size="medium" prefix-icon="el-icon-key"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button style="width: 100%; margin-top: 30px" size="medium" type="primary" @click="login">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</div>
</template>

<style scoped>
.cover {
  width: fit-content;
  background-color: white;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
}
</style>
