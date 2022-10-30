import React, {useEffect, useState} from "react";
import axios from "../../../_config/axios";
import {Col, Radio, Row, Select, Spin} from "antd";
import "./ChartStatistic.css";
import LocusStatisticInfo from "./Locus/LocusStatisticInfo";
import StringUtils from "../../../_utils/StringUtils"
import {useLocation} from "react-router-dom";

const {Option} = Select;

function ChartStatistic() {
  const [currentChromosome, setCurrentChromosome] = useState("A");
  const [currentLocus, setCurrentLocus] = useState(null);
  const [locusList, setLocusList] = useState({A: [], X: [], Y: []});
  const [graphInfo, setGraphInfo] = useState({});
  const [alleleIndex, setAlleleIndex] = useState(null)
  const [loading, setLoading] = useState(false);
  const location = useLocation();

  useEffect(() => {
    axios
        .get("/loci/all")
        .then((res) => {
              setLocusList({
                A: res.data.aloci,
                X: res.data.xloci,
                Y: res.data.yloci,
              });

              const {locus} = StringUtils.queryParams(location.search)
              let chromosome = currentChromosome;

              if (res.data.aloci.includes(locus)) {
                chromosome = "A"
              } else if (res.data.xloci.includes(locus)) {
                chromosome = "X"
              } else if (res.data.yloci.includes(locus)) {
                chromosome = "Y"
              }

              fetchChartData(locus || currentLocus || res.data.aloci[0]);
              setCurrentLocus(locus || currentLocus || res.data.aloci[0]);
              setCurrentChromosome(chromosome);
            }
        )
        .catch((err) => {
          console.log(err);
        });
  }, [currentChromosome, currentLocus, location.search]);

  const fetchChartData = (locus) => {
    setLoading(true);
    let url = `/forenseq-sequences/graph?locus=${locus}`;
    axios.get(url)
        .then(res => {
          setLoading(false);
          setGraphInfo(res.data);
        });
  };

  const onChangeCurrentList = (e) => {
    setAlleleIndex(null);
    setCurrentLocus(e.target.value);
  };

  const currentLocusList = locusList[currentChromosome].map((locus, idx) =>
      <Row key={idx} type="flex" justify="start" align="top">
        <Radio className="locus-radio" value={locus}>{locus}</Radio>
      </Row>
  );

  const onChangeChromosome = (chromosome) => {
    setCurrentChromosome(chromosome)
    setAlleleIndex(null)
    setCurrentLocus(locusList[chromosome][0])
  }

  return (
      <Row style={{marginTop: "20px", marginLeft: "20px"}}>
        <Col xs={6}>
          <Row style={{minWidth: "120px", maxWidth: "240px"}} justify="start" align="middle">
            <Select style={{width: "100%"}} value={currentChromosome} onChange={onChangeChromosome}>
              <Option value="A">Autosomal</Option>
              <Option value="X">X STRs</Option>
              <Option value="Y">Y STRs</Option>
            </Select>
          </Row>
          <Row style={{minWidth: "120px", maxWidth: "240px", marginTop: "10px"}} justify="center" align="middle">
            <Radio.Group onChange={onChangeCurrentList} value={currentLocus}>
              {currentLocusList}
            </Radio.Group>
          </Row>
        </Col>
        <Col xs={17}>
          <Spin tip="Loading..." spinning={loading}>
            <LocusStatisticInfo
                alleleIndex={alleleIndex}
                setAlleleIndex={setAlleleIndex}
                locus={currentLocus}
                graphInfo={graphInfo}
            />
          </Spin>
        </Col>
      </Row>
  );
}

export default ChartStatistic;