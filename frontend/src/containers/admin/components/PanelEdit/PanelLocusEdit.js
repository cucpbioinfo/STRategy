import React, {useState} from "react";
import NewLocusInput from "../NewInput/NewLocusInput";
import {Button, Collapse, Divider, Input, List, Popconfirm, Typography} from "antd";
import LocusEdit from "../LocusEdit/LocusEdit";
import {useMutation, useQueryClient} from "react-query";
import axios from "axios";

const {Panel} = Collapse;

export function PanelLocusEdit(props) {
  const {id, kit, loci} = props;
  const queryClient = useQueryClient()

  const [isEdit, setIsEdit] = useState(false);
  const [editValue, setEditValue] = useState("");

  const deleteKitMutation = useMutation("deleteKit", kitId => axios.delete(`/admins/kits/${kitId}`), {
    onSuccess: () => {
      queryClient.invalidateQueries("allKits")
    }
  })
  const updateKitMutation = useMutation("updateKit", kit => axios.put(`/admins/kits/${kit.id}`, kit), {
    onSuccess: () => {
      queryClient.invalidateQueries("allKits")
    }
  })

  const deleteKitById = (kitId) => {
    deleteKitMutation.mutate(kitId);
  }

  const extraPanel = (id, kit) => {
    return isEdit ? (
        <div>
          <Button onClick={(e) => {
            e.stopPropagation();
            setIsEdit(false);
            updateKitMutation.mutate({id, kit: editValue})
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
                setEditValue(kit);
              }}
          >Edit</Typography.Link>
          <Divider type="vertical"/>
          <Popconfirm
              placement="topRight"
              title={`Do you want to delete Kit: ${kit}`}
              onConfirm={(e) => {
                e.stopPropagation();
                deleteKitById(id)
              }}
              onCancel={(e) => e.stopPropagation()}
              okText="Yes"
              cancelText="No"
          >
            <Typography.Link onClick={(e) => {
              e.stopPropagation();
            }}>Delete</Typography.Link>
          </Popconfirm>
        </div>
    )
  }


  return (
      <Panel
          {...props}
          header={isEdit ? <Input value={editValue} onChange={(e) => setEditValue(e.target.value)}/> : `Kit: ${kit}`}
          key={kit}
          extra={extraPanel(id, kit)}
      >
        <NewLocusInput kitName={kit} kitId={id}/>
        <List
            dataSource={loci}
            renderItem={({id: locusId, locus}) => (
                <LocusEdit locusId={locusId} locus={locus}/>
            )}
        />
      </Panel>
  )
}

export default PanelLocusEdit;