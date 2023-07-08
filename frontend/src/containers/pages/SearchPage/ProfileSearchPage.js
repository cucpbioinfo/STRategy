import React, {useEffect, useState} from 'react';
import {Alert, Button, Col, Collapse, Divider, Form, notification, Row, Select, Table, Upload} from "antd";
import axios from "../../../_config/axios";
import Radio from "antd/es/radio/radio";
import CoreLociField from "./CoreLociField";
import {UploadOutlined} from "@ant-design/icons";
import XLSX from "xlsx";
import NumberUtils from "../../../_utils/NumberUtils";

const {Option} = Select;
const {Panel} = Collapse;

export function ProfileSearchPage(props) {
  const [form] = Form.useForm();
  const [countries, setCountries] = useState([]);
  const [curCountry, setCurCountry] = useState("");
  const [curCoreLoci, setCurCoreLoci] = useState([]);
  const [activeKey, setActiveKey] = useState(["0"])
  const [result, setResult] = useState([]);
  const [mode, setMode] = useState(0);
  const [file, setFile] = useState(null);
  const [isDisabledFields, setIsDisabledFields] = useState(false);

  useEffect(() => {
    form.resetFields();
    setFile(null);
    if (mode === 1) {
      setIsDisabledFields(true)
    } else {
      setIsDisabledFields(false)
    }
  }, [mode])

  const onFinish = (values) => {
    let newValues = Object.entries(values)
        .filter(([key, values]) => values && values.allele1 && values.allele1)
        .map(([key, values]) => {
          return {
            locus: key,
            allele1: values.allele1,
            allele2: values.allele2,
          }
        });

    axios.post("/profile-search/search", newValues).then(res => {
      let sortedData = res.data.sort((a, b) => a.summaryValue - b.summaryValue);
      setResult(sortedData);
      setActiveKey(prevState => {
        if (prevState.includes("2")) {
          return prevState
        } else {
          return [...prevState, "2"]
        }
      })
    })
  };

  useEffect(() => {
    axios.get("/core-loci/countries").then(res => {
      setCountries(res.data.filter(e => e !== "default"));
    })
  }, [])

  useEffect(() => {
    if (countries.includes(curCountry)) {
      axios.get(`/core-loci/?country=${curCountry}`).then(res => {
        setCurCoreLoci(res.data);
        setActiveKey(["0", "1"])
      })
    }
  }, [curCountry])

  const columns = [
    {
      title: 'Locus',
      dataIndex: 'locus',
    },
    {
      title: 'p',
      dataIndex: 'pvalue',
    },
    {
      title: 'q',
      dataIndex: 'qvalue',
    },
    {
      title: 'value',
      dataIndex: 'finalValue',
      render: (value, record) => (
          <div>
            {record.finalValue.toFixed(3)}
          </div>
      )
    },
    {
      title: '1/value',
      dataIndex: 'inverseFinalValue',
    },
  ];

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
        const coreLoci = curCoreLoci.map(ccl => ccl.locus);
        const fields = {};

        for (const row of XLSX.utils.sheet_to_json(worksheet, {header: 1})) {
          if (coreLoci.includes(row[0]) && typeof row[1] === "string") {
            const split = row[1].split(",");
            if (split.length > 1) {
              fields[row[0]] = {allele1: split[0], allele2: split[1]}
            }
          }
        }

        form.setFieldsValue(fields)
        setIsDisabledFields(false)

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
        .get("/files/example-excel", {responseType: "blob"})
        .then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", "sample.xlsx");
          document.body.appendChild(link);
          link.click();
        });
  };

  const checkValidAlleleRequired = (_, value) => {
    if ((NumberUtils.isNumeric(value.allele1) && NumberUtils.isNumeric(value.allele2))) {
      return Promise.resolve();
    }

    if (!value.allele1 || !value.allele2) {
      return Promise.reject(new Error('Alleles are required'));
    }

    return Promise.reject(new Error('Alleles must be number!'));
  };

  const checkValidAlleleOptional = (_, value) => {
    if (value.allele1 === "" && value.allele2 === "") {
      return Promise.resolve();
    }

    if ((NumberUtils.isNumeric(value.allele1) && NumberUtils.isNumeric(value.allele2))) {
      return Promise.resolve();
    }

    return Promise.reject(new Error('Alleles must be number!'));
  }

  const onFinishFailed = (errorInfo) => {
    const errMessage = (errorInfo?.errorFields && errorInfo?.errorFields.length > 0 && errorInfo?.errorFields[0].errors[0]) || "Something went wrong";
    const nameErrMessage = (errorInfo?.errorFields && errorInfo?.errorFields.length > 0 && errorInfo?.errorFields[0].name[0]) || "Something went wrong";
    notification.warn({
      message: `${nameErrMessage}: ${errMessage}`
    })
  }

  return (
      <Row justify="center">
        <Col xs={23} sm={20} md={16} lg={12} xl={10} xxl={8}>
          {mode === 1 ? <Alert
              style={{textAlign: "start", marginBottom: "1rem"}}
              message="If you don't know how to set up an excel file for fill fields by a file, please see our example"
              action={
                <Button size="small" onClick={downloadExample}>
                  Example
                </Button>
              }
              type="info"
              showIcon
              closable
          /> : null}
          <Collapse
              activeKey={activeKey}
              style={{width: "100%"}}
              onChange={(e) => {
                setActiveKey(e);
              }}
          >
            <Panel header="Select Core Loci" key="0">
              <Row justify="center">
                <Select defaultValue="Select Core Loci" style={{width: 120}} onChange={(e) => setCurCountry(e)}>
                  {countries.map(c => <Option key={c} value={c}>{c}</Option>)}
                </Select>
              </Row>
            </Panel>
            <Panel header="Core Loci List" key="1">
              {countries.includes(curCountry) ? (
                  <>
                    <Row justify="space-between">
                      <Col>
                        <div style={{justify: "center", margin: "auto 0"}}>
                          {mode === 1 ? (
                              <>
                                <Row justify="center">
                                  <Upload {...uploadProps}>
                                <span>
                                  <Button
                                      icon={<UploadOutlined/>}>Select File</Button>
                                  <span
                                      style={{
                                        marginLeft: "10px",
                                        color: "#1890ff"
                                      }}
                                  >
                                    {file?.name?.length > 30 ? file?.name.slice(0, 30) + "..." : file?.name}
                                  </span>
                                </span>
                                  </Upload>
                                </Row>
                              </>
                          ) : null}
                        </div>
                      </Col>
                      <Col>
                        <Radio.Group
                            options={[
                              {label: 'Fields', value: 0},
                              {label: 'File', value: 1},
                            ]}
                            onChange={(e) => setMode(e.target.value)}
                            value={mode}
                            optionType="button"
                            buttonStyle="solid"
                        />
                      </Col>
                    </Row>
                    <Divider/>
                    <Row justify="center">
                      <Form
                          onFinishFailed={onFinishFailed}
                          scrollToFirstError={true}
                          form={form}
                          labelAlign="left"
                          colon={false}
                          labelCol={{
                            xs: 24,
                            sm: 5,
                          }}
                          wrapperCol={{
                            xs: 24,
                            sm: 19,
                          }}
                          onFinish={onFinish}
                      >
                        {curCoreLoci.map(coreLocus => (
                            <Form.Item
                                key={coreLocus.id}
                                name={coreLocus.locus}
                                label={coreLocus.locus}
                                rules={[
                                  {
                                    required: coreLocus.required,
                                    message: `${coreLocus.locus} is required`
                                  },
                                  {
                                    validator: coreLocus.required ? checkValidAlleleRequired : checkValidAlleleOptional,
                                  }
                                ]}
                            >
                              <CoreLociField disabled={isDisabledFields}/>
                            </Form.Item>
                        ))}
                        <Button htmlType="submit">Submit</Button>
                      </Form>
                    </Row>
                  < />
              ) : (
                  <div>
                    Please select the core loci first
                  </div>
              )}
            </Panel>
            <Panel header="Summary result" key="2">
              <Row>
                <p>Populations Sorted by Frequency (Most Common to Least Common)</p>
              </Row>
              <Row>
                <Col span={24}>
                  <Table
                      rowKey="country"
                      size="small"
                      columns={[
                        {
                          title: 'Country',
                          dataIndex: 'country',
                        },
                        {
                          title: 'Frequency',
                          dataIndex: 'summaryValue',
                          render: (value, record) => (
                              <div>
                                {record.summaryValue.toExponential(2)}
                              </div>
                          )
                        },
                      ]}
                      dataSource={result}
                  />
                </Col>
              </Row>
            </Panel>
            <Panel header="Each country result" key="3">
              <Row>
                {result.map(e => {
                  return (
                      <Col key={e.country} span={24}>
                        <Table
                            rowKey="locus"
                            title={() => `${e.country} (${e.summaryValue.toExponential(2)})`}
                            size="small"
                            columns={columns}
                            dataSource={e.alleleList}
                            pagination={false}
                            bordered
                            style={{marginBottom: "10px"}}
                        />
                      </Col>
                  );
                })}
              </Row>
            </Panel>
          </Collapse>
        </Col>
      </Row>
  )
}

export default ProfileSearchPage;