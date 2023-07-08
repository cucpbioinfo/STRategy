import {Col, Popover, Row, Segmented, Table, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {Bar, Doughnut} from "react-chartjs-2";
import ColorUtils from "../../../../_utils/ColorUtils";
import {QuestionCircleOutlined} from "@ant-design/icons";
import StatisticUtils from "../../../../_utils/StatisticUtils";

const {Text} = Typography;
const {maskedAllele, generateAlphaColor} = ColorUtils;

function LocusStatisticInfo(props) {
  const {
    locus,
    graphInfo,
    alleleIndex,
    setAlleleIndex,
  } = props;
  const [curLocus, setCurrentLocus] = useState(null);
  const [dataColors, setDataColors] = useState([]);
  const [dataBorderColors, setDataBorderColors] = useState([]);
  const [colorAlpha, setColorAlpha] = useState({});
  const [type, setType] = useState(null);
  const [typeList, setTypeList] = useState([]);
  let observedAllele = [];

  const {
    alleleList,
    numberOfTotal,
    heterozygosity,
    expectedHeterozygosity,
    matchProbability,
    polymorphicInformationContent,
    powerOfDiscrimination,
    powerOfExclusion,
  } = StatisticUtils.resolveGraphInfoWithType(graphInfo, type);

  let alleleAmount = [];

  useEffect(() => {
    setDataColors(Array(alleleList?.length || 0).fill('rgba(54, 162, 235, 0.2)'))
    setDataBorderColors(Array(alleleList?.length || 0).fill('rgb(54, 162, 235)'))
  }, [alleleList])

  useEffect(() => {
    if (alleleList) {
      let result = []
      for (let {sequence} of alleleList) {
        result.push(...sequence)
      }
      setColorAlpha(generateAlphaColor(result))
    }
  }, [curLocus])

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

  alleleList?.sort((a, b) => a.allele - b.allele).forEach((sample) => {
    alleleAmount.push(sample?.frequency.toFixed(3));
    observedAllele.push(sample?.allele === "0.0" ? "Invalid" : sample.allele);
  });

  let chartData = {
    labels: observedAllele,
    datasets: [
      {
        label: "Allele Frequency",
        data: alleleAmount,
        backgroundColor: dataColors,
        borderColor: dataBorderColors,
        borderWidth: 1
      },
    ],
  };

  const columns = [
    {
      title: "Sequence",
      key: "sequence",
      render: (_, record) => (
          <div style={{wordBreak: "break-all"}}>
            {maskedAllele(record.sequence, record.repeatMotif, record.changingBase, colorAlpha)}
          </div>
      ),
    },
    {
      title: "STR repeat motifs",
      key: "repeatMotif",
      render: (_, record) => (
          <div style={{wordBreak: "break-all"}}>
            {record.repeatMotif}
          </div>
      ),
    },
    {
      title: 'Count',
      dataIndex: 'amount'
    },
    {
      title: 'Allele frequency',
      render: (_, record) => (
          <div>
            {record.frequency}
          </div>
      ),
    },
  ];

  const handleElementClick = e => {
    const eventIndex = e.length > 0 && e[0]?._index;
    if (eventIndex !== null && eventIndex !== undefined && eventIndex !== false) {
      if (eventIndex === alleleIndex && locus === curLocus) {
        setAlleleIndex(null);
        setDataColors(Array(alleleList?.length || 0).fill('rgba(54, 162, 235, 0.2)'))
      } else {
        setAlleleIndex(eventIndex);
        setDataColors(Array(alleleList?.length || 0).fill(0).map((val, idx) => idx === eventIndex ? 'rgba(54, 162, 235, 1)' : 'rgba(54, 162, 235, 0.2)'))
      }
      setCurrentLocus(locus);
    }
  }

  let barChartOptions = {
    legend: {
      display: false
    },
    maintainAspectRatio: true,
    scales: {
      xAxes: [
        {
          scaleLabel: {
            display: true,
            labelString: 'allele'
          }
        },
      ],
      yAxes: [
        {
          ticks: {beginAtZero: true},
          scaleLabel: {
            display: true,
            labelString: 'amount'
          }
        },
      ],
    },
  };

  return (
      <div>
        <p>
          <strong>Locus : </strong>
          {locus}
        </p>
        {typeList?.length > 1 ? <Row>
          <Col xs={24}>
            <Segmented value={type} options={typeList} onChange={e => setType(e)}/>
          </Col>
        </Row> : null}
        <br/>
        <Row>
          <Col xs={14}>
            <Row>
              <Col span={24}>
                <Bar
                    onElementsClick={handleElementClick}
                    data={chartData}
                    skipNull={true}
                    options={barChartOptions}
                />
              </Col>
            </Row>
            <br/>
            {alleleIndex !== null && alleleIndex !== undefined && alleleList[alleleIndex]?.sequence?.length > 0 &&
                <>
                  <Row>
                    <p>Allele:&nbsp;<Text strong>{alleleList[alleleIndex].allele}</Text>
                      &nbsp;- Number of variants:&nbsp;<Text strong>{alleleList[alleleIndex]?.sequence?.length}</Text>,
                      Total samples:&nbsp;<Text strong>{alleleList[alleleIndex]?.amount}</Text>
                      <span> </span><Popover
                          content={
                            <div>
                              {Object
                                  .entries(colorAlpha)
                                  .sort((a, b) => a[0].length - b[0].length)
                                  .map(([a, c]) => (
                                          <div
                                              key={c}
                                          ><span
                                              style={{
                                                backgroundColor: c,
                                                border: "2px solid #FFFFFF",
                                                borderRadius: "5px",
                                                cursor: "default"
                                              }}>
                                      &nbsp;&nbsp;&nbsp;&nbsp;
                                    </span> - {a}
                                          </div>
                                      )
                                  )}
                            </div>
                          }
                          title="All color detail">
                        <QuestionCircleOutlined/>
                      </Popover></p>
                  </Row>
                  <Row>
                    <Table rowKey={(record) => record.sequence + record.repeatMotif}
                           dataSource={alleleList[alleleIndex].sequence} columns={columns}
                           pagination={false}/>
                  </Row>
                </>}
          </Col>
          <Col xs={10}>
            <Row align="middle">
              <Col span={24}>
                <div>
                  <br/>
                  <br/>
                  <p>
                    <strong>Summary</strong>
                  </p>
                  <div key={locus}>
                    <div style={{textAlign: "start", paddingLeft: "30%"}}>
                      <div><strong>Locus : </strong> {locus} </div>
                      <br/>
                      <div><strong>Total samples : </strong>{numberOfTotal}</div>
                    </div>
                    <div style={{textAlign: "start", paddingLeft: "30%"}}>
                      {heterozygosity ? <div>
                        <strong>Observed Heterozygosity (Hobs) = </strong>{Number(heterozygosity)?.toFixed(6)}
                      </div> : null}
                      {expectedHeterozygosity ? <div>
                        <strong>Expected Heterozygosity (Hexp) = </strong>{Number(expectedHeterozygosity)?.toFixed(6)}
                      </div> : null}
                      {matchProbability ? <div>
                        <strong>Match probability (PM) = </strong>{Number(matchProbability)?.toFixed(6)}
                      </div> : null}
                      {polymorphicInformationContent ? <div>
                        <strong>Polymorphic Information Content (PIC)
                          = </strong>{Number(polymorphicInformationContent)?.toFixed(6)}
                      </div> : null}
                      {powerOfDiscrimination ? <div>
                        <strong>Power of Discrimination (PD) = </strong>{Number(powerOfDiscrimination)?.toFixed(6)}
                      </div> : null}
                      {powerOfExclusion ? <div>
                        <strong>Power of Exclusion (PE) = </strong>{Number(powerOfExclusion)?.toFixed(6)}
                      </div> : null}
                    </div>
                  </div>
                </div>
              </Col>
            </Row>
            <br/>
            <Row align="middle">
              <Col span={24}>
                {Boolean(heterozygosity) && (
                    <Doughnut data={{
                      labels: ["Heterozygosity", "Homozygosity"],
                      datasets: [
                        {
                          label: "Amount",
                          data: [heterozygosity?.toFixed(2), (1 - heterozygosity)?.toFixed(2)],
                          backgroundColor: ["#fa541c", "#a0d911"],
                        },
                      ],
                    }}
                    />
                )}
              </Col>
            </Row>
          </Col>
        </Row>
      </div>
  );
}

export default LocusStatisticInfo;
