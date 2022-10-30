import React, {useEffect, useState} from "react";
import addHeaders from "../../hoc/addHeader";
import {useHistory, useParams} from "react-router-dom";
import {useQuery} from "../../../hooks/ReactRouterHook";
import {Alert, Button, Col, Divider, Form, Input, Modal, notification, Row, Select, Upload} from "antd";
import {ExclamationCircleOutlined, PlusOutlined, UploadOutlined} from '@ant-design/icons';
import XLSX from "xlsx";
import axios from "../../../_config/axios";
import "./EditProvinces.css"

export function EditProvinces(props) {
  const {countryId} = useParams();
  let query = useQuery();
  const history = useHistory()
  const [form] = Form.useForm();
  const [file, setFile] = useState(null);
  const [country, setCountry] = useState("")
  const [originalId, setOriginalId] = useState([])
  const [regions, setRegions] = useState([])

  const fetchCountryData = () => {
    axios.get("/countries/" + countryId)
        .then(res => {
          setCountry(res.data.country)
          setRegions(res.data.regions)
        })
        .catch(err => {
          console.log(err)
        })
  }

  useEffect(() => {
    fetchCountryData();
    const ids = query.get("ids");
    if (ids) {
      axios.get("/provinces/?ids=" + ids)
          .then(res => {
            form.setFieldsValue({
              provinces: res.data
            })
            setOriginalId(res.data.map(e => e.id))
          })
    } else {
      form.setFieldsValue({
        provinces: []
      })
      setOriginalId([])
    }
  }, [])

  const onChangeCustom = (event) => {
    let reader = new FileReader();
    reader.onload = function (e) {
      try {
        const data = e.target.result;

        /* if binary string, read with type 'binary' */
        const workbook = XLSX.read(data, {type: 'binary'});
        /* DO SOMETHING WITH workbook HERE */
        const firstSheetName = workbook.SheetNames[0];

        /* Get worksheet */
        const worksheet = workbook.Sheets[firstSheetName];
        const provinceList = [...form.getFieldValue("provinces")]

        for (let i = 1; i < XLSX.utils.sheet_to_json(worksheet, {header: 1}).length; i++) {
          const row = XLSX.utils.sheet_to_json(worksheet, {header: 1})[i];
          const id = row[0]
          const province = row[1]
          const nativeName = row[2]
          const latitude = row[3]
          const longitude = row[4]
          const region = regions.find(e => e.region === row[5])
          if (id) {
            const prov = provinceList.find(p => p.id === id);
            if (!prov) {
              notification.error({
                message: `ID: ${id} not found in rows below`
              })
              return;
            }
            prov.province = province
            prov.nativeName = nativeName
            prov.latitude = latitude
            prov.longitude = longitude
            prov.region = region
          } else {
            provinceList.push({id, province, nativeName, latitude, longitude, region})
          }
        }

        form.setFieldsValue({provinces: provinceList})
      } catch (ex) {
        console.log(ex);
      }
    };

    reader.readAsBinaryString(file)
  }

  let uploadProps = {
    showUploadList: false,
    beforeUpload: file => {
      setFile(file);
      return false;
    },
    fileList: file === null ? [] : [file],
    onChange: onChangeCustom,
  };

  const downloadExample = () => {
    axios
        .get("/files/add-edit-provinces", {responseType: "blob"})
        .then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", "add-edit-provinces.xlsx");
          document.body.appendChild(link);
          link.click();
        });
  };

  const onFinish = values => {
    showConfirmSave(values)
  };

  function showConfirmCancel(values) {
    Modal.confirm({
      title: 'Do you want to discard all the changes?',
      icon: <ExclamationCircleOutlined/>,
      content: 'All operations wil not be saved.',
      onOk() {
        history.goBack()
      },
      onCancel() {
        console.log('Cancel');
      },
    });
  }

  function showConfirmSave(values) {
    Modal.confirm({
      title: 'This operation cannot be reversed. Would you like to save it?',
      icon: <ExclamationCircleOutlined/>,
      content: 'The deleted provinces in this page, as well as personal data belonging to the provinces, will be permanently erased.',
      onOk() {
        const removeIds = []
        const currentId = new Set(values.provinces.map(v => v.id));
        for (let value of originalId) {
          if (!currentId.has(Number(value))) {
            removeIds.push(Number(value))
          }
        }

        const data = {
          deleteProvinceIds: removeIds,
          provinceToUpdateList: values.provinces
        };

        axios.put(`/countries/${countryId}/provinces`, data)
            .then(res => {
              notification.success({
                message: "Update success"
              })
              history.push(`/admin/${countryId}/manage-provinces`)
            })
            .catch(err => {
              notification.error({
                message: "Update failed"
              })
            })
      },
      onCancel() {
        console.log('Cancel');
      },
    });
  }

  return (
      <div>
        <Row justify="center">
          <Col xs={23} sm={20} md={18} lg={16} xl={14} xxl={12}>
            <Alert
                style={{textAlign: "start", marginBottom: "1rem"}}
                message={`If you don't know how to set up an Excel, please see our example. 
                Making use of the "ID" column It would fail if ID was not found in the row below. 
                For new rows, leave the ID column blank.`}
                action={
                  <Button size="small" onClick={downloadExample}>
                    Example
                  </Button>
                }
                type="info"
                showIcon
                closable
            />
            <Alert
                style={{textAlign: "start", marginBottom: "1rem"}}
                message="Until you click the save button, no operations will be stored."
                type="info"
                showIcon
                closable
            />
          </Col>
        </Row>
        {country} (ID: {countryId})
        <Row justify="start" style={{margin: 16}}>
          <Upload {...uploadProps}>
            <span>
              <Button
                  icon={<UploadOutlined/>}>Select File</Button>
              <span
                  style={{
                    marginLeft: "10px",
                    color: "#1890ff"
                  }}>
                {file?.name?.length > 30 ? file?.name.slice(0, 30) + "..." : file?.name}
              </span>
            </span>
          </Upload>
          <Divider/>
          <Row justify="center" style={{width: "100%"}}>
            <Col span={23}>
              <Form form={form} name="dynamic_form_nest_item" onFinish={onFinish} autoComplete="off">
                <Form.List name="provinces">
                  {(fields, {add, remove}) => (
                      <>
                        {fields.map(({key, name, ...restField}) => {
                          return (
                              <Row key={key} justify="center" style={{width: "100%"}}>
                                <Col span={2}>
                                  <Form.Item
                                      {...restField}
                                      name={[name, 'id']}
                                  >
                                    <Input disabled placeholder="ID"/>
                                  </Form.Item>
                                </Col>
                                <Col span={4}>
                                  <Form.Item
                                      {...restField}
                                      name={[name, 'province']}
                                      rules={[{required: true, message: 'Missing province'}]}
                                  >
                                    <Input placeholder="Province"/>
                                  </Form.Item>
                                </Col>
                                <Col span={4}>
                                  <Form.Item
                                      {...restField}
                                      name={[name, 'nativeName']}
                                  >
                                    <Input placeholder="Native name"/>
                                  </Form.Item>
                                </Col>
                                <Col span={4}>
                                  <Form.Item
                                      {...restField}
                                      name={[name, 'latitude']}
                                  >
                                    <Input placeholder="Latitude"/>
                                  </Form.Item>
                                </Col>
                                <Col span={4}>
                                  <Form.Item
                                      {...restField}
                                      name={[name, 'longitude']}
                                  >
                                    <Input placeholder="Longitude"/>
                                  </Form.Item>
                                </Col>
                                <Col span={4}>
                                  <Form.Item
                                      {...restField}
                                      name={[name, 'region', 'id']}
                                  >
                                    <Select options={regions.map(r => ({
                                      label: r.region,
                                      value: r.id
                                    }))}/>
                                  </Form.Item>
                                </Col>
                                <Col span={2}>
                                  <Button type="danger" onClick={() => {
                                    remove(name)
                                  }}>Delete</Button>
                                </Col>
                              </Row>
                          );
                        })}
                        <Form.Item>
                          <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                            Add a province
                          </Button>
                        </Form.Item>
                      </>
                  )}
                </Form.List>
                <Form.Item>
                  <Button type="primary" htmlType="submit">
                    Save
                  </Button>
                  <span> </span>
                  <span> </span>
                  <Button onClick={showConfirmCancel} type="danger" htmlType="button">
                    Cancel
                  </Button>
                </Form.Item>
              </Form>
            </Col>
          </Row>
        </Row>
      </div>
  )
}

export default addHeaders(EditProvinces, "Add/Edit Provinces");
