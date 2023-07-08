import React, {useEffect, useState} from "react";
import {Link, useLocation} from "react-router-dom";
import axios from "../../../_config/axios";
import StringUtils from "../../../_utils/StringUtils";
import {Col, Radio, Row, Segmented, Select, Spin, Table} from "antd";
import StatisticUtils from "../../../_utils/StatisticUtils";
import NumberUtils from "../../../_utils/NumberUtils";
import {dynamicPagesConfig} from "../../../_config/roles";

const {Option} = Select;
const {uniqueId} = NumberUtils;

export function AlleleDetails(props) {
  const setCurrentMenu = props.setCurrentMenu;
  const [currentChromosome, setCurrentChromosome] = useState("A");
  const [currentLocus, setCurrentLocus] = useState(null);
  const [locusList, setLocusList] = useState({A: [], X: [], Y: []});
  const [graphInfo, setGraphInfo] = useState({});
  const [type, setType] = useState(null);
  const [typeList, setTypeList] = useState([]);
  const [loading, setLoading] = useState(false);
  const location = useLocation();

  const {
    alleleList,
  } = StatisticUtils.resolveGraphInfoWithType(graphInfo, type);

  useEffect(() => {
    const types = []
    let defaultType;
    if (graphInfo?.diploid) {
      types.push("Diploid")
      defaultType = "Diploid";
    } else {
      types.push({label: "Diploid", value: "Diploid", disabled: true})
    }

    if (graphInfo?.haploid) {
      types.push("Haploid")
      defaultType = "Haploid";
    } else {
      types.push({label: "Haploid", value: "Haploid", disabled: true})
    }

    if (graphInfo?.diploid && graphInfo?.haploid) {
      defaultType = "Diploid"
    }

    setTypeList(types)
    setType(defaultType);
  }, [graphInfo])

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
          setLoading(false)
          console.log(res.data)
          setGraphInfo(res.data);
        });
  };

  const onChangeCurrentList = (e) => {
    setCurrentLocus(e.target.value);
  };

  const currentLocusList = locusList[currentChromosome].map((locus, idx) =>
      <Row key={idx} type="flex" justify="start" align="top">
        <Radio className="locus-radio" value={locus}>{locus}</Radio>
      </Row>
  );

  const columns = [
    {
      title: 'Allele',
      dataIndex: 'allele',
      width: 100,
      align: "center",
      onCell: (record, _) => {
        return {style: {verticalAlign: "text-top"}, rowSpan: record.rowSpan};
      }
    },
    {
      title: 'Count',
      dataIndex: 'alleleAmount',
      width: 100,
      align: "center",
      onCell: (record, _) => {
        return {style: {verticalAlign: "text-top"}, rowSpan: record.rowSpan};
      }
    },
    {
      title: 'Allele Frequency',
      width: 100,
      align: "center",
      render: (record) => {
        return (
            <div>
              {record.frequency.toFixed(6)}
            </div>
        )
      },
      onCell: (record, _) => {
        return {style: {verticalAlign: "text-top"}, rowSpan: record.rowSpan};
      }
    },
    {
      title: <>Allele detail ( Export to excel <Link
          onClick={() => setCurrentMenu(dynamicPagesConfig.exportAsExcel.url)}
          to={`/export/excel?loci=${currentLocus}`}>click here</Link> )</>,
      children: [
        {
          title: 'Variant count',
          dataIndex: 'seqAmount',
          width: 150,
          align: "center",
        },
        {
          title: 'Variant allele frequency',
          width: 150,
          align: "center",
          render: (record, _) => {
            return (
                <div>
                  {record.variantFrequency.toFixed(6)}
                </div>
            )
          }
        },
        {
          title: 'STR repeat motifs',
          dataIndex: 'repeatMotif',
        },
      ],
    },
  ];

  const convertToDisplay = (infoList) => {
    if (!infoList) return [];
    const result = [];
    infoList.forEach(e => {
      let isSet = false;
      e.sequence.forEach((f, idx) => {
        result.push({
          id: uniqueId(),
          no: idx + 1,
          allele: e.allele,
          alleleAmount: e.amount,
          seqAmount: f.amount,
          frequency: e.frequency,
          variantFrequency: f.frequency,
          changingBase: f.changingBase,
          repeatMotif: f.repeatMotif,
          sequence: f.sequence,
          rowSpan: isSet ? 0 : e.sequence.length,
        })
        isSet = true
      })
    })

    return result;
  }

  const onChangeChromosome = (chromosome) => {
    setCurrentChromosome(chromosome)
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
        <Col xs={17} style={{marginBottom: "2rem"}}>
          <Spin spinning={loading}>
            {typeList?.length > 1 ? <Row style={{marginBottom: "25px"}}>
              <Col xs={24}>
                <Segmented value={type} options={typeList} onChange={e => setType(e)}/>
              </Col>
            </Row> : null}
            <Col xs={24}>
              <Table bordered
                     rowKey="id"
                     size="small"
                     columns={columns}
                     dataSource={convertToDisplay(alleleList?.sort((a, b) => a.allele - b.allele))}
                     pagination={false}
              />
            </Col>
          </Spin>
        </Col>
      </Row>
  );

}

export default AlleleDetails;