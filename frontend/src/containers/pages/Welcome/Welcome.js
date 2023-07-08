import React from 'react'
import {Col, Row, Typography} from 'antd'
import ChulaLogo from "../../../pictures/logos/Logo.png"

const {Title, Text} = Typography;

class PageWelcome extends React.Component {
  render() {
    return (
        <Row style={{marginTop: "25px", minHeight: "600px"}} align="middle">
          <Col span={24}>
            <Row justify="center">
              <Title level={3}>Welcome to Forensic Information Management Systems</Title>
            </Row>
            <Row justify="center">
              <img style={{maxWidth: "40%", margin: "20px"}} alt="fgxbio logo" src={ChulaLogo}/>
            </Row>
            <Row justify="center">
              <Title level={3}><Text>The Database for Short Tandem Repeat (STR) Sequence</Text></Title>
            </Row>
          </Col>
        </Row>
    )
  }
}

export default PageWelcome;