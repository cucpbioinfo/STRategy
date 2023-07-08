import React, {useEffect, useState} from "react";
import addHeaders from "../../hoc/addHeader";
import {Button, Col, Collapse, Input, notification, Row} from "antd";
import axios from "../../../_config/axios";
import PanelCountryEdit from "../components/PanelEdit/PanelCountryEdit";

export function ManageCountry(props) {
  const [countryField, setCountryField] = useState("")
  const [countries, setCountries] = useState([]);

  const fetchAllCountries = () => {
    axios.get("/countries/").then(res => {
      setCountries(res.data)
    })
  }

  useEffect(() => {
    fetchAllCountries();
  }, [])

  const addNewCountry = () => {
    setCountryField("");
    axios.post("/countries/", {country: countryField})
        .then(res => {
          fetchAllCountries();
        })
        .catch(err => {
          console.log(err)
          notification.error({
            message: err?.response?.data?.message || "Something went wrong."
          })
        })
  }

  return (
      <Row align="middle" justify="center">
        <Col span={22}>
          <Row justify="center" align="middle">
            <Col xs={23} sm={19} md={17} lg={10} xl={8} xxl={7}>
              <Row justify="center" align="middle">
                <Col xs={24} sm={16} lg={14} xxl={12}>
                  <Input value={countryField} onChange={(e) => setCountryField(e.target.value)}/>
                </Col>
                <Col xs={24} sm={8} lg={10} xxl={12}>
                  <Button style={{width: "100%"}} onClick={() => addNewCountry()}>Add a new country</Button>
                </Col>
              </Row>
            </Col>
          </Row>
          <br/>
          <Row justify="center" align="middle" style={{textAlign: "start"}}>
            <Collapse
                accordion
            >
              {countries.map(({country, id, regions = []}) => (
                  <PanelCountryEdit fetchData={fetchAllCountries} key={id} country={country} regions={regions} id={id}/>
              ))}
            </Collapse>
          </Row>
        </Col>
      </Row>
  )
}

export default addHeaders(ManageCountry, "Manage Countries");
