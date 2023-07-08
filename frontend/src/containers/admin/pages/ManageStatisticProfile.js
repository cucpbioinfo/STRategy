import React, {useEffect, useState} from 'react'
import DraggerUpload from "../../../components/upload/DraggerUpload";
import addHeaders from "../../hoc/addHeader";
import {AutoComplete, Col, notification, Radio, Row} from 'antd';
import axios from "../../../_config/axios";

export function ManageStatisticProfile(props) {
  const [mode, setMode] = useState(0);
  const [country, setCountry] = useState("");
  const [countries, setCountries] = useState([]);

  const handleOnSuccessByCountry = () => {
    notification.success({
      message: `Statistic of country: ${country} has been uploaded`,
    })
  };

  const handleOnSuccessAll = () => {
    notification.success({
      message: `All countries statistic has been uploaded`,
    })
  };

  useEffect(() => {
    axios.get("/countries/").then(res => {
      setCountries(res.data.map(c => ({value: c.country})));
    })
  }, [])

  useEffect(() => {
    setCountry("")
  }, [mode])

  return (
      <div>
        <Radio.Group
            options={[
              {label: 'All', value: 0},
              {label: 'Country', value: 1},
            ]}
            onChange={e => setMode(e.target.value)}
            value={mode}
            optionType="button"
        />
        <br/>
        {mode === 0 ? null : (
            <>
              <br/>
              <Row justify="center">
                <Col span={14}>
                  <AutoComplete
                      allowClear
                      style={{width: '70%'}}
                      placeholder="Country to upload for this file"
                      options={countries}
                      value={country}
                      onChange={value => setCountry(value)}
                  />
                </Col>
              </Row>
            </>
        )}
        <br/>
        {mode === 0 ?
            <DraggerUpload
                infoMessage="If you don't know how to set up an excel file, please see our example"
                exampleUrl="/files/profile-search/all"
                exampleFileName="profile-search-all.csv"
                uploadUrl="/profile-search/"
                method="put"
                uploadTopic={false}
                onSuccess={handleOnSuccessAll}/> :
            <DraggerUpload
                infoMessage="If you don't know how to set up an excel file, please see our example"
                exampleUrl="/files/profile-search/country"
                exampleFileName="profile-search-by-country.csv"
                uploadUrl={`/profile-search/${country}`}
                method="put"
                uploadTopic={false}
                onSuccess={handleOnSuccessByCountry}/>
        }
      </div>
  )
}

export default addHeaders(ManageStatisticProfile, "Manage Length-Based Allele Frequencies for Profile Search");
