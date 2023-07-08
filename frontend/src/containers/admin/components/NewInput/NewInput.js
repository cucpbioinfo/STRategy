import React, {useState} from "react"
import {Button, Col, Input, Row} from "antd";

export function NewInput(props) {
  const {childName, parentId, parentName, onAddInput} = props;
  const [inputValue, setInputValue] = useState("");

  return (
      <Row justify="center" align="middle">
        <Col span={18}>
          <Input
              placeholder={`New ${childName} for ${parentName}`}
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}/>
        </Col>
        <Col span={6}>
          <Button style={{width: "100%"}} onClick={() => {
            onAddInput(parentId, inputValue);
            setInputValue("")
          }}>Add</Button>
        </Col>
      </Row>
  )
}

export default NewInput;