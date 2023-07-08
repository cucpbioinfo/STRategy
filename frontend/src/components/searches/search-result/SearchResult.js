import {CaretRightOutlined, FileSearchOutlined} from '@ant-design/icons';
import {Col, Collapse, Progress, Row, Table, Typography} from 'antd';
import React from 'react';

const {Panel} = Collapse;
const {Title} = Typography;

function SearchResult(props) {
  const {matchedSample, isClicked} = props;
  const {amount, total} = matchedSample;

  const columnsSample = [
    {
      title: 'Sample Year',
      dataIndex: 'sampleYear',
      align: "center",
    },
    {
      title: 'Sample ID',
      dataIndex: 'sampleId',
      align: "center",
      render: (_, record) => <div>
        <a rel="noreferrer" target="_blank" href={`/samples/${record.id}/forenseq`}>
          {record.sampleId}&nbsp;<FileSearchOutlined/>
        </a>
      </div>
    },
  ];

  const columnsProvince = [
    {
      title: 'Province',
      dataIndex: 'province',
      align: "center",
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      align: "center",
    },
  ];

  const columnsRace = [
    {
      title: 'Race',
      dataIndex: 'race',
      align: "center",
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      align: "center",
    },
  ];

  const columnsRegion = [
    {
      title: 'Region',
      dataIndex: 'region',
      align: "center",
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      align: "center",
    },
  ];

  const tableSpan = {xs: 24, sm: 24, md: 24, lg: 24, xl: 24, xxl: 24};
  const columnsSpan = {xs: 23, sm: 20, md: 16, lg: 12, xl: 10, xxl: 8};

  return (
      <Row justify="center">
        <Col {...columnsSpan}>
          <Row justify="center" style={{marginTop: "20px"}}>
            <Title level={4} type="primary">Matched Samples</Title>
          </Row>
          <Row justify="center" align="middle" style={{margin: "10px 0"}}>
            <Col {...tableSpan}>
              <Progress
                  type="dashboard"
                  strokeColor={{
                    '0%': '#108ee9',
                    '100%': '#87d068',
                  }}
                  format={() => `${amount}/ ${total}`}
                  percent={(amount / total) * 100}
              />
            </Col>
            {isClicked && matchedSample?.sampleDetails.length !== 0 && <Col {...tableSpan}>
              <Collapse
                  defaultActiveKey={['1']}
                  expandIcon={({isActive}) => <CaretRightOutlined rotate={isActive ? 90 : 0}/>}
              >
                <Panel header="Sample details" key="1">
                  <Table scroll={{y: 200}}
                         size="small"
                         rowKey="sampleId"
                         pagination={false}
                         dataSource={matchedSample.sampleDetails.map(e => e ? e : "not specified")}
                         columns={columnsSample}
                  />
                </Panel>
                <Panel header="Country" key="2">
                  {matchedSample.countryCount.map(e => e.country ? e : {
                    ...e,
                    country: "Not specified",
                  }).map(e => (
                      <div key={e.country}>
                        {e.country} : {e.amount}
                        <Collapse
                            expandIcon={({isActive}) => <CaretRightOutlined rotate={isActive ? 90 : 0}/>}
                        >
                          <Panel header="Province" key="3">
                            <Table scroll={{y: 200}}
                                   size="small"
                                   rowKey="province"
                                   pagination={false}
                                   dataSource={e.provinceCounts.map(e => e.province ? e : {
                                     ...e,
                                     province: "Not specified",
                                   })}
                                   columns={columnsProvince}
                            />
                          </Panel>
                          <Panel header="Region" key="5">
                            <Table scroll={{y: 200}}
                                   size="small"
                                   rowKey="region"
                                   pagination={false}
                                   dataSource={e.regionCounts.map(e => e.region ? e : {
                                     ...e,
                                     region: "Not specified",
                                   })}
                                   columns={columnsRegion}
                            />
                          </Panel>
                        </Collapse>
                      </div>
                  ))}
                </Panel>
                <Panel header="Race" key="4">
                  <Table scroll={{y: 200}}
                         size="small"
                         rowKey="race"
                         pagination={false}
                         dataSource={matchedSample.raceCount.map(e => e.race ? e : {
                           ...e,
                           race: "Not specified",
                         })}
                         columns={columnsRace}
                  />
                </Panel>
              </Collapse>
            </Col>}
          </Row>
        </Col>
      </Row>
  );
}

export default SearchResult;
