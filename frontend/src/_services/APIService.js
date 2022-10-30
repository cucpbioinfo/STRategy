import {ACCESS_TOKEN} from '../_config/constants';
import axios from '../_config/axios';


export function login(loginRequest) {
  return axios.post("/auth/signin", loginRequest);
}

export function signup(signupRequest) {
  return axios.post("/auth/signup", signupRequest);
}

export function changePassword(changePwdRequest) {
  return axios.post("/auth/change-pwd", changePwdRequest);
}

export function getCurrentUser() {
  if (!localStorage.getItem(ACCESS_TOKEN)) {
    return Promise.reject("No access token set.");
  }

  return axios.get("/user/me");
}