import React, {useCallback, useEffect, useRef, useState} from "react";
import {Button, Col, Input, Row, Space, Table} from "antd";
import axios from "../../../_config/axios";
import PersonItem from "./PersonItem";
import addHeaders from "../../hoc/addHeader";
import NumberUtils from "../../../_utils/NumberUtils";
import StringUtils from "../../../_utils/StringUtils";
import Highlighter from 'react-highlight-words';
import {SearchOutlined} from '@ant-design/icons';


const {resolveSorter, resolveFilter} = StringUtils;

function PersonList() {
  const [personList, setPersonList] = useState([]);
  const [pageInfo, setPageInfo] = useState({curPage: 1, pageSize: 10});
  const [totalItem, setTotalItem] = useState(0);
  const [loading, setLoading] = useState(true);
  const [sorterObj, setSorterObj] = useState([]);
  const [filterObj, setFilterObj] = useState({});
  const [searchedColumn, setSearchedColumn] = useState("");
  const [searchText, setSearchText] = useState("")
  const refInput = useRef();

  const onChange = (pagination, filters, sorter, extra) => {
    setSorterObj(sorter);
    setFilterObj(filters);
    setPageInfo({curPage: pagination.current, pageSize: pagination.pageSize});
  };

  const fetchPerson = useCallback(() => {
    const {curPage, pageSize} = pageInfo;
    let url = `/persons/?page=${curPage - 1}&size=${pageSize}`;
    url = resolveSorter(sorterObj, url);
    url = resolveFilter(filterObj, url);
    axios.get(url).then(({data: {person_list, number_of_person}}) => {
      setPersonList(person_list);
      setTotalItem(number_of_person);
      setLoading(false);
    });
  }, [pageInfo, sorterObj, filterObj]);

  useEffect(() => {
    fetchPerson();
  }, [pageInfo, sorterObj, fetchPerson]);

  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm();
    setSearchText(selectedKeys[0]);
    setSearchedColumn(dataIndex);
  };

  const handleReset = clearFilters => {
    clearFilters();
    setSearchText("");
  };

  const getColumnSearchProps = dataIndex => ({
    filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) => (
        <div style={{padding: 8}}>
          <Input
              ref={refInput}
              placeholder={`Search ${dataIndex}`}
              value={selectedKeys[0]}
              onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
              onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex)}
              style={{width: 188, marginBottom: 8, display: 'block'}}
          />
          <Space>
            <Button
                type="primary"
                onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
                icon={<SearchOutlined/>}
                size="small"
                style={{width: 90}}
            >
              Search
            </Button>
            <Button
                onClick={() => handleReset(clearFilters)}
                size="small"
                style={{width: 90}}>
              Reset
            </Button>
            <Button
                type="link"
                size="small"
                onClick={() => {
                  confirm({closeDropdown: false});
                  setSearchText(selectedKeys[0]);
                  setSearchedColumn(dataIndex);
                }}
            >
              Filter
            </Button>
          </Space>
        </div>
    ),
    filterIcon: filtered => <SearchOutlined style={{color: filtered ? '#1890ff' : undefined}}/>,
    onFilterDropdownVisibleChange: visible => {
      if (visible) {
        setTimeout(() => refInput.current.select(), 100);
      }
    },
    render: text =>
        searchedColumn === dataIndex ? (
            <Highlighter
                highlightStyle={{backgroundColor: '#ffc069', padding: 0}}
                searchWords={[searchText]}
                autoEscape
                textToHighlight={text ? text.toString() : ''}
            />
        ) : (
            text
        ),
  });

  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      align: "center",
      sorter: {
        multiple: 1,
      },
    },
    {
      title: "Gender",
      dataIndex: "gender",
      key: "gender",
      align: "center",
      sorter: {
        multiple: 1,
      },
      filters: [
        {
          text: 'MALE',
          value: 'MALE',
        },
        {
          text: 'FEMALE',
          value: 'FEMALE',
        },
      ],
    },
    {
      title: "Race",
      dataIndex: "race.race",
      key: "race",
      align: "center",
      sorter: {
        multiple: 1,
      },
      ...getColumnSearchProps("race"),
      render: (text, {race}) =>
          searchedColumn === "race" ? (
              <Highlighter
                  highlightStyle={{backgroundColor: '#ffc069', padding: 0}}
                  searchWords={[searchText]}
                  autoEscape
                  textToHighlight={race?.race ? race?.race.toString() : ''}
              />
          ) : (
              race?.race
          )
    },
    {
      title: "Country",
      dataIndex: "country.country",
      key: "country",
      align: "center",
      sorter: {
        multiple: 1,
      },
      ...getColumnSearchProps("country"),
      render: (text, {country}) =>
          searchedColumn === "country" ? (
              <Highlighter
                  highlightStyle={{backgroundColor: '#ffc069', padding: 0}}
                  searchWords={[searchText]}
                  autoEscape
                  textToHighlight={country?.country ? country?.country.toString() : ''}
              />
          ) : (
              country?.country
          )
    },
    {
      title: "Province",
      dataIndex: "province.province",
      key: "province",
      align: "center",
      sorter: {
        multiple: 1,
      },
      ...getColumnSearchProps("province"),
      render: (text, {province}) =>
          searchedColumn === "province" ? (
              <Highlighter
                  highlightStyle={{backgroundColor: '#ffc069', padding: 0}}
                  searchWords={[searchText]}
                  autoEscape
                  textToHighlight={province?.province ? province?.province.toString() : ''}
              />
          ) : (
              province?.province
          )
    },
    {
      title: "Action",
      key: "action",
      align: "center",
      render: (text, person) => (
          <PersonItem fetchPerson={fetchPerson} person={person}/>
      ),
    },
  ];

  return (
      <Row align="center">
        <Col xs={22}>
          <Table
              rowKey={() => NumberUtils.uniqueId()}
              loading={loading}
              columns={columns}
              dataSource={personList}
              onChange={onChange}
              bordered
              size="small"
              pagination={{
                current: pageInfo.curPage,
                pageSize: pageInfo.pageSize,
                total: totalItem,
                showSizeChanger: true
              }}
          />
        </Col>
      </Row>
  );
}

export default addHeaders(PersonList, "Person List");
