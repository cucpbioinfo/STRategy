import React, {useState} from "react";
import {Button, Input, List, Popconfirm, Typography} from "antd";
import {useMutation, useQueryClient} from "react-query";
import axios from "axios";

export function LocusEdit(props) {
  const queryClient = useQueryClient();
  const {locusId, locus} = props;
  const [locusValue, setLocusValue] = useState("");
  const [isEdit, setIsEdit] = useState(false);
  const deleteLocusMutation = useMutation("deleteLocus", locusId => axios.delete(`/admins/loci/${locusId}`), {
    onSuccess: () => {
      queryClient.invalidateQueries("allKits")
    }
  })
  const updateLocusMutation = useMutation("updateLocus", locus => axios.put(`/admins/loci/${locus.id}`, locus), {
    onSuccess: () => {
      queryClient.invalidateQueries("allKits")
    }
  })

  const updateLocus = () => {
    updateLocusMutation.mutate({id: locusId, locus: locusValue});
  }

  return (
      <List.Item
          actions={[
            isEdit ? (
                <Button
                    key="edit"
                    onClick={(e) => {
                      setIsEdit(false);
                      updateLocus();
                    }}
                >Done</Button>
            ) : (
                <Typography.Link
                    key="edit"
                    onClick={(e) => {
                      setIsEdit(true);
                      setLocusValue(locus);
                    }}
                >Edit</Typography.Link>
            ),
            isEdit ? (
                <Button
                    key="cancel"
                    onClick={(e) => {
                      setIsEdit(false);
                    }}
                >Cancel</Button>
            ) : (
                <Popconfirm
                    placement="topRight"
                    title={`Do you want to delete locus: ${locus}`}
                    onConfirm={(e) => {
                      e.stopPropagation();
                      deleteLocusMutation.mutate(locusId)
                    }}
                    onCancel={(e) => e.stopPropagation()}
                    okText="Yes"
                    cancelText="No"
                >
                  <Typography.Link key="delete">Delete</Typography.Link>
                </Popconfirm>
            )
          ]}
      >
        {isEdit ? (
                <Input
                    value={locusValue}
                    onChange={(e) => setLocusValue(e.target.value)}
                />
            ) :
            <div>Locus: {locus}</div>}
      </List.Item>
  )
}

export default LocusEdit;