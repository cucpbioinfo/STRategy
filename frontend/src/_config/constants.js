const {
  REACT_APP_BACKEND_BASE_URL: baseUrl,
  REACT_APP_BACKEND_PORT: port
} = process.env;

export const API_BASE_URL = `${baseUrl??"http://localhost"}:${port??8080}/api`;
export const ACCESS_TOKEN = "accessToken";
export const ROLE = "role";
export const ACCESS_TOKEN_FIELD_FROM_BACKEND = "token";
export const GUEST_ROLE = "GUEST_USER";
export const LAB_USER_ROLE = "ROLE_LAB_USER";
export const ADMIN_ROLE = "ROLE_ADMIN";
export const PATTERN_ALIGNMENT_COLOURS = [
  "#5BF13E",
  "#ffcf5a",
  "#0099da",
  "#3bffb8",
  "#d286ff",
  "#ffd500",
  "#00F5F5",
  "#ca2cff",
]