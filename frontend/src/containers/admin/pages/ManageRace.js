import React, {useEffect, useState} from 'react'
import addHeaders from "../../hoc/addHeader";
import {Button, Col, Input, List, Row} from "antd";
import axios from "axios";
import PanelRaceEdit from "../components/PanelEdit/PanelRaceEdit";


export function ManageRace() {
  const [raceField, setRaceField] = useState("");
  const [allRacesData, setAllRacesData] = useState([]);

  const addNewRace = () => {
    axios.post("/v2/races/", {race: raceField}).then(res => {
      setRaceField("");
      fetchRaces();
    })
  }

  const fetchRaces = () => {
    axios.get("/v2/races/").then(res => {
      setAllRacesData(res.data);
    })
  }

  useEffect(() => {
    fetchRaces();
  }, [])

  return (
      <Row align="middle" justify="center">
        <Col span={22}>
          <Row justify="center" align="middle">
            <Col xs={18} sm={16} md={12} lg={8} xl={6} xxl={5}>
              <Row justify="center" align="middle">
                <Col xs={24} sm={16} lg={14} xxl={12}>
                  <Input value={raceField} onChange={(e) => setRaceField(e.target.value)}/>
                </Col>
                <Col xs={24} sm={8} lg={10} xxl={12}>
                  <Button style={{width: "100%"}} onClick={() => addNewRace()}>Add a new race</Button>
                </Col>
              </Row>
            </Col>
          </Row>
          <br/>
          <Row justify="center" align="middle" style={{textAlign: "start"}}>
            <Col xs={22} sm={16} md={12} lg={10} xl={8} xxl={6}>
              <List
                  size="small"
                  bordered
                  dataSource={allRacesData}
                  rowKey={({id}) => id}
                  renderItem={({race, id}) => <PanelRaceEdit fetchRaces={fetchRaces} key={id} race={race} id={id}/>}
              />
            </Col>
          </Row>
        </Col>
      </Row>
  )
}

export default addHeaders(ManageRace, "Manage races");
