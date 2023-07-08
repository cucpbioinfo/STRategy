import axios from 'axios';
import {API_BASE_URL} from "./constants"
import localStorageService from '../_services/LocalStorageService';
import {notification} from 'antd';

axios.defaults.baseURL = API_BASE_URL;

axios.interceptors.request.use(
    config => {
      if (config.url.includes("/signup") || config.url.includes("/signin")) return config;

      const token = localStorageService.getToken();

      if (token) {
        config.headers["Authorization"] = `Bearer ${token}`;
      }

      return config;
    },
    err => {
      Promise.reject(err);
    }
);

axios.interceptors.response.use(
    response => {
      return response;
    },
    err => {
      if (err.response?.status === 401 && !err?.response.config.url.includes("/auth/signin")) {
        localStorageService.removeToken();
        window.location.reload();
        notification.error({
          message: "กรุณาเข้าสู่ระบบใหม่"
        });

        return Promise.reject(err);
      }

      if (err.response?.status === 403) {
        notification.error({
          message: "คุณไม่มีสิทธิเข้าถึง กรุณาติดต่อแอดมิน"
        });
      }

      return Promise.reject(err);
    }
);

export default axios;