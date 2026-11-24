<template>
  <div class="login">
    <!-- 星座连线科技背景 -->
    <svg class="constellation" viewBox="0 0 1440 810" preserveAspectRatio="xMidYMid slice" aria-hidden="true">
      <g stroke="rgba(255,255,255,0.16)" stroke-width="1" fill="none">
        <path d="M80 120 L260 70 L410 180 L560 90 L720 160 L900 80 L1080 170 L1260 90 L1400 190" />
        <path d="M60 300 L220 360 L380 280 L540 380 L700 300 L880 390 L1060 310 L1240 400 L1420 330" />
        <path d="M100 520 L280 460 L440 560 L620 480 L800 570 L980 490 L1160 580 L1340 500" />
        <path d="M120 700 L300 640 L480 720 L660 650 L840 730 L1020 660 L1200 740 L1380 670" />
        <path d="M260 70 L220 360 L280 460 L300 640" />
        <path d="M560 90 L540 380 L620 480 L660 650" />
        <path d="M900 80 L880 390 L980 490 L1020 660" />
        <path d="M1260 90 L1240 400 L1340 500 L1380 670" />
      </g>
      <g fill="rgba(255,255,255,0.5)">
        <circle cx="80" cy="120" r="2" /><circle cx="260" cy="70" r="2" /><circle cx="410" cy="180" r="2" />
        <circle cx="560" cy="90" r="2" /><circle cx="720" cy="160" r="2" /><circle cx="900" cy="80" r="2" />
        <circle cx="1080" cy="170" r="2" /><circle cx="1260" cy="90" r="2" /><circle cx="1400" cy="190" r="2" />
        <circle cx="220" cy="360" r="2" /><circle cx="540" cy="380" r="2" /><circle cx="880" cy="390" r="2" />
        <circle cx="1240" cy="400" r="2" /><circle cx="280" cy="460" r="2" /><circle cx="620" cy="480" r="2" />
        <circle cx="980" cy="490" r="2" /><circle cx="1340" cy="500" r="2" /><circle cx="300" cy="640" r="2" />
        <circle cx="660" cy="650" r="2" /><circle cx="1020" cy="660" r="2" /><circle cx="1380" cy="670" r="2" />
      </g>
    </svg>

    <div class="login-wrapper">
      <!-- 左侧品牌展示区 -->
      <div class="brand-section">
        <div class="brand-icon">
          <img :src="plcLogo" alt="logo" />
        </div>
        <h1 class="brand-title">PLC设备<br>管理系统</h1>
        <p class="brand-subtitle">精准 · 可靠 · 实时 · 智控</p>
        <div class="brand-bar"><span></span></div>

        <div class="feature-line">
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z" /></svg>
            </div>
            <span class="feature-text">多重安全验证</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 2v11h3v9l7-12h-4l4-8z" /></svg>
            </div>
            <span class="feature-text">极速响应</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3.5 18.5l5-5 4 4 8-8M16 9.5h4.5V14" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" /></svg>
            </div>
            <span class="feature-text">实时监控</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录卡片 -->
      <div class="login-card">
        <h3 class="card-title">管理员登录</h3>
        <p class="card-subtitle">PLC设备管理后台系统</p>
        <div class="card-underline"></div>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="card-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              show-password
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled" class="code-item">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="请输入验证码"
              style="width: 63%"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>

          <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>

          <el-form-item class="btn-item" style="width:100%;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              class="login-btn"
              @click.native.prevent="handleLogin"
            >
              <img class="btn-icon" :src="plcLogo" alt="" />
              <span v-if="!loading">登录系统</span>
              <span v-else>登录中...</span>
            </el-button>
            <div class="register-link" style="float: right;" v-if="register">
              <router-link class="link-type" :to="'/register'">立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>

        <div class="card-tips">
          <span><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15l-5-5 1.41-1.41L11 14.17l7.59-7.59L20 8l-9 9z" /></svg>请确认本地网络畅通</span>
          <span><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15l-5-5 1.41-1.41L11 14.17l7.59-7.59L20 8l-9 9z" /></svg>连续错误5次将锁定15分</span>
        </div>
        <div class="card-links">
          <span>初次登录使用初始密码</span>
          <a href="#" @click.prevent>技术支持</a>
        </div>
        <div class="card-dots">
          <span>1</span><span class="active">2</span><span>3</span>
        </div>
      </div>
    </div>

    <!-- 底部版权 -->
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'
import plcLogo from '@/assets/logo/plc.svg'

