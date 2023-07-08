import {Button, Col, Dropdown, Menu, PageHeader, Row, Select, Tag} from "antd";
import React, {useEffect, useState} from "react";
import AlignmentEntry from "../../../components/alignment/AlignmentEntry";
import axios from "../../../_config/axios";
import addHeaders from "../../hoc/addHeader";
import "./PatternAlignment.css"
import PatternAlignmentUtils from "../../../_utils/PatternAlignmentUtils";
import {MoreOutlined} from "@ant-design/icons";

const {Option} = Select;
const {resolvePatternAlignment} = PatternAlignmentUtils;

function PatternAlignment(props) {
  const [currentChromosome, setCurrentChromosome] = useState("a");
  const [currentLocus, setCurrentLocus] = useState("Select Locus");
  const [currentAllele, setCurrentAllele] = useState("Select Allele");
  const [locusList, setLocusList] = useState({a: [], x: [], y: []});
  const [alleleList, setAlleleList] = useState([]);
  const [isToggle, setIsToggle] = useState(false);
  const [isReverse, setIsReverse] = useState(false);
  const [referenced, setReferenced] = useState({});
  const [result, setResult] = useState([]);

  useEffect(() => {
    if (currentLocus !== "Select Locus") {
      axios.get(`/pattern-alignment/loci/${currentLocus}`)
          .then((res) => {
            const pALocus = res.data;
            setIsReverse(pALocus.isReverse);
            setReferenced(pALocus);
          })
    }
  }, [currentLocus])

  useEffect(() => {
    axios
        .get("/loci/all")
        .then((res) => {
          setLocusList({
            a: res.data.aloci,
            x: res.data.xloci,
            y: res.data.yloci,
          });
        })
        .catch((err) => {
          console.log(err);
        });
  }, []);

  const onChangeLocus = (locus) => {
    setCurrentLocus(locus);
    setCurrentAllele("Select Allele");
    setResult([])
    axios.get(`/samples/allele?locus=${locus}`).then((response) => {
      setAlleleList(response.data.sort((a, b) => a.allele - b.allele));
    });
  };

  const onChangeAllele = (allele) => {
    setCurrentAllele(allele);
    axios.get(`/forenseq-sequences/pattern-alignment?locus=${currentLocus}&allele=${parseFloat(allele).toFixed(1)}&fetch=true`).then((response) => {
      setResult(response.data);
    });
  };

  const onChangeChromosome = (value) => {
    setCurrentChromosome(value)
    setCurrentLocus("Select Locus")
    setCurrentAllele("Select Allele");
  }

  const menu = (
      <Menu
          onClick={(e) => console.log(e)}
          items={[
            {
              key: '1',
              label: (
                  <a onClick={() => setIsToggle(prev => !prev)} rel="toggle sequence" href="#">
                    Toggle Sequence
                  </a>
              ),
            }
          ]}
      />
  );


  const DropdownMenu = () => (
      <Dropdown key="more" overlay={menu} placement="bottomRight">
        <Button type="text" icon={<MoreOutlined style={{fontSize: 20}}/>}/>
      </Dropdown>
  );

  function getTags() {
    if (!isReverse) {
      return isToggle ? <Tag
          onClick={() => setIsToggle(false)}
          style={{cursor: "pointer"}}
          color="blue">Reverse</Tag> : <Tag
          onClick={() => setIsToggle(true)}
          style={{cursor: "pointer"}}
          color="green">Forward</Tag>;
    }

    return isToggle ? <Tag
        onClick={() => setIsToggle(false)}
        style={{cursor: "pointer"}}
        color="green">Forward</Tag> : <Tag
        onClick={() => setIsToggle(true)}
        style={{cursor: "pointer"}}
        color="blue">Reverse</Tag>;
  }

  return (
      <div>
        <Row justify="center">
          <Col xs={20} md={12} lg={10} xl={8}>
            <Row align="middle">
              <Col span={12}>
                <Row justify="start">
                  Current chromosome is
                </Row>
              </Col>
              <Col span={12}>
                <Row justify="end">
                  <Select
                      value={currentChromosome}
                      style={{width: 360}}
                      onChange={onChangeChromosome}
                  >
                    <Option value="a">Autosome</Option>
                    <Option value="x">X STRs</Option>
                    <Option value="y">Y STRs</Option>
                  </Select>
                </Row>
              </Col>
            </Row>
            <Row align="middle">
              <Col span={12}>
                <Row justify="start">
                  Current locus is
                </Row>
              </Col>
              <Col span={12}>
                <Row justify="end">
                  <Select value={currentLocus} style={{width: 360}} onChange={(locus) => onChangeLocus(locus)}>
                    {locusList[currentChromosome].map((locus, idx) => (
                        <Option key={idx} className="locus-radio" value={locus}>
                          {locus}
                        </Option>
                    ))}
                  </Select>
                </Row>
              </Col>
            </Row>
            <Row align="middle">
              <Col span={12}>
                <Row justify="start">
                  Current allele is
                </Row>
              </Col>
              <Col span={12}>
                <Row justify="end">
                  <Select value={currentAllele} style={{width: 360}} onChange={(allele) => onChangeAllele(allele)}>
                    {alleleList.map((allele, idx) => (
                        <Option key={idx} className="locus-radio" value={allele}>
                          {allele}
                        </Option>
                    ))}
                  </Select>
                </Row>
              </Col>
            </Row>
          </Col>
        </Row>
        {result.length > 0 &&
            <Row style={{marginTop: "45px"}} justify="center">
              <PageHeader
                  ghost={false}
                  title="Referenced pattern"
                  tags={getTags()}
                  subTitle={resolvePatternAlignment(PatternAlignmentUtils.getPatternAlignmentByAlleles(referenced.patternAlignmentAlleles, currentAllele), isReverse ? !isToggle : isToggle)}
                  extra={[<DropdownMenu key="more"/>]}
              />
            </Row>}
        <Row style={{marginTop: "15px"}} justify="center">
          <Col span={23}>
            {result.length > 0 &&
                <AlignmentEntry reverse={isToggle} currentLocus={currentLocus} setCurrentMenu={props.setCurrentMenu}
                                data={result}/>}
          </Col>
        </Row>
      </div>
  );
}

export default addHeaders(PatternAlignment, "Pattern Alignment");
