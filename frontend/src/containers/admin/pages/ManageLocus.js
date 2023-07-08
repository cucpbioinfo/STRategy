import React, {useState} from 'react'
import addHeaders from "../../hoc/addHeader";
import {Button, Col, Collapse, Input, Radio, Row} from "antd";
import {useMutation, useQuery, useQueryClient} from "react-query";
import {AdminApi} from "../../../_apis"
import axios from "axios";
import PanelLocusEdit from "../components/PanelEdit/PanelLocusEdit";

const {fetchAllKits} = AdminApi;

export function ManageLocus() {
  const queryClient = useQueryClient()
  const [curChromosome, setCurChromosome] = useState("a");
  const [kitField, setKitField] = useState("");
  const newKitMutation = useMutation("newKit", newKit => axios.post("/admins/kits", newKit), {
    onSuccess: () => {
      queryClient.invalidateQueries('allKits')
    }
  });
  const {data: allKitsData} = useQuery(["allKits", curChromosome], fetchAllKits, {
    keepPreviousData: true,
    initialData: []
  });

  const addNewKit = () => {
    setKitField("");
    newKitMutation.mutate({kit: kitField, chromosomeType: curChromosome})
  }

  return (
      <Row align="middle" justify="center">
        <Col span={22}>
          <Row justify="center" align="middle">
            <Radio.Group value={curChromosome} onChange={(e) => setCurChromosome(e.target.value)}>
              <Radio.Button value="a">Autosomal chromosome</Radio.Button>
              <Radio.Button value="x">X chromosome</Radio.Button>
              <Radio.Button value="y">Y chromosome</Radio.Button>
            </Radio.Group>
          </Row>
          <br/>
          <Row justify="center" align="middle">
            <Col xs={18} sm={16} md={12} lg={8} xl={6} xxl={5}>
              <Row justify="center" align="middle">
                <Col xs={24} sm={16} lg={14} xxl={12}>
                  <Input value={kitField} onChange={(e) => setKitField(e.target.value)}/>
                </Col>
                <Col xs={24} sm={8} lg={10} xxl={12}>
                  <Button style={{width: "100%"}} onClick={() => addNewKit()}>Add a new kit</Button>
                </Col>
              </Row>
            </Col>
          </Row>
          <br/>
          <Row justify="center" align="middle" style={{textAlign: "start"}}>
            <Collapse
                accordion
            >
              {allKitsData.map(({kit, loci, id}) => (
                  <PanelLocusEdit key={id} kit={kit} loci={loci} id={id}/>
              ))}
            </Collapse>
          </Row>
        </Col>
      </Row>
  )
}

export default addHeaders(ManageLocus, "Manage kits and loci");
