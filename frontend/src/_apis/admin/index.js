import axios from "../../_config/axios";
import StringUtils from "../../_utils/StringUtils";

const {resolveSorter, resolveFilter} = StringUtils;

export const fetchAllUser = async ({queryKey: [_, {size, page}, status, sorterObj, filterObj]}) => {
  let url = `/admins/users?size=${size}&page=${page}`;
  url = resolveSorter(sorterObj, url);
  url = resolveFilter(filterObj, url);
  const res = await axios.get(url);
  return res.data;
}

export const fetchAllStatus = async () => {
  const res = await axios.get("/admins/statuses");
  return res.data;
}

export const fetchAllRoles = async () => {
  const res = await axios.get("/admins/roles");
  return res.data;
}

export const fetchAllKits = async ({queryKey: [_, chromosome]}) => {
  const res = await axios.get(`/admins/kits?chromosome=${chromosome}`);
  return res.data;
}