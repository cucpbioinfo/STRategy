import {Button, Col, Form, Row, Select} from "antd";
import React, {useEffect, useState} from "react";
import {useParams, withRouter} from "react-router-dom";
import axios from "../../../_config/axios";

const {Option} = Select;

const formItemLayout = {
  labelCol: {
    xs: 24,
    sm: 8,
    md: 6,
    lg: 8,
  },
  wrapperCol: {
    xs: 24,
    sm: 16,
    md: 18,
    lg: 20,
  },
};

function PersonEditForm(props) {
  const {id} = useParams();
  const [form] = Form.useForm();

  const [provinceId, setProvinceId] = useState(0);
  const [provinceList, setProvinceList] = useState([]);
  const [raceId, setRaceId] = useState(0);
  const [raceList, setRaceList] = useState([]);
  const [countryId, setCountryId] = useState(0);
  const [countryList, setCountryList] = useState([]);
  const [regionId, setRegionId] = useState(0);
  const [regionList, setRegionList] = useState([]);

  useEffect(() => {
    axios.get(`/persons/${id}`).then((res) => {
      const {
        gender,
        race: raceObj = {},
        country: countryObj,
        region: regionObj,
        province: provinceObj,
      } = res.data;
      const {id: raceId, race} = raceObj || {};
      const {id: countryId, country} = countryObj || {};
      const {id: regionId, region} = regionObj || {};
      const {id: provinceId, province} = provinceObj || {};
      form.setFieldsValue({
        gender,
        race,
        country,
        region,
        province,
      });
      setCountryId(countryId);
      setProvinceId(provinceId);
      setRaceId(raceId);
      setRegionId(regionId);
    });
    axios.get("/countries/").then((res) => {
      setCountryList(res.data);
    });
    axios.get("/v2/races/").then((res) => {
      setRaceList(res.data);
    });
  }, [form, id]);

  useEffect(() => {
    axios.get(`/regions/?country_id=${countryId}`).then((res) => {
      setRegionList(res.data);
    });
  }, [countryId, form]);

  useEffect(() => {
    axios.get(`/regions/${regionId}/provinces/`).then((res) => {
      setProvinceList(res.data);
    });
  }, [form, regionId]);

  const onFinish = ({gender}) => {
    const updatePerson = {
      gender,
      race_id: raceId,
      country_id: countryId,
      province_id: provinceId,
      region_id: regionId,
    };
    axios.put(`/persons/${id}`, updatePerson).then((res) => {
      props.history.push("/persons");
    });
  };

  return (
      <>
        <Row align="center" style={{marginTop: "20px"}}>
          <h2>Person Edit ID: {id}</h2>
        </Row>
        <br/>
        <Row justify="center">
          <Col xs={20} sm={16} md={12} lg={6}>
            <Form onFinish={onFinish} {...formItemLayout} form={form} name="edit-form">
              <Form.Item name="gender" label="Gender" rules={[{required: true}]}>
                <Select placeholder="Select gender">
                  <Option value="MALE">MALE</Option>
                  <Option value="FEMALE">FEMALE</Option>
                </Select>
              </Form.Item>
              <Form.Item name="country" label="Country" rules={[{required: true, message: "Please select country"}]}>
                <Select placeholder="Please select country" onChange={(id) => setCountryId(id)}>
                  {countryList.map(({id, country}) => (
                      <Option value={id}>{country}</Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="region" label="Region" rules={[{required: true, message: "Please select region"}]}>
                <Select placeholder="Please select region" onChange={(id) => setRegionId(id)}>
                  {regionList.map(({id, region}) => (
                      <Option value={id}>{region}</Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="province" label="Province" rules={[{required: true, message: "Please select province"}]}>
                <Select placeholder="Please select province" onChange={(id) => setProvinceId(id)}>
                  {provinceList.map(({id, province}) => (
                      <Option value={id}>{province}</Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="race" label="Race" rules={[{required: true, message: "Please select race"}]}>
                <Select placeholder="Please select race" onChange={(id) => setRaceId(id)}>
                  {raceList.map(({id, race}) => (
                      <Option value={id}>{race}</Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit">
                  Update
                </Button>
              </Form.Item>
            </Form>
          </Col>
        </Row>
      </>
  );
}

export default withRouter(PersonEditForm);
