import React, {useEffect, useState} from 'react';
import addHeader from "../../hoc/addHeader";
import {Button, Checkbox, Col, Form, Radio, Row, Select, Spin} from "antd";
import axios from "../../../_config/axios";
import StringUtils from "../../../_utils/StringUtils";
import {useLocation} from "react-router-dom";

const {Option, OptGroup} = Select;

export function ExportAsExcel(props) {
  const [form] = Form.useForm();
  const location = useLocation();
  const [locusList, setLocusList] = useState({A: [], X: [], Y: []});
  const [aCheckbox, setACheckBox] = useState(false);
  const [xCheckbox, setXCheckBox] = useState(false);
  const [yCheckbox, setYCheckBox] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    axios
        .get("/loci/all")
        .then((res) => {
              setLocusList({
                A: res.data.aloci,
                X: res.data.xloci,
                Y: res.data.yloci,
              });
              const {loci} = StringUtils.queryParams(location.search)
              if (loci) {
                let locusList = loci.split(",").filter(e => e !== "");
                const resultLocus = [];
                for (let lc of locusList) {
                  if (res.data.aloci.includes(lc) || res.data.xloci.includes(lc) || res.data.yloci.includes(lc)) {
                    resultLocus.push(lc);
                  }
                }
                form.setFieldsValue({loci: resultLocus})
              }
            }
        )
        .catch((err) => {
          console.log(err);
        });
  }, []);

  const onFinish = (values) => {
    const {version, columns, loci} = values
    axios
        .post(`/samples/forenseq/export?version=${version}`,
            {loci, columns},
            {responseType: "blob"})
        .then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          let date = new Date();
          link.setAttribute("download", `Info sheet - ${date.toDateString()} ${date.toTimeString()}.xlsx`);
          document.body.appendChild(link);
          link.click();
          setIsLoading(false)
        });
    setIsLoading(true)
  }

  function validateLociToCheckbox(value) {
    const aLoci = locusList["A"];
    const xLoci = locusList["X"];
    const yLoci = locusList["Y"];
    let aChecked = true;
    let xChecked = true;
    let yChecked = true;

    for (let lc of aLoci) {
      if (!value.includes(lc)) {
        aChecked = false;
        break;
      }
    }

    for (let lc of xLoci) {
      if (!value.includes(lc)) {
        xChecked = false;
        break;
      }
    }

    for (let lc of yLoci) {
      if (!value.includes(lc)) {
        yChecked = false;
        break;
      }
    }

    setACheckBox(aChecked);
    setXCheckBox(xChecked);
    setYCheckBox(yChecked);
  }

  function onChangeACheckBox(e) {
    setACheckBox(e.target.checked)
    const loci = form.getFieldValue("loci") || [];
    const aLoci = locusList["A"];
    if (e.target.checked) {
      const result = [...loci];
      aLoci?.forEach(l => {
        if (!result.includes(l)) {
          result.push(l);
        }
      })
      form.setFieldsValue({loci: result})
    } else {
      const result = []
      loci?.forEach(l => {
        if (!aLoci.includes(l)) {
          result.push(l)
        }
      })
      form.setFieldsValue({loci: result})
    }
  }

  function onChangeXCheckBox(e) {
    setXCheckBox(e.target.checked)
    const loci = form.getFieldValue("loci") || [];
    const xLoci = locusList["X"];
    if (e.target.checked) {
      const result = [...loci];
      xLoci?.forEach(l => {
        if (!result.includes(l)) {
          result.push(l);
        }
      })
      form.setFieldsValue({loci: result})
    } else {
      const result = []
      loci?.forEach(l => {
        if (!xLoci.includes(l)) {
          result.push(l)
        }
      })
      form.setFieldsValue({loci: result})
    }
  }

  function onChangeYCheckBox(e) {
    setYCheckBox(e.target.checked)
    const loci = form.getFieldValue("loci") || [];
    const yLoci = locusList["Y"];
    if (e.target.checked) {
      const result = [...loci];
      yLoci?.forEach(l => {
        if (!result.includes(l)) {
          result.push(l);
        }
      })
      form.setFieldsValue({loci: result})
    } else {
      const result = []
      loci?.forEach(l => {
        if (!yLoci.includes(l)) {
          result.push(l)
        }
      })
      form.setFieldsValue({loci: result})
    }
  }

  return (
      <Row justify="center">
        <Col xs={23} sm={20} md={18} lg={15} xl={13} xxl={10}>
          <Spin spinning={isLoading} tip="This might take minutes...">
            <Form
                labelCol={{
                  xs: 24,
                  sm: 5,
                  md: 4,

                }}
                wrapperCol={{
                  xs: 24,
                  sm: 19,
                  md: 20
                }}
                form={form}
                initialValues={{
                  version: "XLSX",
                  columns: [
                    "sampleId",
                    "sampleYear",
                    "allele",
                    "STRRepeatMotifs",
                    "sequence"
                  ]
                }}
                onFinish={onFinish}
            >
              <Form.Item label="Excel version" name="version">
                <Radio.Group>
                  <Radio.Button value="XLS">Excel 97-2003</Radio.Button>
                  <Radio.Button value="XLSX">Excel 2007+</Radio.Button>
                </Radio.Group>
              </Form.Item>
              <Form.Item
                  name="columns"
                  label="Columns"
                  rules={[
                    {
                      required: true,
                      message: 'Please select export columns',
                      type: 'array',
                    },
                  ]}
              >
                <Select mode="multiple" placeholder="Please select favourite colors">
                  <Option value="sampleId">Sample ID</Option>
                  <Option value="sampleYear">Sample Year</Option>
                  <Option value="allele">Allele</Option>
                  <Option value="STRRepeatMotifs">STR repeat motifs</Option>
                  <Option value="sequence">Sequence</Option>
                </Select>
              </Form.Item>
              <Form.Item
                  label="Loci"
                  name="loci"
                  rules={[{
                    required: true,
                    message: 'Loci at least 1 locus is required'
                  }]}
              >
                <Select
                    mode="multiple"
                    placeholder="Select loci"
                    onChange={(value) => validateLociToCheckbox(value)}
                >
                  <OptGroup label="Autosome loci">
                    {locusList["A"].map(value => (
                        <Option key={value} value={value}>{value}</Option>
                    ))}
                  </OptGroup>
                  <OptGroup label="X loci">
                    {locusList["X"].map(value => (
                        <Option key={value} value={value}>{value}</Option>
                    ))}
                  </OptGroup>
                  <OptGroup label="Y loci">
                    {locusList["Y"].map(value => (
                        <Option key={value} value={value}>{value}</Option>
                    ))}
                  </OptGroup>
                </Select>
              </Form.Item>
              <Form.Item
                  label=" "
                  colon={false}
              >
                <Checkbox
                    checked={aCheckbox}
                    onChange={onChangeACheckBox}
                >
                  All Autosomal-STR loci
                </Checkbox>
                <Checkbox
                    checked={xCheckbox}
                    onChange={onChangeXCheckBox}
                >
                  All X-STR loci
                </Checkbox>
                <Checkbox
                    checked={yCheckbox}
                    onChange={onChangeYCheckBox}
                >
                  All Y-STR loci
                </Checkbox>
              </Form.Item>
              <Form.Item wrapperCol={{offset: {xs: 0, sm: 5, md: 4}, span: {xs: 24, sm: 19, md: 20}}}>
                <Button htmlType="submit" type="primary">Export</Button>
              </Form.Item>
            </Form>
          </Spin>
        </Col>
      </Row>
  )
}

export default addHeader(ExportAsExcel, "Export as Excel file");