import React, {useEffect, useState} from 'react'
import {Badge, Button, Col, Descriptions, PageHeader, Row, Spin, Typography} from 'antd';
import './ManagePatternAlignment.css'
import axios from "../../../_config/axios";
import addHeaders from "../../hoc/addHeader";
import {WarningOutlined} from "@ant-design/icons";
import {dynamicPagesConfig} from "../../../_config/roles";

const {Link} = Typography;

export function ManagePatternAlignment(props) {
  const {fetchMenuDetails, menuNotification} = props;
  const isNotificationEnabled = menuNotification && menuNotification["Admin"] && menuNotification["Admin"][dynamicPagesConfig.managePatternAlignment.url] === 1
  const [isLoading, setIsLoading] = useState(true);
  const [lastUpdate, setLastUpdate] = useState("");
  const [note, setNote] = useState("");
  const [numberOfSample, setNumberOfSample] = useState(0);
  const [isThereNewReference, setIsThereNewReference] = useState(0);

  const performLoadDetail = () => {
    setIsLoading(true);
    axios.get("/forenseq-sequences/pattern-alignment/calculation").then(res => {
      const {time, note} = res.data;
      const lastUpdate = new Date(time);
      setLastUpdate(lastUpdate.toDateString() + " " + lastUpdate.toTimeString());
      setNote(note);
      fetchMenuDetails()
      setIsLoading(false);
    })
    axios.get("/configuration/keys?keys=" +
        "number_of_new_samples_since_update_pattern_alignment," +
        "is_there_new_referenced_pattern").then(res => {
      const data = res.data;
      let isThereNew = parseInt(data["is_there_new_referenced_pattern"]);
      let numberOfNewSample = parseInt(data["number_of_new_samples_since_update_pattern_alignment"]);
      setNumberOfSample(numberOfNewSample)
      setIsThereNewReference(isThereNew)
    })
  }

  const dismissNotification = () => {
    axios.patch("/configuration/keys", [{
      configurationKey: "required_update_pattern_notification_enabled",
      configurationValue: "0"
    }]).then(res => {
      fetchMenuDetails();
    })
  }

  const performGenerateSeqAlign = () => {
    setIsLoading(true);
    axios.put("/forenseq-sequences/pattern-alignment/calculation").then(res => {
      performLoadDetail();
    })
  }

  useEffect(() => {
    performLoadDetail()
  }, [])

  return (
      <Row justify="center">
        <Col xs={24} sm={24} md={22} lg={16} xl={14} xxl={12}>
          <Spin spinning={isLoading} tip="This might take minutes...">
            <div className="site-page-header-ghost-wrapper">
              <PageHeader
                  ghost={false}
                  title="Pattern Alignment"
                  subTitle="Manager"
                  extra={[
                    <Button key="0" onClick={performLoadDetail}>
                      Refresh
                    </Button>,
                    <Button key="1" onClick={performGenerateSeqAlign} type="primary">
                      Generate
                    </Button>,
                  ]}
              >
                <Descriptions size="small" column={1}>
                  <Descriptions.Item label="Last Update">{lastUpdate}</Descriptions.Item>
                  <Descriptions.Item label="Note">
                    {note}
                  </Descriptions.Item>
                </Descriptions>
                {isThereNewReference || numberOfSample ?
                    <div style={{textAlign: "start", backgroundColor: "yellow", fontWeight: "bold"}}>
                      <Badge count={(isThereNewReference || numberOfSample) && isNotificationEnabled ? 1 : 0}>
                        <WarningOutlined/> Required the regeneration of referenced STR repeat motifs patterns
                      </Badge>
                      &nbsp;:&nbsp;{numberOfSample ? numberOfSample + " new samples have just been uploaded since the last generation" : null}
                      {numberOfSample && isThereNewReference ? " and " : null}
                      {isThereNewReference ? "Reference patterns just been uploaded" : null}
                      &nbsp;{isNotificationEnabled ? <Link onClick={dismissNotification}><u>(Dismiss)</u></Link> : null}
                    </div> : null}
              </PageHeader>
            </div>
          </Spin>
        </Col>
      </Row>
  )
}

export default addHeaders(ManagePatternAlignment, "Manage Pattern Alignment");
