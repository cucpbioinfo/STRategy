import React, {useEffect, useState} from "react";
import addHeaders from "../../hoc/addHeader";
import axios from "../../../_config/axios";
import {useHistory, useParams} from "react-router-dom";
import {Button, Col, Divider, Modal, notification, Row, Table} from "antd";
import {ExclamationCircleOutlined} from "@ant-design/icons";

export function ManageProvinces(props) {
  const {countryId} = useParams();
  const history = useHistory()
  const [country, setCountry] = useState("")
  const [loading, setLoading] = useState(false)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [provinces, setProvinces] = useState([]);

  const fetchCountryData = () => {
    axios.get("/countries/" + countryId)
        .then(res => {
          setCountry(res.data.country)
        })
        .catch(err => {
          console.log(err)
        })
  }

  const fetchProvincesData = () => {
    axios.get(`/countries/${countryId}/provinces`)
        .then(res => {
          setProvinces(res.data.sort((a, b) => a.id - b.id).map(e => ({...e, key: e.id})))
        })
        .catch(err => {
          console.log(err)
        })
  }

  useEffect(() => {
    fetchCountryData();
    fetchProvincesData();
  }, [])

  function showConfirm() {
    Modal.confirm({
      title: 'Do you want to delete these items?',
      icon: <ExclamationCircleOutlined/>,
      content: 'The selected provinces, as well as personal data belonging to the provinces, will be permanently erased.',
      onOk() {
        onDelete()
      },
      onCancel() {
        console.log('Cancel');
      },
    });
  }

  const onDelete = () => {
    setLoading(true);
    axios.put(`/countries/${countryId}/provinces`, {provinceToUpdateList: [], deleteProvinceIds: selectedRowKeys})
        .then(res => {
          fetchProvincesData()
          setLoading(false)
        })
        .catch(err => {
          console.log(err)
          notification.error({
            message: "Delete failed"
          })
        })
  };

  const onEdit = () => {
    setLoading(true);
    history.push(`/admin/${countryId}/edit-provinces?ids=${selectedRowKeys}`)
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
      align: 'center'
    },
    {
      title: 'Province',
      dataIndex: 'province',
    },
    {
      title: 'Native name',
      dataIndex: 'nativeName',
    },
    {
      title: 'Latitude',
      dataIndex: 'latitude',
    },
    {
      title: 'Longitude',
      dataIndex: 'longitude',
    },
    {
      title: 'Region',
      render: (record) => (
          <div>
            {record?.region?.region}
          </div>
      )
    },
  ];

  const onSelectChange = selectedRowKeys => {
    setSelectedRowKeys(selectedRowKeys)
  };

  const hasSelected = selectedRowKeys.length > 0;

  return (
      <div>
        {country} (ID: {countryId})
        <Row justify="start" style={{margin: 16}}>
          <Button type="dashed" onClick={onEdit} loading={loading}>
            Add bulk
          </Button>
          <Divider/>
          <Button type="primary" onClick={onEdit} disabled={!hasSelected} loading={loading}>
            Edit
          </Button>
          <Button type="danger" onClick={showConfirm} disabled={!hasSelected} loading={loading}>
            Delete
          </Button>
          <span style={{margin: "auto", marginLeft: 8}}>
            {hasSelected ? `Selected ${selectedRowKeys.length} items` : ''}
          </span>
        </Row>
        <Row justify="center" style={{margin: 16}}>
          <Col xs={24}>
            <Table
                rowSelection={{
                  onChange: onSelectChange,
                  selections: [
                    Table.SELECTION_ALL,
                    Table.SELECTION_INVERT,
                    Table.SELECTION_NONE,
                  ],
                  selectedRowKeys
                }}
                columns={columns}
                dataSource={provinces}/>
          </Col>
        </Row>
      </div>
  )
}

export default addHeaders(ManageProvinces, "Manage Provinces");