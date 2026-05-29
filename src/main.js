import Vue from 'vue'
import App from './App.vue'
import router from './router'
import VueECharts from 'vue-echarts'
import 'echarts/lib/chart/bar' // 引入柱状图

// css
import '@/assets/global.css'

// 引入element-ui
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

import AppHeader from "@/components/AppHeader.vue";
import AppAside from "@/components/AppAside.vue";
import AppMain from "@/components/AppMain.vue";
import auth from '@/utils/auth'; // 引入 auth.js
import SlideVerify from 'vue-monoplasty-slide-verify';// 滑块验证码



Vue.config.productionTip = false

Vue.use(ElementUI, { size: 'medium'});
Vue.use(SlideVerify); // 滑块验证码

Vue.directive(
    'loading', {
        inserted (el, binding) {
            binding.value ? el.classList.add('loading') : el.classList.remove('loading')
        },
        update (el, binding) {
            binding.value ? el.classList.add('loading') : el.classList.remove('loading')
        }
    }
)

Vue.component(
    "AppHeader", AppHeader
)
Vue.component(
    "AppAside", AppAside
)
Vue.component(
    "AppMain", AppMain
)

// 监听未授权事件（防重复跳转）
let toLogin = false;
auth.$on('unauthorized', () => {
    if (!toLogin && router.currentRoute.path !== '/login') {
        toLogin = true;
        router.push('/login');
    }
});
router.afterEach(() => {
    toLogin = false;
});

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')



