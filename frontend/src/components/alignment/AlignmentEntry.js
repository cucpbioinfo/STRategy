import React from "react";
import {Col, Collapse, Row, Table, Tooltip} from "antd";
import NumberUtils from "../../_utils/NumberUtils"
import ColorUtils from "../../_utils/ColorUtils"
import PatternAlignmentUtils from "../../_utils/PatternAlignmentUtils"
import {ExportOutlined} from "@ant-design/icons";
import {dynamicPagesConfig} from "../../_config/roles";
import {Link} from "react-router-dom";

const {maskedAllele, generateAlphaColor} = ColorUtils;
const {resolvePatternAlignment} = PatternAlignmentUtils;
const {Panel} = Collapse;

const WrappedAlignmentEntry = ({data, setCurrentMenu, currentLocus, reverse = false}) => {
  const alphaColor = generateAlphaColor(data, reverse);

  const columns = [
    {
      title: "Sample Year",
      dataIndex: "sampleYear",
      key: "sampleYear",
    },
    {
      title: "Sample ID",
      dataIndex: "sampleId",
      key: "sampleId",
    },
    {
      title: "Sequence",
      render: (_, record) => (
          <div style={{wordBreak: "break-all"}}>
            {maskedAllele(record.sequence, record.repeatMotif, record.changingBase, alphaColor, reverse)}
          </div>
      ),
    },
    {
      title: "Read count",
      dataIndex: "readCount",
      key: "readCount",
    },
    {
      title: "STR repeat motifs",
      key: "repeatMotif",
      render: (_, record) => (
          <div style={{wordBreak: "break-all"}}>
            {resolvePatternAlignment(record.repeatMotif, reverse)}
          </div>
      ),
    }
  ];

  return (
      <div>
        <Row>
          <Col span={22}>
            <Collapse
                bordered={false}
                ghost
            >
              <Panel header="Color details" key="1">
                <Row justify="space-around">
                  {Object
                      .entries(alphaColor)
                      .sort((a, b) => a[0].length - b[0].length)
                      .map(([a, c]) => (
                              <span key={c}>
                            <span
                                style={{
                                  backgroundColor: c,
                                  border: "2px solid #FFFFFF",
                                  borderRadius: "5px",
                                  cursor: "default"
                                }}>&nbsp;&nbsp;&nbsp;&nbsp;</span> - {a}
                          </span>
                          )
                      )}
                </Row>
              </Panel>
            </Collapse>
          </Col>
          <Col span={2}>
            <Row style={{height: "100%"}} justify="end" align="middle">
              <Link
                  onClick={() => setCurrentMenu(dynamicPagesConfig.exportAsExcel.url)}
                  to={`/export/excel?loci=${currentLocus}`}>
                <Tooltip placement="topRight" title="Export pattern alignment">
                  <ExportOutlined/>
                </Tooltip>
              </Link>
            </Row>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <Table rowKey={() => NumberUtils.uniqueId()}
                   bordered
                   size="small"
                   columns={columns}
                   dataSource={data}
            />
          </Col>
        </Row>
      </div>
  )
}
export default WrappedAlignmentEntry;