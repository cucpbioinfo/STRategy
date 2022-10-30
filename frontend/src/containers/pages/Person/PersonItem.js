import {Divider, Modal} from 'antd';
import React from 'react';
import {Link} from 'react-router-dom';
import {DeleteOutlined} from "@ant-design/icons";
import axios from '../../../_config/axios';
import "./PersonItem.css"

function PersonItem(props) {
  const {person, fetchPerson} = props;

  console.log({person})

  const onClickDelete = () => {
    const config = {
      title: <h4>Are you sure to permanently delete<br/><b>{person.firstname} {person.lastname}(ID: {person.id})</b> ?
      </h4>,
      okText: "Delete",
      okType: "danger",
      maskClosable: true,
      icon: <DeleteOutlined style={{color: "red"}}/>,
      onOk: () => {
        axios.delete(`/persons/${person.id}`)
            .then(() => {
              fetchPerson();
            });
      },
    };
    Modal.confirm(config);
  };

  return (
      <span>
      {person.samples.length !== 0 ?
          <Link to={`/samples/${person.samples[0].sampleId}/forenseq`}>View Sample</Link> : <>Sample Not Available</>}
        <Divider type="vertical"/>
      <Link to={`/persons/${person.id}`}>Edit</Link>
      <Divider type="vertical"/>
      <span className="link" onClick={onClickDelete}>Delete</span>
      <Modal
          title="Modal"
          onOk={() => this.handleOk(person.sampleId, person.sampleYear)}
          onCancel={() => this.handleCancel(person.sampleId)}
          okText="Delete"
          cancelText="Cancel"
      >
        <p>
          Are you sure to delete the whole data of Sample ID : {person.sampleId} / Sample Year :{" "}
          {person.sampleYear}
        </p>
      </Modal>
    </span>
  );
}

export default PersonItem;
