import React, {useState} from "react";
import {Button, Collapse, Divider, Input, List, Popconfirm, Typography} from "antd";
import NewInput from "../NewInput/NewInput";
import axios from "../../../../_config/axios";
import FieldEdit from "../LocusEdit/FieldEdit";
import {Link} from "react-router-dom";

const {Panel} = Collapse;
const {Text} = Typography;

export function PanelCountryEdit(props) {
  const {id, regions, country, fetchData} = props;
  const [isEdit, setIsEdit] = useState(false);
  const [editValue, setEditValue] = useState("");

  const extraPanel = (id, country) => {
    return isEdit ? (
        <div>
          <Button onClick={(e) => {
            e.stopPropagation();
            setIsEdit(false);
            axios.put("/countries/", {id, country: editValue})
                .then(res => {
                  fetchData()
                })
                .catch(err => {
                  console.log(err)
                })
          }}>Done</Button>
          <Divider type="vertical"/>
          <Button onClick={(e) => {
            e.stopPropagation();
            setIsEdit(false);
          }}>Cancel</Button>
        </div>
    ) : (
        <div style={{marginLeft: "25px"}}>
          <Typography.Link
              onClick={e => {
                e.stopPropagation()
                setIsEdit(true);
                setEditValue(country);
              }}
          >Edit</Typography.Link>
          <Divider type="vertical"/>
          <Popconfirm
              placement="topRight"
              title={`Do you want to delete Kit: ${country}`}
              onConfirm={(e) => {
                e.stopPropagation();
              }}
              onCancel={(e) => e.stopPropagation()}
              okText="Yes"
              cancelText="No"
          >
            <Typography.Link disabled={true} onClick={(e) => {
              e.stopPropagation();
            }}>Delete</Typography.Link>
          </Popconfirm>
        </div>
    )
  }

  const addNewRegion = (countryId, region) => {
    axios.post("/regions/" + countryId, {region})
        .then(res => {
          fetchData();
        })
        .catch(err => {
          console.log(err)
        })
  }

  const onUpdateRegion = (id, region) => {
    axios.put("/regions/", {id, region})
        .then(res => {
          fetchData();
        })
        .catch(err => {
          console.log(err)
        })
  }

  const onDeleteRegion = (id) => {
    axios.delete("/regions/" + id)
        .then(res => {
          fetchData();
        })
        .catch(err => {
          console.log(err)
        })
  }

  return (
      <Panel
          {...props}
          header={isEdit ?
              <Input value={editValue} onChange={(e) => setEditValue(e.target.value)}/> : `Country: ${country}`}
          key={country}
          extra={extraPanel(id, country)}
      >
        <NewInput onAddInput={addNewRegion} parentName={country} parentId={id} childName="Region"/>
        <List
            dataSource={regions}
            renderItem={({id: regionId, region}) => (
                <FieldEdit
                    onUpdateField={onUpdateRegion}
                    onDeleteField={onDeleteRegion}
                    fieldId={regionId}
                    confirmMessage={(fieldName) => <div>Do you want to delete region: {fieldName}?
                      <br/>
                      <Text type="danger">
                        [Warning] All provinces and person data
                        <br/>
                        that belong to region: {fieldName}
                        <br/>
                        and country: {country} will be deleted
                      </Text>
                    </div>}
                    fieldName={region}
                    fieldDisplay="Region"/>
            )}
        />
        {regions.length === 0 ? (
            <Button disabled={true} type="link" block>
              Please add more region to manage provinces
            </Button>
        ) : (
            <Link to={`/admin/${id}/manage-provinces`}>
              <Button type="link" block>
                Manage provinces
              </Button>
            </Link>
        )}
      </Panel>
  )
}

export default PanelCountryEdit;