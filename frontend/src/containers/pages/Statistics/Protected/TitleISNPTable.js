import {Col, Row} from 'antd';
import ColorUtils from "../../../../_utils/ColorUtils";
import React from 'react';

function TitleISNPTable() {
  const color = ColorUtils.iSNPColor;

  const arr = [
    [[" ", " "], ["A", color["A"]], ["T", color["T"]], ["C", color["C"]], ["G", color["G"]]],
    [["A", color["A"]], ["A,A", color["A,A"]], ["A,T", color["A,T"]], ["A,C", color["A,C"]], ["A,G", color["A,G"]]],
    [["T", color["T"]], ["T,A", color["T,A"]], ["T,T", color["T,T"]], ["T,C", color["T,C"]], ["T,G", color["T,G"]]],
    [["C", color["C"]], ["C,A", color["C,A"]], ["C,T", color["C,T"]], ["C,C", color["C,C"]], ["C,G", color["C,G"]]],
    [["G", color["G"]], ["G,A", color["G,A"]], ["G,T", color["G,T"]], ["G,C", color["G,C"]], ["G,G", color["G,G"]]],
  ];

  return (
      <>
        {arr.map((row) => (
            <Row justify="center">
              {row.map(([content, color]) => (
                  <Col span={4}>
                    <strong style={{background: color, margin: "5px"}}>
                      &nbsp;&nbsp;{content}&nbsp;&nbsp;
                    </strong>
                  </Col>
              ))}
            </Row>
        ))}
      </>
  );
}

export default TitleISNPTable;
