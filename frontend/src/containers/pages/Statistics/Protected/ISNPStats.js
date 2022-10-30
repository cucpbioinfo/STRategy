import {AutoComplete, Col, Divider, Empty, Input, Pagination, Popover, Row, Typography,} from "antd";
import React, {useEffect, useState} from "react";
import axios from "../../../../_config/axios";
import ColorUtils from "../../../../_utils/ColorUtils";
import addHeaders from "../../../hoc/addHeader";
import "./ISNPStats.css";
import TitleISNPTable from "./TitleISNPTable";

const {Text} = Typography;
const {Search} = Input;

function ISNPStats() {
  const [snpSummary, setSnpSummaryList] = useState([]);
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [pageInfo, setPageInfo] = useState({curPage: 1, pageSize: 10});
  const [allLocus, setAllLocus] = useState([]);
  const [totalItem, setTotalItem] = useState(0);

  useEffect(() => {
    setLoading(true);
    axios
        .get(
            `/forenseq-sequences/isnp?query=${query.trim()}&page=${pageInfo.curPage - 1
            }&size=${pageInfo.pageSize}`
        )
        .then(({data: {isnp_list: isnpList, total_entities: total}}) => {
          setLoading(false);
          setTotalItem(total);
          setSnpSummaryList(isnpList);
        });
  }, [query, pageInfo]);

  const onChange = (page, pageSize) => {
    setPageInfo({curPage: page, pageSize});
  };

  useEffect(() => {
    axios.get("/loci/isnp")
        .then((res) => {
          setAllLocus(res.data);
        })
  }, [])

  return (
      <div>
        <div>
          <div align="center">
            <Row justify="center">
              <Col xs={16} sm={12} md={10} lg={8} xl={6} xxl={4}>
                <TitleISNPTable/>
              </Col>
            </Row>
            <Divider/>
            <AutoComplete
                options={allLocus.filter(e => e.includes(query) && query).map(e => ({value: e}))}
                onSearch={(value) => setQuery(value)}
            >
              <Search
                  loading={loading}
                  placeholder="rs876724"
                  style={{width: 200}}
                  enterButton
              />
            </AutoComplete>
          </div>
        </div>
        <br/>
        {snpSummary.length === 0 ? (
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
        ) : (
            <div>
              {snpSummary.map((entry, idx) => (
                  <Row key={idx} justify="center">
                    <Col span={22}>
                      <Row>
                        <Col span={4}>
                          <Divider orientation="left">
                            Locus: <Text keyboard><a target="_blank" rel="noreferrer"
                                                     href={`https://www.ncbi.nlm.nih.gov/snp/?term=${entry.locus}`}>{entry.locus}</a></Text>
                          </Divider>
                        </Col>
                        <Col span={16}>
                          <Row style={{height: "100%"}} align="middle">
                            {entry.details.map(({genotype, amount}) => {
                              const percent = (amount / entry.total) * 100;
                              const content = <div>{genotype} ({percent.toFixed(2)} %)</div>;
                              return (
                                  <Popover content={content}>
                                    <p
                                        className="label-color"
                                        style={{
                                          background: ColorUtils.iSNPColor[genotype],
                                          width: percent + "%",
                                          height: 40,
                                          borderRadius: "5px",
                                          border: "1px solid #C5C5C5",
                                        }}>
                                      <Row justify="center" align="middle"
                                           style={{
                                             height: "100%",
                                             textOverflow: "ellipsis",
                                             whiteSpace: "nowrap",
                                             overflowX: "hidden"
                                           }}>
                                        <Text style={{cursor: "default"}}>{genotype}</Text>
                                      </Row>
                                    </p>
                                  </Popover>
                              );
                            })}
                          </Row>
                        </Col>
                        <Col span={3}>
                          <Row style={{height: "100%"}} justify="center" align="middle">
                            <Divider orientation="right">
                              <h3>N = {entry.total}</h3>
                            </Divider>
                          </Row>
                        </Col>
                      </Row>
                    </Col>
                  </Row>
              ))}
              <Row justify="end" style={{padding: "20px"}}>
                <Pagination
                    pageSize={pageInfo.pageSize}
                    current={pageInfo.curPage}
                    total={totalItem}
                    onChange={onChange}
                    showSizeChanger
                />
              </Row>
            </div>
        )}
      </div>
  );
}

export default addHeaders(ISNPStats, "iSNP Statistic Summary");