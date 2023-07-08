import axios from "../../_config/axios";
import LocalStorageService from "../../_services/LocalStorageService";

export const checkUserStatus = async () => {
  if (LocalStorageService.getToken()) {
    const res = await axios.get("/auth/status")
    return res.data;
  }
  return false;
}