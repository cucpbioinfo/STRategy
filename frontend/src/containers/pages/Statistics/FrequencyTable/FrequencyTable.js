import {Col, Row, Select, Table} from 'antd';
import axios from '../../../../_config/axios';
import React, {useEffect, useState} from 'react';
import ColorUtils from '../../../../_utils/ColorUtils';
import addHeaders from '../../../hoc/addHeader';
import TableElement from './TableElement';
import "./Frequency.css";

const {Option} = Select;

function FreqTable() {
  const [locusList, setLocusList] = useState([]);
  const [dataRes, setDataRes] = useState({});

  const fetchLoci = () => {
    axios.get("/loci/global")
        .then(res => {
          setLocusList(res.data);
        });
  };

  const fetchData = (locus) => {
    axios.get(`/summaries/locus/${locus}`)
        .then(res => {
          setDataRes(res.data);
        });
  };

  useEffect(() => {
    fetchLoci();
  }, []);

  const handleChange = value => {
    fetchData(value);
  };

  const generateColumns = (countriesList) => {
    countriesList.sort();
    const idx = countriesList.findIndex(e => e === "Local Database");
    countriesList.splice(idx, 1)
    const columns = countriesList?.map(country => ({
      title: <div className="freq-table-title">
        <div style={{
          transform: "rotate(270deg)",
          width: "30px",
          transformOrigin: "right bottom",
          whiteSpace: "nowrap"
        }}>{country}</div>
      </div>,
      dataIndex: country,
      align: "center",
      width: 30,
      render: (value, record) => {
        const color = ColorUtils.colorForFreqTable(value);
        return <TableElement value={value} color={color}/>;
      }
    }));

    columns?.unshift({
      title: <div className="freq-table-title">
        <div style={{
          transform: "rotate(270deg)",
          width: "30px",
          transformOrigin: "right bottom",
          whiteSpace: "nowrap"
        }}>Local Database
        </div>
      </div>,
      dataIndex: "Local Database",
      align: "center",
      width: 30,
      render: (value, record) => {
        const color = ColorUtils.colorForFreqTable(value);
        return <TableElement value={value} color={color}/>;
      }
    });

    columns?.unshift({
      title: "Allele",
      dataIndex: "allele",
      align: "center",
      fixed: 'left',
      width: 60,
    });

    return columns;
  };

  return (
      <>
        <Row justify="center" align="middle">
          <div style={{margin: "0 1rem"}}>Current Locus:</div>
          <Select defaultValue="Please Select Locus" style={{width: 240}} onChange={handleChange}>
            {locusList.map(locus => <Option key={locus} value={locus}>{locus}</Option>)}
          </Select>
        </Row>
        {dataRes.countryCountList?.length && <Row justify="center" align="middle">
          Min
          <div style={{
            borderRadius: "10px",
            width: "50%",
            height: "1rem",
            margin: "1rem 1rem",
            background: "linear-gradient(270deg, rgba(0,0,0,1) 0%, rgba(97,60,27,1) 25%, rgba(249,65,21,1) 50%, rgba(255,90,6,1) 75%, rgba(255,138,0,1) 100%)"
          }}/>
          Max
        </Row>}
        <Row justify="center">
          <Col xs={24} sm={22}>
            <Row justify="center">
              {dataRes.countryCountList?.length && <Table
                  scroll={{x: "max-content"}}
                  style={{width: "fit-content"}}
                  rowKey="allele"
                  dataSource={dataRes.countryCountList.sort((a,b) => a.allele - b.allele)}
                  columns={generateColumns(dataRes.countriesMap && Object.keys(dataRes.countriesMap))}
                  pagination={false}
                  size="small"
              />}
            </Row>
          </Col>
        </Row>
      </>
  );
}

export default addHeaders(FreqTable, "Frequency Table");
