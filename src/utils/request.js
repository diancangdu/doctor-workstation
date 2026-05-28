import axios from 'axios';
import auth from './auth';
import Cookies from "js-cookie";
import config from '@/../project-config.json';

const request = axios.create({
    baseURL: 'http://localhost:' + config.backendPort + '/api',
    timeout: 5000,
})

// 请求拦截器
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    const userJson = Cookies.get('user')
    if(userJson) {
        const user = JSON.parse(userJson);

        // 设置请求头
        if (user && user.token) {
            config.headers['token'] = user.token; // 设置token
        }

    }

    // config.headers['token'] = user.token; // 设置token
    return config;
}, error => {
    return Promise.reject(error);
});

// response拦截器
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        if (res.code === '401') {
            auth.handleUnauthorized();// 调用未授权处理逻辑
        }
        return res;
    }, error => {
        console.log('err' + error)
        return Promise.reject(error)
    }
)

export default request;
