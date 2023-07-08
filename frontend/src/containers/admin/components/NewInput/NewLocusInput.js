import React, {useState} from "react";
import {Button, Col, Input, Row} from "antd";
import {useMutation, useQueryClient} from "react-query";
import axios from "axios";

export function NewLocusInput(props) {
  const {kitId, kitName} = props;
  const [inputValue, setInputValue] = useState("");
  const queryClient = useQueryClient()
  const newLocusMutation = useMutation(["newLocus", kitId], locus => axios.post(`/admins/kits/${locus.kitId}/loci`, locus), {
    onSuccess: () => {
      queryClient.invalidateQueries("allKits")
    }
  })

  const addNewLocus = () => {
    newLocusMutation.mutate({kitId, locus: inputValue});
    setInputValue("");
  }

  return (
      <Row justify="center" align="middle">
        <Col span={18}>
          <Input
              placeholder={`New locus for ${kitName}`}
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}/>
        </Col>
        <Col span={6}>
          <Button style={{width: "100%"}} onClick={() => addNewLocus()}>Add</Button>
        </Col>
      </Row>
  )
}

export default NewLocusInput;