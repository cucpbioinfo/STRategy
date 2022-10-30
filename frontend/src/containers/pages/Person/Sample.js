import {Button, Col, Collapse, Descriptions, PageHeader, Row, Select, Table} from "antd";
import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import axios from "../../../_config/axios";

const {Option} = Select;
const {Panel} = Collapse;

const frCol = [
  {
    title: "Locus",
    dataIndex: "locus",
    key: "locus",
  },
  {
    title: "Genotype",
    dataIndex: "genotype",
    key: "genotype",
  },
  {
    title: "QC Indicator",
    dataIndex: "qc_indicator",
    key: "qc_indicator",
    render: (text) => <div style={{wordWrap: "break-word"}}>{text}</div>
  },
];

const fsrCol = [
  {
    title: "Locus",
    dataIndex: "locus",
  },
  {
    title: "Allele",
    dataIndex: "allele",
    key: "allele",
  },
  {
    title: "Sequence",
    dataIndex: "sequence",
    key: "sequence",
    render: (text, record, index) => <div style={{wordBreak: "break-all"}}>{text}</div>
  },
  {
    title: "Read count",
    dataIndex: "read_count",
  },
];

function Person() {
  const {id} = useParams();
  const [forenseqPerson, setForenseqPerson] = useState({
    person: {country: {}, province: {}, race: {}},
    fr_a: [],
    fr_x: [],
    fr_y: [],
    fr_i: [],
    fsr_a: [],
    fsr_x: [],
    fsr_y: [],
    fsr_i: [],
  });
  const [currentPage, setCurrentPage] = useState("A");
  const [forenseqTable, setForenseqTable] = useState([]);
  const [forenseqSeqTable, setForenseqSeqTable] = useState([]);

  useEffect(() => {
    axios
        .get(`/samples/${id}/forenseq`)
        .then((res) => {
          setForenseqPerson(res.data);
          setForenseqTable(res.data.fr_a);
          setForenseqSeqTable(res.data.fsr_a);
        })
        .catch((err) => {
        });
  }, [id]);

  const handleChange = (value) => {
    setCurrentPage(value);
    switch (value) {
      case "A":
        setForenseqTable(forenseqPerson.fr_a);
        setForenseqSeqTable(forenseqPerson.fsr_a);
        break;
      case "X":
        setForenseqTable(forenseqPerson.fr_x);
        setForenseqSeqTable(forenseqPerson.fsr_x);
        break;
      case "Y":
        setForenseqTable(forenseqPerson.fr_y);
        setForenseqSeqTable(forenseqPerson.fsr_y);
        break;
      case "I":
        setForenseqTable(forenseqPerson.fr_i);
        setForenseqSeqTable(forenseqPerson.fsr_i);
        break;
      default:
    }
  };

  const {
    gender,
    country,
    province,
    race
  } = forenseqPerson.person;
  const region = forenseqPerson.person.region?.region

  return (
      <Row justify="center">
        <Col xs={23} sm={22} md={20} lg={16} xl={12} xxl={10}>
          <Row style={{marginTop: "10px"}} align="center">
            <h2>
              Sample ID: {id}
            </h2>
          </Row>
          <Row justify="space-between" align="middle">
            <Col xs={24}>
              <div className="site-page-header-ghost-wrapper">
                <PageHeader
                    ghost={false}
                    title="Basic"
                    subTitle="Information"
                >
                  <Descriptions size="small" column={3}>
                    <Descriptions.Item label="Gender">{gender || "Not specific"}</Descriptions.Item>
                    <Descriptions.Item label="Country">{country?.country || "Not specific"}</Descriptions.Item>
                    <Descriptions.Item label="Region">{region || "Not specific"}</Descriptions.Item>
                    <Descriptions.Item label="Province">{province?.province || "Not specific"}</Descriptions.Item>
                    <Descriptions.Item label="Race">{race?.race || "Not specific"}</Descriptions.Item>
                  </Descriptions>
                </PageHeader>
              </div>
            </Col>
          </Row>
          <Row justify="center">
            <Collapse style={{width: "100%"}}>
              <Panel header="Genotype table" key="1">
                <Row className="col-text" justify="center">
                  <Select value={currentPage} style={{width: 360}} onChange={handleChange}>
                    <Option value="A">Autosome</Option>
                    <Option value="X">X STRs</Option>
                    <Option value="Y">Y STRs</Option>
                    <Option value="I">iSNPs</Option>
                  </Select>
                </Row>
                <Row justify="center">
                  <Table
                      rowKey="locus"
                      dataSource={forenseqTable}
                      columns={frCol}
                      size="small"
                      pagination={{
                        position: ["bottomCenter"],
                        showSizeChanger: true,
                      }}
                  />
                </Row>
              </Panel>
              <Panel header="Sequence table" key="2">
                <Table
                    rowKey={(record) => record.locus + record.sequence}
                    dataSource={forenseqSeqTable}
                    columns={fsrCol}
                    size="small"
                    pagination={{
                      position: ["bottomCenter"],
                      showSizeChanger: true,
                    }}/>
              </Panel>
            </Collapse>
          </Row>
        </Col>
      </Row>
  );
}

export default Person;
