import React, {useCallback, useEffect, useState} from "react";
import {Alert, Button, Col, Form, Input, Row, Select, Skeleton} from "antd";
import axios from "../../../_config/axios";
import "./FormSearch.css";
import SearchResult from "../search-result/SearchResult";

const {Option} = Select;

export default function FormSearch(props) {
  const [kitsList, setKitsList] = useState({a: [], x: [], y: []});
  const [curChromosome, setCurChromosome] = useState("a");
  const [curKit, setCurKit] = useState("");
  const [curLociList, setCurLociList] = useState([]);
  const [isClicked, setIsClicked] = useState(false);
  const [matchedSample, setMatchedSample] = useState({
    amount: 0,
    total: 0,
    sampleDetails: [],
    countryCount: [],
    provinceCount: [],
    raceCount: [],
    regionCount: [],
  });
  const [loading, setLoading] = useState(true);
  const [form] = Form.useForm();

  const fetchKits = useCallback((kit, chro) => {
    const targetKit = kitsList[chro].find((e) => e.kit === kit);
    axios
        .get(`/kits/${targetKit.id}/loci`)
        .then((res) => {
          setCurLociList(res.data._embedded.loci.map((e) => e.locus));
        })
        .catch((err) => {
          console.error(err);
        });
  }, [kitsList]);


  useEffect(() => {
    axios
        .get("/kits/all")
        .then((res) => {
          setKitsList({
            a: res.data.akits,
            x: res.data.xkits,
            y: res.data.ykits,
          });
          setCurKit(res.data.akits[0].kit);
          axios
              .get(`/kits/8/loci`)
              .then((res) => {
                setCurLociList(res.data._embedded.loci.map((e) => e.locus));
                setLoading(false);
              })
              .catch((err) => {
                console.error(err);
                setLoading(false);
              });
        })
        .catch((err) => {
          console.error(err);
        });
  }, []);

  useEffect(() => {
    if (kitsList.a.length !== 0) {
      fetchKits(curKit, curChromosome)
    }
  }, [curChromosome, curKit])

  const getFields = () => {
    const children = [];

    for (let i = 0; i < curLociList.length; i++) {
      children.push(
          <Col span={8} key={i}>
            <Form.Item name={curLociList[i]} label={<>&nbsp;{curLociList[i]}</>}>
              <Input placeholder="m[, n]"/>
            </Form.Item>
          </Col>
      );
    }

    return children;
  };

  const handleSearch = (values) => {
    let data = [];
    let locus = Object.keys(values);
    for (let i = 0; i < locus.length; i++) {
      if (typeof values[locus[i]] !== "undefined") {
        let multi = values[locus[i]].split(",");
        multi.forEach((allele) =>
            data.push({
              locus: `${locus[i]}`,
              allele: `${allele}`,
            })
        );
      }
    }

    axios.post("/samples/person", data).then((res) => {
      setMatchedSample({
        amount: res.data.amount,
        sampleDetails: res.data.sampleDetails,
        countryCount: res.data.countryCount,
        raceCount: res.data.raceCount,
        total: res.data.total,
      });
    });

    setIsClicked(true);
  };

  const handleChangeChromosome = (value) => {
    const nextKit = kitsList[value][0].kit;

    setCurChromosome(value);
    setCurKit(nextKit);
  };
  const handleChangeKit = (value) => {
    setCurKit(value);
  };

  const curKitList = kitsList[curChromosome]?.map(({kit}, idx) => {
    return (
        <Option key={idx} value={kit}>
          {kit}
        </Option>
    );
  });

  return (
      <>
        <Row justify="center">
          <Col xs={23} sm={20} md={16} lg={12} xl={10} xxl={8}>
            <Row>
              <Col span={24}>
                <Alert
                    style={{textAlign: "start", marginBottom: "1rem"}}
                    message="Search pattern"
                    action={
                      <Button size="small" onClick={() => form.setFieldsValue({
                        D12S391: "19,25",
                        TPOX: "8",
                        D13S317: "8",
                      })}>
                        Example
                      </Button>
                    }
                    description={
                      <p>
                        [Locus] : [Allele]
                        <br/>
                        [Locus] : [Allele] , [Allele]
                      </p>
                    }
                    type="info"
                    showIcon
                    closable
                />
              </Col>
            </Row>
            <Row justify="start" align="middle" style={{margin: "10px 0"}}>
              <Col span={12}>
                <Row justify="start" align="middle">
                  Current chromosome is
                </Row>
              </Col>
              <Col span={12}>
                <Row justify="end" align="middle">
                  <Select
                      value={curChromosome}
                      style={{width: 360}}
                      onChange={handleChangeChromosome}
                  >
                    <Option value="a">Autosome</Option>
                    <Option value="x">X STRs</Option>
                    <Option value="y">Y STRs</Option>
                  </Select>
                </Row>
              </Col>
            </Row>
            <Row justify="start" align="middle" style={{margin: "0 0 15px 0"}}>
              <Col span={12}>
                <Row justify="start">
                  Current kit is
                </Row>
              </Col>
              <Col span={12}>
                <Row justify="end">
                  <Select
                      value={curKit}
                      style={{width: 360}}
                      onChange={(kit) => handleChangeKit(kit)}
                  >
                    {curKitList}
                  </Select>
                </Row>
              </Col>
            </Row>
            <Row style={{margin: "0 0 15px 0"}}>
              {loading ? (
                  <Skeleton active/>
              ) : (
                  <Form
                      form={form}
                      name="advanced_search"
                      className="ant-advanced-search-form"
                      onFinish={handleSearch}
                      labelCol={{xs: 12, md: 11}}
                      wrapperCol={{xs: 12, md: 13}}
                      colon={false}
                      labelAlign="left"
                  >
                    <Row gutter={24}>{getFields()}</Row>
                    <Row>
                      <Col span={24} style={{textAlign: "right"}}>
                        <Button type="primary" htmlType="submit">
                          Search
                        </Button>
                        <Button
                            style={{margin: "0 8px"}}
                            onClick={() => {
                              form.resetFields();
                            }}
                        >
                          Clear
                        </Button>
                      </Col>
                    </Row>
                  </Form>
              )}
            </Row>
          </Col>
        </Row>
        <SearchResult isClicked={isClicked} matchedSample={matchedSample}/>
      </>
  )
      ;
}
