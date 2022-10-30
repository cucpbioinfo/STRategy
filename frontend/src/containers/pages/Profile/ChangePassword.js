import React, {useState} from 'react';
import {changePassword} from '../../../_services/APIService';
import "./ChangePassword.css"
import {LockOutlined} from '@ant-design/icons';
import {Button, Form, Input, notification} from 'antd';
import addHeader from "../../hoc/addHeader";

function ChangePassword(props) {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const onFinish = values => {
    const payload = {
      oldPassword: values["oldPassword"],
      newPassword: values["newPassword"],
    }
    setLoading(true);

    changePassword(payload)
        .then(result => {
          notification.success({
            message: "Your password have been changed.",
          })
          setLoading(false);
          form.resetFields();
        }).catch(error => {
      setLoading(false);
      notification.error({
        message: "Change password failed",
        description: error?.response?.data?.message || "Something went wrong."
      })
    })
  };

  return (
      <div className="change-pwd-container">
        <div>
          <Form
              name="login"
              className="change-pwd-container"
              onFinish={onFinish}
              form={form}
          >
            <Form.Item
                name="oldPassword"
                rules={[{required: true, message: 'Please input your old password!'}]}
            >
              <Input
                  prefix={<LockOutlined className="site-form-item-icon"/>}
                  type="password"
                  placeholder="Old Password"
              />
            </Form.Item>
            <Form.Item
                name="newPassword"
                rules={[{required: true, message: 'Please input your Password!'}]}
            >
              <Input
                  prefix={<LockOutlined className="site-form-item-icon"/>}
                  type="password"
                  placeholder="New Password"
              />
            </Form.Item>
            <Form.Item
                name="confirm-new-password"
                hasFeedback
                dependencies={['newPassword']}
                rules={[
                  {
                    required: true,
                    message: 'Please confirm your new password!',
                  },
                  ({getFieldValue}) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('newPassword') === value) {
                        return Promise.resolve();
                      }

                      return Promise.reject(new Error('The two passwords that you entered do not match!'));
                    },
                  }),
                ]}
            >
              <Input
                  prefix={<LockOutlined className="site-form-item-icon"/>}
                  type="password"
                  placeholder="Confirm New Password"
              />
            </Form.Item>
            <Form.Item>
              <Button loading={loading} type="primary" htmlType="submit" className="login-form-button">
                Change password
              </Button>
            </Form.Item>
          </Form>
        </div>
      </div>
  );
};

export default addHeader(ChangePassword, "Change Password");