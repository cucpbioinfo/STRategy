import {Col, Row, Table, Tag} from 'antd';
import axios from 'axios';
import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import addHeaders from '../../hoc/addHeader';

function Dashboard() {
  const [sumData, setSumData] = useState([]);
  const columns = [
    {
      title: 'Kit',
      dataIndex: 'kit',
    },
    {
      title: 'Loci',
      render: (text, {loci}) => <div>{loci.map(locus => <Tag key={locus}><Link
          to={`/stats/graph?locus=${locus}`}>{locus}</Link></Tag>)}</div>
    },
    {
      title: 'Number of samples',
      dataIndex: 'numberOfSamples'
    },
  ];

  useEffect(() => {
    axios.get("/summaries/dashboard").then(({data}) => {
      const sData = [];
      for (let kit of Object.keys(data)) {
        const {loci, numberOfSamples} = data[kit];
        sData.push({
          kit: kit,
          loci,
          numberOfSamples,
        });
      }
      setSumData(sData);
    });
  }, []);

  return (
      <Row justify="center">
        <Col xs={24} sm={22}>
          <Table rowKey="kit" dataSource={sumData} columns={columns}/>
        </Col>
      </Row>
  );
}

export default addHeaders(Dashboard, "Summary Data Dashboard");
