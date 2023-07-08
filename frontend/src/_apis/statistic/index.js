import axios from "../../_config/axios";
import {API_BASE_URL} from "../../_config/constants";

export const fetchLociData = async () => {
  const res = await axios.get("/loci/all");
  return {
    Autosome: res.data.aloci,
    X: res.data.xloci,
    Y: res.data.yloci,
  }
}

export const fetchDefaultMapData = async () => {
  const res = await axios.get("/configuration/keys?keys=default_center_map_longitude," +
      "default_center_map_latitude,default_map_scale," +
      "is_external_map_url,external_statistic_map_url," +
      "internal_statistic_map_url")
      console.log(res.data)
  return res.data;
}

export const fetchMapData = async ({queryKey: [_, locus]}) => {
  const res = await axios.get(`/forenseq-sequences/map?locus=${locus}`)
  return res.data;
};

export const fetchGeography = async ({queryKey: [_, defaultMap]}) => {
  const {
    external_statistic_map_url: externalUrl,
    is_external_map_url: isExternal,
    internal_statistic_map_url: internalUrl,
  } = defaultMap;

  const mapResourceUrl = isExternal === "true" ? externalUrl : `${API_BASE_URL}${internalUrl}`;

  console.log(defaultMap);
  const res = await fetch(mapResourceUrl);
  return await res.json();
}