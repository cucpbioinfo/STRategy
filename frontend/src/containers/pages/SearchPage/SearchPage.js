import React, {useState} from "react";
import {Col, Radio, Row} from "antd";
import "./SearchPage.css";
import ExcelSearch from "../../../components/searches/excel-search/ExcelSearch";
import TextSearch from "../../../components/searches/text-search/TextSearch";
import FormSearch from "../../../components/searches/form-search/FormSearch";
import addHeaders from "../../hoc/addHeader";

function SearchPage() {
  const [searchMeth, setSearchMeth] = useState(1);
  let searchElement;

  if (searchMeth === 0) {
    searchElement = <ExcelSearch/>;
  } else if (searchMeth === 1) {
    searchElement = <TextSearch/>;
  } else {
    searchElement = <FormSearch/>;
  }

  return (
      <Row justify="center">
        <Col span={24}>
          <div style={{margin: "0 0 25px 0"}}>
            <Radio.Group
                onChange={(e) => setSearchMeth(e.target.value)}
                defaultValue={1}
            >
              <Radio.Button value={0}>Excel</Radio.Button>
              <Radio.Button value={1}>Text</Radio.Button>
              <Radio.Button value={2}>Manual</Radio.Button>
            </Radio.Group>
          </div>
          <div>
            <div>
              {searchElement}
            </div>
          </div>
        </Col>
      </Row>
  );
}

export default addHeaders(SearchPage, "Search for samples");