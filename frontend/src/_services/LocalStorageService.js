import {ACCESS_TOKEN, ADMIN_ROLE, GUEST_ROLE, LAB_USER_ROLE, ROLE} from "../_config/constants";

const _setToken = (accessToken) => {
  localStorage.setItem(ACCESS_TOKEN, accessToken);
};

const _getToken = () => {
  return localStorage.getItem(ACCESS_TOKEN);
};

const _removeToken = () => {
  localStorage.removeItem(ACCESS_TOKEN);
  localStorage.removeItem(ROLE);
};

const _setRole = (roles) => {
  if (roles.includes(ADMIN_ROLE)) {
    localStorage.setItem(ROLE, ADMIN_ROLE);
  } else {
    localStorage.setItem(ROLE, LAB_USER_ROLE);
  }
};

const _getRole = () => {
  return localStorage.getItem(ROLE) ?? GUEST_ROLE;
};

const serviceFn = {
  setToken: _setToken,
  getToken: _getToken,
  removeToken: _removeToken,
  setRole: _setRole,
  getRole: _getRole,
};

export default serviceFn;