export default {
  name: "Login",
  data() {
    return {
      plcLogo,
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 560px;
  overflow: hidden;
  background: linear-gradient(120deg, #2f7bff 0%, #2563eb 45%, #1a4fd6 100%);
}
.constellation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.login-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: min(1120px, 92%);
  gap: 60px;
}

/* 左侧品牌区 */
.brand-section {
  flex: 1;
  color: #fff;
  padding-left: 10px;
}
.brand-icon {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  background: linear-gradient(135deg, #7db4ff 0%, #3f83ff 100%);
  box-shadow: 0 10px 30px rgba(12, 58, 160, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 34px;
  img { width: 38px; height: 38px; }
}
.brand-title {
  margin: 0;
  font-size: 58px;
  line-height: 1.18;
  font-weight: 700;
  letter-spacing: 4px;
  text-shadow: 0 4px 18px rgba(10, 50, 140, 0.25);
}
.brand-subtitle {
  margin: 22px 0 0;
  font-size: 16px;
  letter-spacing: 3px;
  color: rgba(255, 255, 255, 0.92);
}
.brand-bar {
  width: 230px;
  height: 3px;
  margin-top: 26px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.3);
  span { display: block; width: 68%; height: 100%; border-radius: 3px; background: #fff; }
}

.feature-line {
  position: relative;
  width: 440px;
  max-width: 100%;
  margin-top: 46px;
  height: 110px;
  &::before {
    content: "";
    position: absolute;
    top: 22px;
    left: 8%;
    right: 8%;
    height: 1px;
    background: rgba(255, 255, 255, 0.32);
  }
}
.feature-item {
  position: absolute;
  top: 0;
  text-align: center;
  &:nth-child(1) { left: 0; }
  &:nth-child(2) { left: 50%; transform: translateX(-50%); }
  &:nth-child(3) { right: 0; }
}
.feature-icon {
  width: 44px;
  height: 44px;
  margin: 0 auto;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  svg { width: 22px; height: 22px; fill: #fff; }
}
.feature-text {
  display: block;
  margin-top: 12px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  white-space: nowrap;
}

/* 右侧登录卡片 */
.login-card {
  width: 400px;
  flex-shrink: 0;
  padding: 38px 38px 26px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 20px 60px rgba(11, 46, 130, 0.28);
}
.card-title {
  margin: 0;
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  color: #2563eb;
  letter-spacing: 1px;
}
.card-subtitle {
  margin: 8px 0 0;
  text-align: center;
  font-size: 13px;
  color: #a0a8b8;
}
.card-underline {
  width: 36px;
  height: 3px;
  margin: 16px auto 26px;
  border-radius: 3px;
  background: #2563eb;
}
.card-form {
  ::v-deep .el-form-item {
    margin-bottom: 18px;
  }
  ::v-deep .el-input__inner {
    height: 44px;
    line-height: 44px;
    background: #f4f7ff;
    border: 1px solid #e6edff;
    border-radius: 9px;
    padding-left: 42px;
    color: #333;
    &:focus { border-color: #2563eb; background: #fff; }
    &::placeholder { color: #b6c0d4; }
  }
  ::v-deep .el-input__icon {
    line-height: 44px;
    color: #6f9bf5;
  }
  .input-icon {
    height: 44px;
    width: 16px;
    margin-left: 4px;
  }
  .login-code {
    width: 34%;
    height: 44px;
    float: right;
    img { cursor: pointer; vertical-align: middle; }
  }
  ::v-deep .code-item .el-form-item__content {
    display: flex;
    align-items: center;
    .el-input { width: 63% !important; }
    .login-code { width: 34%; float: none; margin-left: 3%; }
    .login-code-img { width: 100%; height: 44px; border-radius: 6px; }
  }
}
.remember-me {
  margin: 0 0 18px;
  font-size: 13px;
  color: #8b95a8;
}
.btn-item {
  margin-bottom: 8px;
  ::v-deep .el-form-item__content {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 2px;
  border: none;
  border-radius: 9px;
  background: linear-gradient(90deg, #2f7bff 0%, #1e5fe6 100%);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.28);
  &:hover { background: linear-gradient(90deg, #1e6cf0 0%, #1752d6 100%); }
  .btn-icon { width: 18px; height: 18px; margin-right: 6px; vertical-align: -3px; }
}
.register-link {
  font-size: 13px;
}
.card-tips {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin-top: 18px;
  font-size: 11px;
  color: #a6afbf;
  span { display: inline-flex; align-items: center; white-space: nowrap; }
  svg { width: 12px; height: 12px; fill: #6f9bf5; margin-right: 3px; }
}
.card-links {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 12px;
  color: #8b95a8;
  a { color: #2563eb; text-decoration: none; }
}
.card-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
  span {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    font-size: 11px;
    color: #b6c0d4;
    display: flex;
    align-items: center;
    justify-content: center;
    &.active { background: #2563eb; color: #fff; }
  }
}

.el-login-footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 36px;
  line-height: 36px;
  text-align: center;
  color: rgba(255, 255, 255, 0.75);
  font-size: 12px;
  letter-spacing: 1px;
  z-index: 1;
}

@media (max-width: 980px) {
  .brand-section { display: none; }
  .login-wrapper { justify-content: center; }
}
</style>
