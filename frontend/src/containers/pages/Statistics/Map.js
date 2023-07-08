import React, {useState} from "react";
import {Col, Popover, Radio, Row, Select, Spin, Table} from "antd";
import MapMarker from "./MapMarker";
import {ComposableMap, Geographies, Geography, Marker, ZoomableGroup} from "react-simple-maps";
import {useQuery} from "react-query";
import {StatisticApi} from "../../../_apis"
import {feature} from "topojson-client";
import ColorUtils from "../../../_utils/ColorUtils";
import CalculationUtils from "../../../_utils/CalculationUtils";

const {Option} = Select;
const {generateUniqueColor} = ColorUtils;
const {countMapData, countItemAndCount, countByRegion} = CalculationUtils;
const {fetchLociData, fetchDefaultMapData, fetchMapData, fetchGeography} = StatisticApi;

function MapStats() {
  const [curLocus, setCurLocus] = useState("CSF1PO");
  const [currentChromosome, setCurrentChromosome] = useState("Autosome");
  const {data: locusList} = useQuery('loci', fetchLociData, {
    initialData: {
      Autosome: [],
      X: [],
      Y: []
    }
  });

  const {data: defaultMap} = useQuery('defaultMap', fetchDefaultMapData, {
    initialData: {
      "default_center_map_scale": "2000",
      "default_center_map_longitude": "100",
      "default_center_map_latitude": "12.8",
      "external_statistic_map_url": "",
      "is_external_map_url": "false",
      "internal_statistic_map_url": "/admins/maps",
    }
  });
  const {data: geographyMapData} = useQuery(["fetchMapData", defaultMap], fetchGeography, {
    initialData: [],
    keepPreviousData: true
  })
  const {isFetching: dataMapLoading, data} = useQuery(['dataMap', curLocus], fetchMapData, {
    initialData: [],
    keepPreviousData: true
  });

  const {
    default_center_map_longitude: longitude,
    default_center_map_latitude: latitude,
    default_map_scale: scale,
  } = defaultMap;
  const onChangeCurrentList = (e) => {
    setCurLocus(e.target.value);
  };

  const regionList = data.map(({province}) => province.region.region);
  const uniqueColor = generateUniqueColor(regionList);
  const uniqueCount = countMapData(data);
  const counterByRegion = countByRegion(data);

  const currentLocusList = locusList[currentChromosome].map((locus) => (
      <Row key={locus} type="flex" justify="start" align="top">
        <Radio className="locus-radio" value={locus}>
          {locus}
        </Radio>
      </Row>
  ))

  return (
      <Row style={{marginTop: "20px", marginLeft: "20px", height: "100%", minHeight: "500px"}}>
        <Col span={6}>
          <Row style={{minWidth: "120px", maxWidth: "240px"}} justify="start" align="middle">
            <Select
                style={{width: "100%"}}
                value={currentChromosome}
                onChange={(value) => {
                  setCurLocus(locusList[value][0])
                  setCurrentChromosome(value)
                }}
            >
              <Option value="Autosome">Autosomal</Option>
              <Option value="X">X STRs</Option>
              <Option value="Y">Y STRs</Option>
            </Select>
          </Row>
          <Row style={{minWidth: "120px", maxWidth: "240px", marginTop: "10px"}} justify="center" align="middle">
            <Radio.Group onChange={onChangeCurrentList} value={curLocus}>
              {currentLocusList}
            </Radio.Group>
          </Row>
        </Col>
        <Col span={18}>
          <Row justify="space-around">
            {Object
                .entries(uniqueColor)
                .sort((a, b) => a[0].length - b[0].length)
                .map(([a, c]) => {
                      const content = (
                          <div>
                            <Table
                                columns={[
                                  {
                                    title: 'Allele',
                                    dataIndex: 'allele',
                                  },
                                  {
                                    title: 'Amount',
                                    dataIndex: 'amount',
                                  }
                                ]}
                                dataSource={counterByRegion[a]}
                                pagination={false}
                                size="small"
                            />
                          </div>
                      );

                      return (
                          <Popover
                              content={content}
                              title={`${a} (Total count: ${uniqueCount[a]})`}
                              key={`${a} (Total count: ${uniqueCount[a]})`}
                          >
                            <div key={c}>
                          <span
                              style={{
                                backgroundColor: c,
                                border: "2px solid #FFFFFF",
                                borderRadius: "5px",
                                cursor: "default"
                              }}>&nbsp;{uniqueCount[a]}&nbsp;</span> - {a}
                            </div>
                          </Popover>
                      );
                    }
                )}
          </Row>
          <Row>
            <Col span={24}>
              <Spin tip="Loading map data..." spinning={dataMapLoading}>
                <ComposableMap
                    projection="geoMercator"
                    projectionConfig={{
                      center: [longitude, latitude],
                      scale: scale,
                    }}>
                  <ZoomableGroup center={[longitude, latitude]}>
                    <Geographies parseGeographies={(geo) => {
                      if (geographyMapData.objects) {
                        return feature(geographyMapData, geographyMapData.objects[Object.keys(geographyMapData.objects)[0]]).features;
                      }
                      return geo;
                    }} geography="https://raw.githubusercontent.com/soncomqiq/map-topo-json/main/maps.json">
                      {({geographies}) =>
                          geographies.map((geo) => (
                              <Geography
                                  style={{
                                    default: {fill: "#ECEFF1", stroke: "#607D8B", strokeWidth: 0.75, outline: "none"},
                                    hover: {fill: "#FFFFFF", stroke: "#607D8B", strokeWidth: 0.75, outline: "none"},
                                    pressed: {fill: "#000000", stroke: "#607D8B", strokeWidth: 0.75, outline: "none"},
                                  }}
                                  key={geo.rsmKey}
                                  geography={geo}
                                  fill="#EAEAEC"
                                  stroke="#D6D6DA"
                              />
                          ))
                      }
                    </Geographies>
                    {data.map(({province, alleleAmountList}) => {
                      let eachTotal = Math.floor(Math.log10(countItemAndCount(alleleAmountList)) + 1);

                      return (
                          <Marker key={province.province} coordinates={[province.longitude, province.latitude]}>
                            <g
                                fill={uniqueColor[province.region.region]}
                                stroke="#000"
                                strokeWidth="1"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            >
                              <circle cx={eachTotal * 3 - 3} cy="-0.5"
                                      r={(5 + eachTotal)}/>
                              <MapMarker
                                  province={province}
                                  alleleAmountList={alleleAmountList}
                              />
                            </g>
                          </Marker>
                      );
                    })}
                  </ZoomableGroup>
                </ComposableMap>
              </Spin>
            </Col>
          </Row>
        </Col>
      </Row>
  );
}

export default React.memo(MapStats);