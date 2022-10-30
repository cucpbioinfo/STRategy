import React from "react";
import {Checkbox, Col, Row} from "antd";
import "./ManageProvinceRow.css"

export function ManageProvinceRow(props) {
  const item = props.item

  return (
      <Row className="MangeProvinceRow" justify="space-between" style={{borderRadius: "2px 2px 0 0"}}>
        <Col span={1}>
          <Checkbox/>
        </Col>
        <Col span={4}>
          {item.province}
        </Col>
        <Col span={5}>
          {item.nativeName}
        </Col>
        <Col span={5}>
          {item.latitude}
        </Col>
        <Col span={5}>
          {item.longitude}
        </Col>
        <Col span={4}>
          {item.region?.region}
        </Col>
      </Row>
  )
}

export default ManageProvinceRow;

