import React, {useState} from "react";
import {Button, Col, Collapse, Divider, Input, Popconfirm, Row, Typography} from "antd";
import axios from "axios";
import "./PanelRaceEdit.css"

const {Panel} = Collapse;

export function PanelRaceEdit(props) {
  const {id, race, fetchRaces} = props;

  const [isEdit, setIsEdit] = useState(false);
  const [editValue, setEditValue] = useState("");

  const deleteRaceById = () => {
    axios.delete("/v2/races/" + id).then(res => {
      fetchRaces();
    })
  }

  const updateRaceById = () => {
    axios.put("/v2/races/" + id, {race: editValue}).then(res => {
      fetchRaces();
    })
  }

  const extraPanel = (id, race) => {
    return isEdit ? (
        <div>
          <Button onClick={(e) => {
            e.stopPropagation();
            setIsEdit(false);
            updateRaceById();
          }}>Done</Button>
          <Divider type="vertical"/>
          <Button onClick={(e) => {
            e.stopPropagation();
            setIsEdit(false);
          }}>Cancel</Button>
        </div>
    ) : (
        <div style={{marginLeft: "25px"}}>
          <Typography.Link
              onClick={e => {
                e.stopPropagation()
                setIsEdit(true);
                setEditValue(race);
              }}
          >Edit</Typography.Link>
          <Divider type="vertical"/>
          <Popconfirm
              placement="topRight"
              title={`Do you want to delete Race: ${race}`}
              onConfirm={(e) => {
                e.stopPropagation();
                deleteRaceById()
              }}
              onCancel={(e) => e.stopPropagation()}
              okText="Yes"
              cancelText="No"
          >
            <Typography.Link onClick={(e) => {
              e.stopPropagation();
            }}>Delete</Typography.Link>
          </Popconfirm>
        </div>
    )
  }


  return (
      <div className="race-panel">
        <Row style={{width: "100%"}}>
          <Col span={12}>
            {isEdit ? <Input value={editValue} onChange={(e) => setEditValue(e.target.value)}/> : `Race: ${race}`}
          </Col>
          <Col span={12}>
            <Row justify="end">
              {extraPanel(id, race)}
            </Row>
          </Col>
        </Row>
      </div>
  )
}

export default PanelRaceEdit;