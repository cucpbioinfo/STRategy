import React, {useEffect, useState} from 'react'
import addHeaders from "../../hoc/addHeader";
import {Alert, Button, Col, Form, Input, InputNumber, notification, Row, Select, Spin, Upload} from "antd";
import {useQuery} from "react-query";
import {StatisticApi} from "../../../_apis";
import {ComposableMap, Geographies, Geography} from "react-simple-maps";
import {API_BASE_URL} from "../../../_config/constants";
import {feature} from 'topojson-client';
import {CloudUploadOutlined} from "@ant-design/icons";
import axios from "axios";

const {fetchDefaultMapData, fetchGeography} = StatisticApi;

export function ManageMap() {
  const [form] = Form.useForm();
  const [mapUrl, setMapUrl] = useState("");
  const [external, setExternal] = useState(false);
  const [mapFile, setMapFile] = useState(null);
  const [mapData, setMapData] = useState([]);
  const [latitudeField, setLatitudeField] = useState("");
  const [longitudeField, setLongitudeField] = useState("");
  const [scaleField, setScaleField] = useState("");
  const [curLongitude, setCurLongitude] = useState(0);
  const [curLatitude, setCurLatitude] = useState(0);
  const [curScale, setCurScale] = useState(0);
  const {isFetching: defaultMapLoading, data: defaultMap} = useQuery('defaultMap', fetchDefaultMapData, {
    initialData: {
      "default_center_map_scale": "2000",
      "default_center_map_longitude": "100",
      "default_center_map_latitude": "12.8",
      "external_statistic_map_url": "",
      "is_external_map_url": "false",
      "internal_statistic_map_url": "/admins/maps",
    }
  });

  const {data: geographyMapData} = useQuery(["fetchGeography", mapUrl], fetchGeography, {
    initialData: [],
    keepPreviousData: true,
  })

  const {
    default_center_map_longitude: longitude,
    default_center_map_latitude: latitude,
    default_map_scale: scale,
    external_statistic_map_url: externalUrl,
    is_external_map_url: isExternal,
    internal_statistic_map_url: internalUrl,
  } = defaultMap;

  const layout = {
    labelCol: {
      span: 8,
    },
    wrapperCol: {
      span: 16,
    },
  };

  const onFinish = (values) => {
    const {
      externalUrl,
      isExternal,
      latitude,
      longitude,
      scale,
      mapFile,
    } = values;

    const requestBody = [
      {
        "configurationKey": "default_center_map_latitude",
        "configurationValue": latitude
      },
      {
        "configurationKey": "default_center_map_longitude",
        "configurationValue": longitude
      },
      {
        "configurationKey": "default_map_scale",
        "configurationValue": scale
      },
    ]

    if (isExternal) {
      requestBody.push(
          {
            "configurationKey": "is_external_map_url",
            "configurationValue": "true"
          },
          {
            "configurationKey": "external_statistic_map_url",
            "configurationValue": externalUrl
          }
      )
    } else {
      requestBody.push({
        "configurationKey": "is_external_map_url",
        "configurationValue": "false"
      })

      if (mapFile) {
        const data = new FormData();
        data.append("map", mapFile)

        axios.post("/admins/maps/upload", data)
            .then(res => {
            })
            .catch(err => {
              console.log(err);
            })
      }
    }

    axios.patch("/configuration/keys", requestBody)
        .then(res => {
          notification.success({
            message: "Configuration is updated"
          })
        })
        .catch(err => {
          console.error(err);
          notification.error({
            message: "Update is not success"
          })
        })
  };

  useEffect(() => {
    const id = setTimeout(() => {
      setCurLatitude(latitudeField);
    }, 1000)
    return () => {
      clearTimeout(id)
    }
  }, [latitudeField])

  useEffect(() => {
    const id = setTimeout(() => {
      setCurLongitude(longitudeField);
    }, 1000)
    return () => {
      clearTimeout(id)
    }
  }, [longitudeField])

  useEffect(() => {
    const id = setTimeout(() => {
      setCurScale(scaleField);
    }, 1000)
    return () => {
      clearTimeout(id)
    }
  }, [scaleField])

  useEffect(() => {
    let externalValue = isExternal === "true";
    const mapResourceUrl = externalValue ? externalUrl : `${API_BASE_URL}${internalUrl}`;
    form.setFieldsValue({
      isExternal: externalValue,
      longitude: Number(longitude),
      latitude: Number(latitude),
      externalUrl,
      scale,
    })
    setLongitudeField(Number(longitude));
    setLatitudeField(Number(latitude));
    setScaleField(scale)
    setMapData(geographyMapData)
    setExternal(externalValue);
    setMapUrl(mapResourceUrl);
  }, [externalUrl, scale, internalUrl, form, latitude, longitude, isExternal, geographyMapData])

  useEffect(() => {
    setMapData(geographyMapData);
  }, [geographyMapData])

  let onClickPreviewMap = () => {
    if (external) {
      if (form.getFieldValue("externalUrl")) {
        notification.info({
          message: "Previewing map from external url. Please wait..."
        })
        const eUrl = form.getFieldValue("externalUrl")
        fetch(eUrl).then(res => res.json()).then(data => {
          console.log(data)
          setMapData(data);
        });
      } else {
        notification.warn({
          message: "The external url is empty."
        })
      }
    } else {
      if (form.getFieldValue("mapFile")) {
        console.log(form.getFieldValue("mapFile"))
        notification.info({
          message: "Convert the map file. Please wait..."
        })
        try {
          const reader = new FileReader()
          reader.onload = async (e) => {
            try {
              const parseObj = JSON.parse(e.target.result)
              console.log(parseObj);
              setMapData(parseObj);
            } catch (e) {
              console.error(e);
              notification.error({
                message: "Map file format is wrong. Please upload again."
              })
            }
          };

          reader.readAsText(form.getFieldValue("mapFile"))
        } catch (e) {
          console.error(e);

          notification.error({
            message: "Map file format is wrong. Please upload again."
          })
        }
      } else {
        notification.warn({
          message: "Map file is empty, you have to upload a map file before preview it."
        })
      }
    }
  };
  return (
      <Col span={23}>
        <Row align="middle" justify="center">
          <Alert
              message="Map configuration description"
              description="Manage map which is abort locus and allele frequency group by geography."
              type="info"
              showIcon
              style={{marginBottom: "1rem"}}
              closable
          />
          <Col span={18} style={{border: "1px solid black"}}>
            <Spin tip="Loading map data..." spinning={defaultMapLoading}>
              <ComposableMap
                  projection="geoMercator"
                  projectionConfig={{
                    center: [curLongitude, curLatitude],
                    scale: curScale,
                  }}
              >
                <Geographies
                    geography="https://raw.githubusercontent.com/soncomqiq/map-topo-json/main/maps.json"
                    parseGeographies={(geo) => {
                      if (mapData.objects) {
                        return feature(mapData, mapData.objects[Object.keys(mapData.objects)[0]]).features;
                      }
                      return geo;
                    }}>
                  {({geographies}) =>
                      geographies.map((geo) => (
                          <Geography
                              style={{
                                default: {
                                  fill: "#ECEFF1",
                                  stroke: "#607D8B",
                                  strokeWidth: 0.75,
                                  outline: "none",
                                },
                                hover: {
                                  fill: "#FFFFFF",
                                  stroke: "#607D8B",
                                  strokeWidth: 0.75,
                                  outline: "none",
                                },
                                pressed: {
                                  fill: "#000000",
                                  stroke: "#607D8B",
                                  strokeWidth: 0.75,
                                  outline: "none",
                                },
                              }}
                              key={geo.rsmKey}
                              geography={geo}
                              fill="#EAEAEC"
                              stroke="#D6D6DA"
                          />
                      ))
                  }
                </Geographies>
              </ComposableMap>
            </Spin>
          </Col>
        </Row>
        <Row style={{marginTop: "20px"}} align="middle" justify="center">
          <Col span={12}>
            <Form
                onFinish={onFinish}
                name="map-configuration"
                {...layout}
                form={form}
                style={{marginBottom: "1rem"}}
            >
              {external ? (
                  <Alert
                      message="External url notes"
                      description={(
                          <div style={{textAlign: "start"}}>
                            In order to create your map you will first need a valid topojson/geojson file. We have <a
                              href="https://github.com/zcreativelabs/react-simple-maps/tree/master/topojson-maps"
                              target="_blank"
                              without rel="noreferrer">a few topojson files</a> (based on <a
                              href="https://www.naturalearthdata.com/downloads/"
                              target="_blank"
                              without rel="noreferrer">Natural Earth data</a>) that you can
                            experiment with in this map.
                          </div>
                      )}
                      type="info"
                      showIcon
                      closable
                  />
              ) : (
                  <Alert
                      message="File map notes"
                      description={(
                          <div style={{textAlign: "start"}}>
                            If you are interested in how to create custom topojson files based on shapefile data,
                            check out this guide on <a
                              href="https://medium.com/hackernoon/how-to-convert-and-prepare-topojson-files-for-interactive-mapping-with-d3-499cf0ced5f"
                              target="_blank"
                              without rel="noreferrer">how to convert shapefiles to topojson/geojson with
                            mapshaper.org</a>.
                          </div>
                      )} type="info"
                      style={{marginBottom: "1rem"}}
                      showIcon
                      closable
                  />
              )}
              <Form.Item
                  name="isExternal"
                  label="Map-url type"
                  rules={[
                    {
                      required: true,
                    },
                  ]}
              >
                <Select onChange={(value) => setExternal(value)} placeholder="Please select type of url">
                  <Select.Option value={true}>External URL</Select.Option>
                  <Select.Option value={false}>Upload</Select.Option>
                </Select>
              </Form.Item>
              {external ? (
                  <Form.Item
                      name="externalUrl"
                      label="External url"
                      rules={[
                        {
                          required: true,
                        },
                      ]}
                  >
                    <Input/>
                  </Form.Item>
              ) : (
                  <Form.Item
                      name="mapFile"
                      label="Map file"
                      getValueFromEvent={e => e.file}
                  >
                    <Upload
                        onRemove={file => setMapFile(null)}
                        beforeUpload={file => {
                          setMapFile(file)
                          return false
                        }}
                        fileList={mapFile ? [mapFile] : []}
                    >
                      <Button icon={<CloudUploadOutlined/>}>Select File</Button>
                    </Upload>
                  </Form.Item>
              )}
              <Form.Item
                  wrapperCol={{
                    span: 24,
                  }}
              >
                <Button
                    type="primary"
                    onClick={onClickPreviewMap}
                >
                  Preview Map
                </Button>
              </Form.Item>
              <Form.Item
                  name="longitude"
                  label="Default longitude center"
                  rules={[
                    {
                      required: true,
                    },
                    {
                      type: 'number',
                      min: -180,
                      max: 180,
                      message: "Longitude must be between -180 and 180 (Inclusive)",
                    }
                  ]}
              >
                <InputNumber max={180} min={-180} onChange={(num) => setLongitudeField(num)}/>
              </Form.Item>
              <Form.Item
                  name="latitude"
                  label="Default latitude center"
                  rules={[
                    {
                      required: true,
                    },
                    {
                      type: 'number',
                      min: -90,
                      max: 90,
                      message: "Latitude must be between -90 and 90 (Inclusive)",
                    }
                  ]}
              >
                <InputNumber max={90} min={-90} onChange={(num) => setLatitudeField(num)}/>
              </Form.Item>
              <Alert
                  message="Scale description"
                  description={(
                      <div style={{textAlign: "start"}}>
                        A minimum scale value is 1, displaying a full world map. And If you want to zoom 2000 times,
                        you can specific the scale to 2000
                      </div>
                  )}
                  style={{marginBottom: "1rem"}}
                  type="info"
                  showIcon
                  closable
              />
              <Form.Item
                  name="scale"
                  label="Default scale"
                  rules={[
                    {
                      required: true,
                    },
                  ]}
              >
                <InputNumber onChange={(num) => setScaleField(num)}/>
              </Form.Item>
              <Button htmlType="submit" style={{marginTop: "10px"}} type="primary">Save</Button>
            </Form>
          </Col>
        </Row>
      </Col>
  )
}

export default addHeaders(ManageMap, "Map Configuration");
