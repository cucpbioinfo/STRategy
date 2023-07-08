import React, {useState} from "react";
import {Button, Input, List, Popconfirm, Typography} from "antd";

export function FieldEdit(props) {
  const {fieldId, fieldName, fieldDisplay, onUpdateField, onDeleteField, confirmMessage} = props;
  const [value, setValue] = useState("");
  const [isEdit, setIsEdit] = useState(false);

  return (
      <List.Item
          actions={[
            isEdit ? (
                <Button
                    key="edit"
                    onClick={(e) => {
                      setIsEdit(false);
                      onUpdateField(fieldId, value)
                    }}
                >Done</Button>
            ) : (
                <Typography.Link
                    key="edit"
                    onClick={(e) => {
                      setIsEdit(true);
                      setValue(fieldName);
                    }}
                >Edit</Typography.Link>
            ),
            isEdit ? (
                <Button
                    key="cancel"
                    onClick={(e) => {
                      setIsEdit(false);
                    }}
                >Cancel</Button>
            ) : (
                <Popconfirm
                    placement="topRight"
                    title={confirmMessage(fieldName)}
                    onConfirm={(e) => {
                      e.stopPropagation();
                      onDeleteField(fieldId)
                    }}
                    onCancel={(e) => e.stopPropagation()}
                    okText="Yes"
                    cancelText="No"
                >
                  <Typography.Link key="delete">Delete</Typography.Link>
                </Popconfirm>
            )
          ]}
      >
        {isEdit ? (
                <Input
                    value={value}
                    onChange={(e) => setValue(e.target.value)}
                />
            ) :
            <div>{fieldDisplay}: {fieldName}</div>}
      </List.Item>
  )
}

export default FieldEdit;