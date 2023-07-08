import React, {useEffect, useState} from "react";
import addHeader from "../../hoc/addHeader";
import {Button, Col, Form, InputNumber, notification, Row} from "antd";
import axios from "../../../_config/axios";

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

export function EditCalculationProfileSearch(props) {
  const [form] = Form.useForm();

  useEffect(() => {
    fetchConfig();
  }, [])

  const fetchConfig = () => {
    axios.get("/configuration/keys?keys=profile_search_theta").then(res => {
      const config = res.data;
      form.setFieldValue("theta", Number(config["profile_search_theta"]))
    })
  }

  const onFinish = () => {
    let theta = form.getFieldValue("theta");
    const configs = [
      {
        configurationKey: "profile_search_theta",
        configurationValue: theta
      }
    ]
    axios
        .patch("/configuration/keys", configs)
        .then(res => {
          notification.success({
            message: "Configuration has been updated"
          })
        })
  }


  return (
      <Row justify="center">
        <Col xs={12} sm={10} md={6} lg={4}>
          <Form
              name="calculation"
              {...formItemLayout}
              form={form}
              onFinish={onFinish}
          >
            <Form.Item
                label="Theta"
                name="theta"
                rules={[
                  {
                    required: true,
                    message: "Theta cannot be blank."
                  }
                ]}
            >
              <InputNumber/>
            </Form.Item>
            <br/>
            <Row justify="space-around">
              <Col span={12}>
                <Button type="primary" htmlType="submit">
                  Save
                </Button>
              </Col>
              <Col span={12}>
                <Button onClick={fetchConfig} htmlType="button">
                  Reset
                </Button>
              </Col>
            </Row>
          </Form>
        </Col>
      </Row>
  )
}

export default addHeader(EditCalculationProfileSearch, "Constant for Calculating Homozygous Genotype");