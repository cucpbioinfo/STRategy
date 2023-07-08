import React from 'react';
import './App.css';
import 'antd/dist/antd.css';
import PrivateRoutes from './components/private-routes/PrivateRoutes';
import LocalStorageService from './_services/LocalStorageService';
import {useQuery} from "react-query";
import {UserApi} from "./_apis"
import {Divider, Layout, Row} from "antd";
import ChulaEngineeringComputer from "./pictures/logos/ChulaEngineeringComputer.png"
import CUCPBioInfoLab from "./pictures/logos/CUCPBioinfoLab.png"

const {checkUserStatus} = UserApi;
const {Footer} = Layout;

function App() {
  const {data} = useQuery(["check-status", LocalStorageService.getToken()], checkUserStatus, {
    keepPreviousData: true,
  })

  return (
      <div className="App" id={data || "App-ID"}>
        <PrivateRoutes role={LocalStorageService.getRole()}/>
        <Footer style={{
          textAlign: 'center',
          backgroundColor: "#ffffff",
          borderTop: "1px solid #f0f0f0",
          marginTop: "auto",
          padding: "12px",
        }}>
          <Row justify="center" align="middle" style={{cursor:"default"}}>
            <img style={{maxHeight: "40px"}} src={ChulaEngineeringComputer} alt="Chula Engineer Logo"/>
            <Divider type="vertical"/>
            <img style={{maxHeight: "40px"}} src={CUCPBioInfoLab} alt="CUCP Bioinfo Lab Logo"/>
            <Divider type="vertical"/>
            CUCPBioinfo Lab, Department of Computer Engineering, Chulalongkorn University ©2022 Created
          </Row>
        </Footer>
      </div>
  );
}

export default App;