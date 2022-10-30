import {Form, Input, Select} from "antd";
import React from "react";

function EditableCell({
                        dataIndex,
                        editing,
                        title,
                        record,
                        allStatus,
                        allRoles,
                        index,
                        children,
                        ...restProps
                      }) {
  let editableField = null;

  switch (dataIndex) {
    case "roles":
      editableField = (
          <Form.Item
              name={dataIndex}
              style={{
                margin: 0,
              }}
              rules={[
                {
                  required: true,
                  message: `Please Input ${title}!`,
                }
              ]}
          >
            <Select mode="multiple" placeholder="Please select roles">
              {allRoles.map(({id, name}) => <Select.Option key={id} value={id}>{name}</Select.Option>)}
            </Select>
          </Form.Item>
      );
      break;
    case "status":
      editableField = (
          <Form.Item
              name={dataIndex}
              style={{
                margin: 0,
              }}
              rules={[
                {
                  required: true,
                  message: `Please Input ${title}!`,
                }
              ]}
          >
            <Select placeholder="Please select a status">
              {allStatus.map(status => <Select.Option key={status} value={status}>{status}</Select.Option>)}
            </Select>
          </Form.Item>
      );
      break;
    case "password":
      editableField = (
          <Form.Item
              name={dataIndex}
              style={{
                margin: 0,
              }}
          >
            <Input placeholder="New password"/>
          </Form.Item>
      )
      break;
    default:
      let rules = [{
        required: true,
        message: `Please Input ${title}!`,
      }]

      if (dataIndex === "email") {
        rules.push({
          type: "email",
          message: "Invalid email!"
        })
      }

      editableField = (
          <Form.Item
              name={dataIndex}
              style={{
                margin: 0,
              }}
              rules={rules}
          >
            <Input/>
          </Form.Item>
      );
  }

  return (
      <td {...restProps}>
        {editing ? editableField : children}
      </td>
  )
}

export default EditableCell;