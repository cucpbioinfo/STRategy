import React, {useEffect, useState} from 'react';
import {login} from '../../../_services/APIService';
import {Link, withRouter} from 'react-router-dom';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {Button, Col, Descriptions, Form, Input, notification, Row, Tag} from 'antd';
import LocalStorageService from '../../../_services/LocalStorageService';
import {ACCESS_TOKEN_FIELD_FROM_BACKEND} from '../../../_config/constants';
import {dynamicPagesConfig} from "../../../_config/roles";
import axios from "../../../_config/axios";
import ColorUtils from "../../../_utils/ColorUtils";
import addHeader from "../../hoc/addHeader";

function MyProfile(props) {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [profile, setProfile] = useState({});

  useEffect(() => {
    axios.get("/auth/me").then(res => {
      setProfile(res.data)
    })
  }, [])

  const status = profile.status;

  return (
      <Row justify="center">
        <Col span={10}>
          <Descriptions
              contentStyle={{backgroundColor: "rgba(255, 255, 255, 0.2)"}}
              size="small"
              bordered
              title={false}
              column={1}
          >
            <Descriptions.Item label="UserName">{profile.username}</Descriptions.Item>
            <Descriptions.Item label="Email">{profile.email}</Descriptions.Item>
            <Descriptions.Item label="Status"> <Tag color={ColorUtils.resolveStatusColor(status)} key={status}>
              {status?.toUpperCase()}
            </Tag></Descriptions.Item>
            <Descriptions.Item label="Roles">{profile?.authorities?.map(({authority: role}) => (
                <Tag color={ColorUtils.resolveRoleColor(role)} key={role}>
                  {role?.toUpperCase()}
                </Tag>
            ))}</Descriptions.Item>
          </Descriptions>
        </Col>
      </Row>
  );
};

export default addHeader(MyProfile, "User Info");