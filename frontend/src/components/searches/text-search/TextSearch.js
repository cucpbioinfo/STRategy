import React, {useEffect, useState} from "react";
import {Alert, Button, Col, Form, Input, Row} from "antd";
import axios from "axios";
import SearchResult from "../search-result/SearchResult";

const {TextArea} = Input;

function TextSearch(props) {
  const [isClicked, setIsClicked] = useState(false);
  const [fieldValue, setFieldValue] = useState("");
  const [form] = Form.useForm();
  const [matchedSample, setMatchedSample] = useState({
    amount: 0,
    total: 0,
    sampleDetails: [],
    countryCount: [],
    provinceCount: [],
    raceCount: [],
    regionCount: [],
  });

  useEffect(() => {
    form.setFieldsValue({
      search: fieldValue,
    });
  }, [fieldValue, form]);

  const extractLocusAllele = (values) => {
    let data = [];
    let tmp = values.search.replace(/(\r\n|\n|\r)/gm, "$");
    let tmp1 = tmp.split("$");
    let tmp2 = tmp1.map((e) => e.trim());

    tmp2.forEach((e) => {
      let tmp4 = e.split(":");
      let tmp5 = tmp4[1].split(",");
      tmp5.forEach((ele) => {
        data.push({locus: tmp4[0], allele: ele});
      });
    });

    return data;
  };

  const handleSubmit = (values) => {
    let data = extractLocusAllele(values);

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

  return (
      <>
        <Row justify="center">
          <Col xs={23} sm={20} md={16} lg={12} xl={10} xxl={8}>
            <Alert
                style={{
                  textAlign: "start",
                  marginBottom: "1rem"
                }}
                message="Search pattern"
                action={
                  <Button size="small" onClick={() => setFieldValue("D12S391:19,25\nTPOX:8\nD13S317:8")}>
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
        <Row justify="center">
          <Col xs={23} sm={20} md={16} lg={12} xl={10} xxl={8}>
            <Form onFinish={handleSubmit} form={form}>
              <Form.Item
                  name="search"
                  rules={[
                    {
                      required: true,
                      message: "Please input at least 1 locus and allele.",
                    },
                  ]}
              >
                <TextArea
                    placeholder="Locus : m[, n]"
                    autoSize={{minRows: 3, maxRows: 5}}
                />
              </Form.Item>
              <Form.Item>
                <Col span={24} style={{textAlign: "right"}}>
                  <Button type="primary" htmlType="submit">
                    Search
                  </Button>
                  <Button
                      style={{marginLeft: 8}}
                      onClick={() => setFieldValue("")}
                  >
                    Clear
                  </Button>
                </Col>
              </Form.Item>
            </Form>
          </Col>
        </Row>
        <SearchResult
            isClicked={isClicked}
            matchedSample={matchedSample}
        />
      </>
  );
}

export default TextSearch;
