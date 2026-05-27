<script>
import request from "@/utils/request";
import dayjs from 'dayjs';
import Cookies from "js-cookie";

export default {
  name: 'AppBodyTable',
  data() {
    return {
      user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : {},
      tableData: [
      ],
      rawTableDate: [],
      rowId: 0, // 当前选中行的id
      patientData: [],
      doctorData: [],
      adminData: [],
      total: 0,
      params: {
        username: '',
        phoneNumber: '',
        email: '',
        pageSize: 13,
        pageNum: 1
      },
      // 角色选择
      roleOptions: [
        {value: 'admin', label: '管理员'},
        {value: 'patient', label: '患者'},
        {value: 'doctor', label: '医生'}
      ],
      // 修改规则
      updateRules: {
        'user.phoneNumber': [
          { required: true, message: '请输入电话号码', trigger: 'blur' },
          { validator: this.checkPhone, trigger: 'blur' }
        ],
        'user.username': [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请输入科室', trigger: 'blur' }
        ],
        adminLevel: [
          { required: true, message: '请选择管理等级', trigger: 'change' },
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' },
        ],
        dateOfBirth: [
          { required: true, message: '请选择出生日期', trigger: 'change' },
        ],
        specialty: [
          { required: true, message: '请输入专业', trigger: 'blur' },
        ],
        experienceYears: [
          { required: true, message: '请输入工作年限', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min:6, max: 18, message: '密码为6~18位', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min:6, max: 18, message: '密码为6~18位', trigger: 'blur' }
        ],
        'user.email': [
          { required: false, message: '请输入邮箱', trigger: 'blur' },
          { validator: this.checkEmail, trigger: 'blur' }
        ]
      },
      // 管理等级选择
      adminLevelOptions: [
        {value: 'normal', label: '普通管理员'},
        {value: 'super', label: '超级管理员'}
      ],
      // 控制详细信息对话框显示
      dialogTableVisible: false,
      // 控制修改界面对话框显示
      updateDialogFormVisible: false,
      // 控制修改密码界面对话框显示
      changePassDialogFormVisible: false,
      // 存储选中的用户详细信息
      selectedUser: null,
      // 存储临时数据
      tempData: {
        user: { username: '', email: '', password: '', phoneNumber: ''}
      },
      // 修改密码表单
      changePassForm: {},
      // rawSelectedUser: null, // 用于显示的选中的用户详细信息
      // 控制详细信息对话框角色显示
      patientDetailBool: false,
      doctorDetailBool: false,
      adminDetailBool: false,
      // 控制详细信息对话框表单
      patientDetailForm: false,
      doctorDetailForm: false,
      adminDetailForm: false,
    }
  },
  created() {
    this.load();
    this.loadPatients();
    this.loadDoctors();
    this.loadAdmins();
  },
  methods: {
    load() {
      request.get('/user/page', {params: this.params}).then(res => {
        // console.log("res:" + JSON.stringify(res, null, 2))
        if (res.code === '200') {
          this.tableData = res.data.list
          this.total = res.data.total
          this.handleDisplayTable()
          // console.log(JSON.stringify(this.tableData))
        }
      })
    },
    // 获取患者信息
    loadPatients() {
      request.get('/user/patient', {params: this.params}).then(res => {

        if (res.code === '200') {
          this.patientData = res.data.list
          // console.log(JSON.stringify(this.patientData))
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    // 获取医生信息
    loadDoctors() {
      request.get('/user/doctor', {params: this.params}).then(res => {
        // console.log("res:" + JSON.stringify(res, null, 2))
        if (res.code === '200') {
          this.doctorData = res.data
          // console.log(JSON.stringify(this.doctorData))
        }
      })
    },
    // 获取管理员信息
    loadAdmins() {
      request.get('/user/admin', {params: this.params}).then(res => {
        // console.log("res:" + JSON.stringify(res, null, 2))
        if (res.code === '200') {
          this.adminData = res.data
          // console.log(res)
        }
      })
    },
    reset() {
      this.params = {
        username: '',
        phoneNumber: '',
        pageSize: 13,
        pageNum: 1
      }
      this.load()
    },
    // 处理展示信息
    handleDisplayTable() {
      if (this.tableData === null || this.tableData.length === 0) {
        return
      }
      this.rawTableDate = this.tableData;
      try {
        for (let i = 0; i < this.tableData.length; i++) {
          if (this.tableData[i].role === 'admin') {
            this.tableData[i].role = '管理员'
          } else if (this.tableData[i].role === 'patient') {
            this.tableData[i].role = '患者'
          } else if (this.tableData[i].role === 'doctor') {
            this.tableData[i].role = '医生'
          }
          let date = new Date(this.tableData[i].createdAt)
          this.tableData[i].createdAt = dayjs(date).format('YYYY-MM-DD HH:mm:ss')
          date = new Date(this.tableData[i].lastLoginAt)
          this.tableData[i].lastLoginAt = dayjs(date).format('YYYY-MM-DD HH:mm:ss')
        }
      } catch (e) {
        console.log(e)
      }
    },
    handleDisplaySelectedUser() {
      if(this.selectedUser === null || this.selectedUser.length === 0) {
        return
      }
      if (this.selectedUser.user.role === 'patient') {
        this.selectedUser.user.role = '患者';
        this.selectedUser.gender = this.selectedUser.gender === 'male' ? '男' : '女'
        let dateOfBirth = new Date(this.selectedUser.dateOfBirth)
        this.selectedUser.dateOfBirth = dayjs(dateOfBirth).format('YYYY-MM-DD')

        this.patientDetailBool = true
        this.doctorDetailBool = false
        this.adminDetailBool = false
      } else if (this.selectedUser.user.role === 'doctor') {
        this.selectedUser.user.role = '医生'
        this.selectedUser.gender = this.selectedUser.gender === 'male' ? '男' : '女'

        this.patientDetailBool = false
        this.doctorDetailBool = true
        this.adminDetailBool = false
      } else if (this.selectedUser.user.role === 'admin') {
        this.selectedUser.user.role = '管理员'
        this.selectedUser.adminLevel = this.selectedUser.adminLevel === 'super' ? '超级管理员' : '普通管理员'

        this.patientDetailBool = false
        this.doctorDetailBool = false
        this.adminDetailBool = true
      }
      let date = new Date(this.selectedUser.user.createdAt)
      this.selectedUser.user.createdAt = dayjs(date).format('YYYY-MM-DD HH:mm:ss')
      date = new Date(this.selectedUser.user.lastLoginAt)
      this.selectedUser.user.lastLoginAt = dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    },
    // 点击分页
    handleCurrentPageChange(pageNum) {
      this.params.pageNum = pageNum
      this.load()
    },
    // // 点击每页显示多少条
    // handleSizeChange(pageSize) {
    //   this.params.pageSize = pageSize
    //   this.load()
    // },
    // 查看详细
    viewDetails(rowUser) {
      // console.log(JSON.stringify(rowUser))
      // console.log(rowUser.userId)
      // console.log(this.patientData)
      // console.log(this.adminData)
      if (rowUser.role === '患者') {
        try {
          for (let i = 0; i < this.patientData.length; i++) {
            // console.log(this.patientData[i].userId)
            if (this.patientData[i].userId === rowUser.userId) {
              this.selectedUser = this.patientData[i]
              // console.log(this.selectedUser)
              break
            }
          }
        } catch (e) {
          console.error(e)
        }
        // console.log(this.dialogTableVisible)
        this.dialogTableVisible = true;
      }
      if (rowUser.role === '医生') {
        try {
          for (let i = 0; i < this.doctorData.length; i++) {
            // console.log(JSON.stringify(this.doctorData[i].userId))
            if (this.doctorData[i].userId === rowUser.userId) {
              this.selectedUser = this.doctorData[i]
              // console.log(JSON.stringify(this.selectedUser))
              break
            }
          }
        } catch (e) {
          console.error(e)
        }
        this.dialogTableVisible = true;
      }
      if (rowUser.role === '管理员') {
        try {
          for (let i = 0; i < this.adminData.length; i++) {
            // console.log(JSON.stringify(this.adminData[i].userId))
            if (this.adminData[i].userId === rowUser.userId) {
              this.selectedUser = this.adminData[i]
              // console.log(JSON.stringify(this.selectedUser))
              break
            }
          }
        } catch (e) {
          console.error(e)
        }
        this.dialogTableVisible = true;
      }
      console.log(this.selectedUser)
      this.handleDisplaySelectedUser()
    },
    // 查看修改表单
    viewUpdateForm(rowUser) {
      console.log(this.patientData)
      // console.log(rowUser)
      if (rowUser.role === '患者') {
        this.patientDetailForm = true
        this.doctorDetailForm = false
        this.adminDetailForm = false
        try {
          for (let i = 0; i < this.patientData.length; i++) {
            if (this.patientData[i].userId === rowUser.userId) {
              this.tempData = {...this.patientData[i]};
              this.selectedUser = {...this.patientData[i]};
              break
            }
          }
        } catch (e) {
          console.error(e)
        }
        this.updateDialogFormVisible = true;
      }
      if (rowUser.role === '医生') {
        this.patientDetailForm = false
        this.doctorDetailForm = true
        this.adminDetailForm = false
        try {
          for (let i = 0; i < this.doctorData.length; i++) {
            if (this.doctorData[i].userId === rowUser.userId) {
              this.tempData = {...this.doctorData[i]};
              this.selectedUser = {...this.doctorData[i]};
              break
            }
          }
        } catch (e) {
          console.error(e)
        }
        this.updateDialogFormVisible = true;
      }
      if (rowUser.role === '管理员') {
        this.patientDetailForm = false
        this.doctorDetailForm = false
        this.adminDetailForm = true
        try {
          for (let i = 0; i < this.adminData.length; i++) {
            if (this.adminData[i].userId === rowUser.userId) {
              this.tempData = {...this.adminData[i]};
              this.selectedUser = {...this.adminData[i]};
              break
            }
          }
        } catch (e) {
          console.error(e)
        }
        this.updateDialogFormVisible = true;
      }
    },
    // 删除角色请求
    deleteUserRequest(rowUser) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log(JSON.stringify(rowUser))
        rowUser.createdAt = '';
        rowUser.lastLoginAt = '';
        request({
          url: '/user/deleteUserByRole',
          method: 'post',
          data: rowUser
        }).then(res => {
          if (res.code === '200') {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
          } else {
            this.$message.error(res.msg);
          }
          this.load();
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    // 修改角色请求
    updateUserRequest() {
      // console.log(JSON.stringify(this.selectedUser))
      // console.log(JSON.stringify(this.tempData))
      this.selectedUser = {...this.tempData}

      this.selectedUser.user.createdAt = null;
      this.selectedUser.user.lastLoginAt = null;
      this.selectedUser.updatedAt = Date.now();

      this.selectedUser.user.status = true;

      // console.log(this.selectedUser)
      this.$confirm('请确认修改信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$refs['updateForm'].validate((valid) => {
          if (valid) {
            let requestUrl
            if(this.selectedUser.user.role === 'patient') {
              requestUrl= '/user/updatePatient';
            } else if(this.selectedUser.user.role === 'doctor') {
              requestUrl= '/user/updateDoctor';
            } else if(this.selectedUser.user.role === 'admin') {
              requestUrl= '/user/updateAdmin';
            }
            this.selectedUser.user.createdAt = null
            this.selectedUser.user.lastLoginAt = null
            request({
              url: requestUrl,
              method: 'post',
              data: this.selectedUser
            }).then(res => {
              if (res.code === '200') {
                this.$message({
                  type: 'success',
                  message: '修改成功!'
                });
                this.updateDialogFormVisible = false;
                this.tempData = this.selectedUser;
                this.load();
              } else {
                this.$message({
                  type: 'error',
                  message: res.msg
                })
              }
            })
          }
        })
      }).catch( () => {
        this.$message({
          type: 'info',
          message: '已取消修改'
        });
      })
    },
    // 取消
    handleCancel() {
      // console.log(this.tempData)
      this.updateDialogFormVisible = false;
    },
    // 打开修改密码对话框
    viewChangePasswordForm(row) {
      this.changePassForm = JSON.parse(JSON.stringify(row));
      // console.log(this.changePassForm);
      this.changePassDialogFormVisible = true;

    },
    // 修改密码请求
    changePassRequest() {
      console.log(this.changePassForm);
      this.$refs['changePassForm'].validate((valid) => {
        if (valid) {
          request.put('/user/updatePassword', this.changePassForm).then(res => {
            if(res.code === '200') {
              this.$notify.success("修改密码成功")
              if (this.user.userId === this.changePassForm.userId) {
                Cookies.remove('user')
                this.$router.push('/login');
              }
              this.load();
              this.changePassDialogFormVisible = false;
            } else {
              this.$notify.error("修改密码失败")
            }
          })
        }
      })
    },
    // 修改用户状态
    changeStatus(row) {
      if (this.user.userId === row.userId && !row.status) {
        row.status = true
        this.$notify.error("不能修改自己的状态")
        return
      }
      let temp = row
      temp.createdAt = null
      temp.lastLoginAt = null
      request.put('/user/updateUser', row).then(res => {
        if(res.code === '200') {
          this.$notify.success("修改用户状态成功")
          this.load();
        } else {
          this.$notify.error(res.msg)
        }
      })
    },

    // 表单验证
    checkPhone(rule, value, callback) {
      // console.log(value)
      if (!/^[1][3,4,5,6,7,8,9][0-9]{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'));
      }
      callback();
    },
    checkEmail(rule, value, callback) {
      // console.log(value);
      if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value)) {
        callback(new Error('请输入正确的邮箱'));
      }
      callback();
    },
    checkPassword(rule, value, callback) {
      console.log(value);
    }
  }
}
</script>

<template>
  <div class="user-management-page">
    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input placeholder="输入名称" v-model="params.username" class="search-input" />
          </el-col>
          <el-col :span="6">
            <el-input placeholder="请输入电话" v-model="params.phoneNumber" class="search-input" />
          </el-col>
          <el-col :span="6">
            <el-input placeholder="请输入邮件" v-model="params.email" class="search-input" />
          </el-col>
          <el-col :span="6">
            <el-select v-model="params.role" placeholder="筛选角色" class="search-input">
              <el-option
                  v-for="item in roleOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                  :disabled="item.disabled"
              />
            </el-select>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="search-actions">
          <el-col :span="6">
            <el-button type="primary" block @click="load">
              <i class="el-icon-search"></i> 搜索
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="warning" block @click="reset">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table :data="tableData" stripe border highlight-current-row>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="userId" label="ID" width="100" />
        <el-table-column prop="username" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="80" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phoneNumber" label="电话" />
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column prop="lastLoginAt" label="最后登录时间" />
        <el-table-column label="操作" width="280">
          <template #default="scoped">
            <div class="action-btns">
              <el-button type="text" size="small" @click="viewDetails(scoped.row)">详细</el-button>
              <el-button
                type="text"
                size="small"
                @click="viewUpdateForm(scoped.row)"
                v-if="user.role === 'admin'">修改</el-button>
              <el-button
                type="text"
                size="small"
                @click="viewChangePasswordForm(scoped.row)"
                v-if="scoped.row.userId === user.userId || user.role === 'admin'">修改密码</el-button>
              <el-button
                type="text"
                size="small"
                style="color: #F56C6C"
                @click="deleteUserRequest(scoped.row)"
                v-if="scoped.row.role !== '管理员' && user.role === 'admin'">删除</el-button>
            </div>
          </template>
        </el-table-column>
<!--        <el-table-column label="状态" width="100">-->
<!--          <template #default="scoped">-->
<!--            <el-switch-->
<!--                v-model="scoped.row.status"-->
<!--                active-color="#13ce66"-->
<!--                inactive-color="#ff4949"-->
<!--                @change="changeStatus(scoped.row)"-->
<!--            />-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-card>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          layout="prev, pager, next, total"
          @current-change="handleCurrentPageChange"
          :total="total"
      />
    </div>

    <!--详细对话框-->
    <el-dialog
        :visible.sync="dialogTableVisible"
        width="80%"
        >
      <el-descriptions class="margin-top" title="详细信息" :column="3" border>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            用户姓名
          </template>
          {{ this.selectedUser?.user.username }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            用户ID
          </template>
          {{ this.selectedUser?.user.userId }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-outline"></i>
            角色
          </template>
          {{ this.selectedUser?.user.role }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class=" el-icon-mobile-phone"></i>
            手机号
          </template>
          {{ this.selectedUser?.user.phoneNumber }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-message"></i>
            邮件
          </template>
          {{ this.selectedUser?.user.email }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            创建于
          </template>
          {{ this.selectedUser?.user.createdAt }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            最后登录
          </template>
          {{ this.selectedUser?.user.lastLoginAt }}
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions class="margin-top" title="管理员信息" :column="3" border v-if="adminDetailBool">
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            管理员编号
          </template>
          {{ this.selectedUser?.adminId }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-finished"></i>
            管理等级
          </template>
          {{ this.selectedUser?.adminLevel }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            所属部门
          </template>
          {{ this.selectedUser?.department }}
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions class="margin-top" title="患者信息" :column="3" border v-if="patientDetailBool">
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            患者编号
          </template>
          {{ this.selectedUser?.patientId }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            性别
          </template>
          {{ this.selectedUser?.gender }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-time"></i>
            出生日期
          </template>
          {{ this.selectedUser?.dateOfBirth }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-outline"></i>
            联系地址
          </template>
          {{ this.selectedUser?.address }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-s-custom"></i>
            紧急联系人
          </template>
          {{ this.selectedUser?.emergencyContact }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-phone-outline"></i>
            紧急联系电话
          </template>
          {{ this.selectedUser?.contactPhone }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            用药历史
          </template>
          {{ this.selectedUser?.medicalHistory }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            过敏症状
          </template>
          {{ this.selectedUser?.allergies }}
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions class="margin-top" title="医生信息" :column="3" border v-if="doctorDetailBool">
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            医生编号
          </template>
          {{ this.selectedUser?.doctorId }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            性别
          </template>
          {{ this.selectedUser?.gender }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            所属部门
          </template>
          {{ this.selectedUser?.department }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-timer"></i>
            工作年限
          </template>
          {{ this.selectedUser?.experienceYears }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-medal"></i>
            专业
          </template>
          {{ this.selectedUser?.specialty }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            资格证书
          </template>
          {{ this.selectedUser?.qualification }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!--修改对话框-->
    <el-dialog
        :visible.sync="updateDialogFormVisible"
        width="50%"
    >
      <el-form ref="updateForm" :model="tempData" label-width="100px" v-if="adminDetailForm" :rules="updateRules">
        <el-form-item label="姓名" prop="user.username">
          <el-input v-model="tempData.user.username"></el-input>
        </el-form-item>
        <el-form-item label="电话号码" prop="user.phoneNumber">
          <el-input v-model="tempData.user.phoneNumber"></el-input>
        </el-form-item>
        <el-form-item label="邮件地址" prop="user.email">
          <el-input v-model="tempData.user.email"></el-input>
        </el-form-item>
        <el-form-item label="管理等级" prop="adminLevel">
          <el-select v-model="tempData.adminLevel" placeholder="请选择">
            <el-option
              v-for="item in adminLevelOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门" prop="department">
          <el-input v-model="tempData.department"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateUserRequest">提交修改</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>

      <el-form ref="updateForm" :model="tempData" label-width="100px" v-if="patientDetailForm" :rules="updateRules">
        <el-form-item label="姓名" prop="user.username">
          <el-input v-model="tempData.user.username"></el-input>
        </el-form-item>
        <el-form-item label="电话号码" prop="user.phoneNumber">
          <el-input v-model="tempData.user.phoneNumber"></el-input>
        </el-form-item>
        <el-form-item label="邮件地址" prop="user.email">
          <el-input v-model="tempData.user.email"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio v-model="tempData.gender" label="male">男</el-radio>
          <el-radio v-model="tempData.gender" label="female">女</el-radio>
        </el-form-item>
        <el-form-item label="出生年月" prop="dateOfBirth">
          <el-date-picker
              v-model="tempData.dateOfBirth"
              type="month"
              placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="tempData.address"></el-input>
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="tempData.emergencyContact"></el-input>
        </el-form-item>
        <el-form-item label="紧急联系电话" prop="contactPhone">
          <el-input v-model="tempData.contactPhone"></el-input>
        </el-form-item>
        <el-form-item label="用药历史" prop="medicalHistory">
          <el-input type="textarea" v-model="tempData.medicalHistory"></el-input>
        </el-form-item>
        <el-form-item label="过敏症状" prop="allergies">
          <el-input type="textarea" v-model="tempData.allergies"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateUserRequest">提交修改</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>

      <el-form ref="updateForm" :model="tempData" label-width="100px" v-if="doctorDetailForm" :rules="updateRules">
        <el-form-item label="姓名" prop="user.username">
          <el-input v-model="tempData.user.username"></el-input>
        </el-form-item>
        <el-form-item label="电话号码" prop="user.phoneNumber">
          <el-input v-model="tempData.user.phoneNumber"></el-input>
        </el-form-item>
        <el-form-item label="邮件地址" prop="user.email">
          <el-input v-model="tempData.user.email"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio v-model="tempData.gender" label="male">男</el-radio>
          <el-radio v-model="tempData.gender" label="female">女</el-radio>
        </el-form-item>
        <el-form-item label="所属部门" prop="department">
          <el-input v-model="tempData.department"></el-input>
        </el-form-item>
        <el-form-item label="工作年限" prop="experienceYears">
          <el-input-number v-model="tempData.experienceYears" @change="handleChange" :min="0" :max="70" label="请输入"></el-input-number>
        </el-form-item>
        <el-form-item label="专业领域" prop="specialty">
          <el-input v-model="tempData.specialty"></el-input>
        </el-form-item>
        <el-form-item label="资格证书" prop="qualification">
          <el-input v-model="tempData.qualification"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateUserRequest">提交修改</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!--修改密码对话框-->
    <el-dialog
      :visible.sync="changePassDialogFormVisible"
      width="30%"
      title="修改密码">
      <el-form ref="changePassForm" :model="changePassForm" label-width="100px" :rules="{ newPassword: updateRules.newPassword }">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="changePassForm.newPassword" show-password placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePassRequest">确认修改</el-button>
          <el-button @click="changePassDialogFormVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-management-page {
  padding: 20px;
  background-color: #f9f9f9;
}

.search-card,
.table-card {
  margin-bottom: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-form {
  padding: 10px;
}

.search-input {
  width: 100%;
}

.search-actions {
  margin-top: 10px;
}

.table-card .el-table {
  border-radius: 8px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.action-btns {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
</style>
