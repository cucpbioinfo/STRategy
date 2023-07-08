import React, {useRef, useState} from 'react'
import {Button, Col, Divider, Form, Input, Modal, Popconfirm, Row, Space, Table, Tag, Typography} from "antd";
import {useMutation, useQuery, useQueryClient} from "react-query";
import {AdminApi} from "../../../_apis"
import addHeaders from "../../hoc/addHeader";
import EditableCell from "../components/EditableCell/EditableCell";
import axios from "axios";
import {DeleteOutlined, SearchOutlined} from "@ant-design/icons";
import Highlighter from "react-highlight-words";
import ColorUtils from "../../../_utils/ColorUtils";

const {fetchAllUser, fetchAllStatus, fetchAllRoles} = AdminApi;

export function ManageAllUser(props) {
  const {userStatus} = props;

  const queryClient = useQueryClient()
  const [form] = Form.useForm();
  const [pageInfo, setPageInfo] = useState({size: 10, page: 0});
  const [editingKey, setEditingKey] = useState('');
  const [sorterObj, setSorterObj] = useState([]);
  const [filterObj, setFilterObj] = useState(userStatus ? {status: [userStatus]} : {});
  const [searchedColumn, setSearchedColumn] = useState("");
  const [searchText, setSearchText] = useState("")
  const refInput = useRef();
  const updateUserMutation = useMutation(userInfo => axios.patch(`/admins/users/${userInfo.id}`, userInfo), {
    onSuccess: () => {
      queryClient.invalidateQueries('allUser')
    }
  });
  const {
    isFetching,
    data: allUserData
  } = useQuery(["allUser", pageInfo, userStatus, sorterObj, filterObj], fetchAllUser, {
    keepPreviousData: true,
    initialData: {usersList: [], allUserSize: 0}
  })
  const {data: allStatus} = useQuery("status", fetchAllStatus, {
    keepPreviousData: true,
    initialData: []
  })
  const {data: allRoles} = useQuery("role", fetchAllRoles, {
    keepPreviousData: true,
    initialData: []
  })

  const isEditing = (record) => record.id === editingKey;

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
            <Button onClick={() => handleReset(clearFilters)} size="small" style={{width: 90}}>
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

  const edit = (record) => {
    form.setFieldsValue({
      username: '',
      email: '',
      status: '',
      password: '',
      ...record,
      roles: record.roles.map(role => role.id),
    });
    setEditingKey(record.id);
  };

  const cancel = () => {
    setEditingKey('');
  };

  const save = async (id) => {
    try {
      const row = await form.validateFields();
      const rowDto = {id, ...row, roles: row.roles.map(id => ({id}))};
      updateUserMutation.mutate(rowDto);
      setEditingKey('');
    } catch (errInfo) {
      console.log('Validate Failed:', errInfo);
    }
  };

  const deleteFn = ({id}) => {
    updateUserMutation.mutate({id, status: "DELETE"});
  };

  const onClickDelete = (user) => {
    const config = {
      title: <h4>Are you sure to permanently
        delete<br/><b>Username: {user.username}<br/>Email: {user.email}<br/>(ID: {user.id})</b> ?
      </h4>,
      okText: "Delete",
      okType: "danger",
      maskClosable: true,
      icon: <DeleteOutlined style={{color: "red"}}/>,
      onOk: () => {
        deleteFn(user)
      },
    };
    Modal.confirm(config);
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      sorter: {
        multiple: 1
      },
    },
    {
      title: 'Username',
      dataIndex: 'username',
      editable: true,
      sorter: {
        multiple: 1
      },
      ...getColumnSearchProps("username"),
    },
    {
      title: 'Email',
      dataIndex: 'email',
      editable: true,
      sorter: {
        multiple: 1
      },
      ...getColumnSearchProps("email"),
    },
    {
      title: 'Role',
      dataIndex: 'roles',
      editable: true,
      filters: [
        {
          text: 'ROLE_LAB_USER',
          value: 'ROLE_LAB_USER',
        },
        {
          text: 'ROLE_ADMIN',
          value: 'ROLE_ADMIN',
        },
      ],
      render: roles => (
          <>
            {roles.map(({name: role}) => {
              return (
                  <Tag color={ColorUtils.resolveRoleColor(role)} key={role}>
                    {role.toUpperCase()}
                  </Tag>
              );
            })}
          </>
      ),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      editable: true,
      sorter: {
        multiple: 1
      },
      filters: [
        {
          text: 'ACCEPT',
          value: 'ACCEPT',
        },
        {
          text: 'DELETE',
          value: 'DELETE',
        },
        {
          text: 'BLOCK',
          value: 'BLOCK',
        },
        {
          text: 'NOT_ACCEPT',
          value: 'NOT_ACCEPT',
        },
      ],
      defaultFilteredValue: userStatus ? [userStatus] : [],
      render: (_, {status}) => {
        return (
            <Tag color={ColorUtils.resolveStatusColor(status)} key={status}>
              {status.toUpperCase()}
            </Tag>
        );
      }
    },
    {
      title: 'Password',
      dataIndex: "password",
      editable: true,
      render: (_, record) => <Button onClick={() => edit(record)} type="dashed">Reset</Button>
    },
    {
      title: 'Action',
      render: (_, record) => {
        const editable = isEditing(record);
        return editable ? (
            <span>
            <div
                className="link"
                onClick={() => save(record.id)}
                style={{
                  marginRight: 8,
                }}
            >
              Save
            </div>
            <Popconfirm title="Sure to cancel?" onConfirm={cancel}>
              <div className="link">Cancel</div>
            </Popconfirm>
          </span>
        ) : (
            <span>
            <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
              Edit
            </Typography.Link>
              <Divider type="vertical"/>
            <Typography.Link disabled={editingKey !== ''} onClick={() => onClickDelete(record)}>
              Delete
            </Typography.Link>
            </span>
        );
      },
    },
  ];

  const mergedColumns = columns.map((col) => {
    if (!col.editable) {
      return col;
    }

    return {
      ...col,
      onCell: (record) => ({
        record,
        allStatus,
        allRoles,
        dataIndex: col.dataIndex,
        title: col.title,
        editing: isEditing(record),
      }),
    };
  });

  const {usersList, allUserSize} = allUserData;

  return (
      <Row align="middle" justify="center">
        <Col span={22}>
          <Form form={form} component={false}>
            <Table
                rowClassName="editable-row"
                components={{
                  body: {
                    cell: EditableCell,
                  },
                }}
                loading={isFetching}
                rowKey="id"
                size="small"
                columns={mergedColumns}
                dataSource={usersList}
                pagination={{
                  pageSize: pageInfo.size,
                  current: pageInfo.page + 1,
                  total: allUserSize,
                  showSizeChanger: true
                }}
                onChange={({current: page, pageSize: size}, filter, sorter) => {
                  setFilterObj(filter);
                  setPageInfo({size, page: page - 1});
                  setSorterObj(sorter)
                  cancel()
                }}
            />
          </Form>
        </Col>
      </Row>
  )
}

export default addHeaders(ManageAllUser, "Manage all users");
