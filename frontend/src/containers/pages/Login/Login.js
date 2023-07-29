import React, {useState} from 'react';
import {login} from '../../../_services/APIService';
import './Login.css';
import {Link, withRouter} from 'react-router-dom';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {Button, Form, Input, notification} from 'antd';
import LocalStorageService from '../../../_services/LocalStorageService';
import {ACCESS_TOKEN_FIELD_FROM_BACKEND} from '../../../_config/constants';
import {dynamicPagesConfig} from "../../../_config/roles";

function Login(props) {
  const [loading, setLoading] = useState(false);

  const onFinish = values => {
    const payload = {
      username: values.username,
      password: values.password,
    }
    setLoading(true);

    login(payload)
        .then(result => {
          notification.success({
            message: "Login successful.",
          })
          LocalStorageService.setToken(result.data[ACCESS_TOKEN_FIELD_FROM_BACKEND]);
          LocalStorageService.setRole(result.data.roles)
          setLoading(false);
          props.setRole(LocalStorageService.getRole());
          props.setCurrentMenu(dynamicPagesConfig.home.url)
          props.history.push(dynamicPagesConfig.home.url)
        }).catch(error => {
      setLoading(false);
      notification.error({
        message: "Login failed.",
        description: error?.response?.data?.message || "Something went wrong."
      })
    })
  };

  return (
      <div className="signup-container">
        <h1>Login</h1>
        <div className="signup-content">
          <Form
              name="login"
              className="login-container"
              onFinish={onFinish}
          >
            <Form.Item
                name="username"
                rules={[{required: true, message: 'Please input your Username!'}]}
            >
              <Input prefix={<UserOutlined className="site-form-item-icon"/>} placeholder="Username"/>
            </Form.Item>
            <Form.Item
                name="password"
                rules={[{required: true, message: 'Please input your Password!'}]}
            >
              <Input
                  prefix={<LockOutlined className="site-form-item-icon"/>}
                  type="password"
                  placeholder="Password"
              />
            </Form.Item>

            <Form.Item>
              <Button loading={loading} type="primary" htmlType="submit" className="login-form-button">
                Log in
              </Button>
              <br/>
              <br/>
              <Link to="/signup">
                <Button type="dashed" className="login-form-button">
                  Register
                </Button>
              </Link>
            </Form.Item>
          </Form>
        </div>
      </div>
  );
};

export default withRouter(Login);
