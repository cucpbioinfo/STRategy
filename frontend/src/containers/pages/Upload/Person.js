import {Col, Row, Table} from "antd";
import React from "react";
import DraggerUpload from "../../../components/upload/DraggerUpload";

function Person() {
  const columns = [
    {
      title: 'Column',
      dataIndex: 'column',
      align: 'center'
    },
    {
      title: 'Field',
      dataIndex: 'field',
      align: 'center'
    },
    {
      title: 'Data type',
      dataIndex: 'type',
      align: 'center'
    },
    {
      title: 'Note',
      dataIndex: 'note',
      align: 'center'
    },
  ];
  const dataSource = [
    {
      column: '1',
      field: 'Gender',
      type: "String [ \"MALE\" OR \"FEMALE\" ]",
      note: "-"
    },
    {
      column: '2',
      field: 'Province',
      type: "String",
      note: "case in-sensitive"
    },
    {
      column: '3',
      field: 'Region',
      type: "String",
      note: "case in-sensitive"
    },
    {
      column: '4',
      field: 'Country',
      type: "String",
      note: "case in-sensitive"
    },
    {
      column: '5',
      field: 'Race',
      type: "String",
      note: "case in-sensitive"
    },
    {
      column: '6',
      field: 'SampleId',
      type: "String",
      note: "case in-sensitive",
    },
  ];

  return <>
    <Row justify="center">
      <Col xs={20}>
        <DraggerUpload
            infoMessage="If you don't know how to set up an excel file, please see our example"
            exampleUrl="/files/example-person"
            exampleFileName="example-person.xlsx"
            uploadUrl="/files/person"
            uploadTopic="Upload Person Data"/>
      </Col>
    </Row>
    <Row justify="center">
      <Col xs={20}>
        <br/>
        <h3>File Format - Each record within the file contains the following columns.</h3>
        <Table rowKey="column" dataSource={dataSource} columns={columns} pagination={false}/>
        <br/>
      </Col>
    </Row>
  </>;
}

export default Person;
