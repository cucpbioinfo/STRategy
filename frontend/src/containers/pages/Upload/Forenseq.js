import React, {useState} from "react";
import {Alert, Button, Col, List, notification, Row, Typography, Upload} from "antd";
import axios from "../../../_config/axios";
import {InboxOutlined} from "@ant-design/icons";
import XLSX from 'xlsx';
import NumberUtils from "../../../_utils/NumberUtils";
import BrowserUtils from "../../../_utils/BrowserUtils";
import {useHistory} from "react-router-dom";
import Checkbox from "antd/es/checkbox/Checkbox";
import ExcelUtils from "../../../_utils/ExcelUtils";

const {uniqueId} = NumberUtils;
const {useCookie} = BrowserUtils;

function Forenseq(props) {
  const [uploadFileList, setUploadFileList] = useState([]);
  const [duplicateFileList, setDuplicateFileList] = useState([]);
  const [isNotice, setIsNotice] = useState(false);
  const [isNoticeLongClose, setIsNoticeLongClose] = useState(false);
  const [noticeCookie, updateCookie] = useCookie("notice-upload", "true");
  const history = useHistory()

  const {Dragger} = Upload;

  const customRequest = async ({onSuccess, onError, file}) => {
    const formData = new FormData();
    let reader = new FileReader();
    reader.onload = function (e) {
      try {
        const data = e.target.result;

        /* if binary string, read with type 'binary' */
        const workbook = XLSX.read(data, {type: 'binary'});
        /* DO SOMETHING WITH workbook HERE */
        const first_sheet_name = workbook.SheetNames[0];

        /* Get worksheet */
        const worksheet = workbook.Sheets[first_sheet_name];
        const sampleId = XLSX.utils.sheet_to_json(worksheet, {header: 1})[2][1];
        let resultFile = file
        if (file.name.endsWith(".xlsb")) {
          resultFile = ExcelUtils.convertToXlsx(workbook, file);
        }
        axios.get(`/admins/samples/existing?ids=${sampleId}`)
            .then(async ({data: [{isExisted}]}) => {
              if (isExisted) {
                setDuplicateFileList(preValue => [...preValue, {file: resultFile, sampleId, id: uniqueId()}]);
                onError(`Duplicate sample ID: ${sampleId}`);
              } else {
                formData.append("file", resultFile);
                try {
                  await axios.post("/files/forenseq", formData);
                  onSuccess("Upload successful.");
                } catch (err) {
                  console.log(err);
                  onError(err?.response?.data?.message || "Something went wrong.");
                }
              }
            })
      } catch (ex) {
        onError("Something went wrong.");
      }
    };

    reader.readAsBinaryString(file)
  };

  const onCloseNoticeUpload = () => {
    if (isNoticeLongClose) {
      updateCookie("false", 1);
    }
  }

  const btn = (
      <div>
        <Checkbox onChange={(e) => setIsNoticeLongClose(e.target.checked)}>Do not display this for 1 hour</Checkbox>
        &nbsp;&nbsp;
        <Button onClick={() => {
          history.push("/persons")
          onCloseNoticeUpload()
          notification.close("notice-upload");
        }} type="primary" size="small">
          View
        </Button>
      </div>
  );

  const uploadProps = {
    customRequest,
    multiple: true,
    fileList: uploadFileList,
    showUploadList: true,
    listType: "listType",
    onChange(info) {
      let fileList = [...info.fileList];

      fileList = fileList.map(file => {
        if (file.error) {
          // Component will show file.url as link
          file.response = file.error;
        }
        return file;
      });

      const {status} = info.file;

      if (status === 'done') {
        notification.success({
          message: `${info.file.name} file uploaded successfully.`,
          description: "",
        });
        if (!isNotice && noticeCookie === "true") {
          notification.open({
            key: "notice-upload",
            message: 'Uploaded successfully ',
            description: 'The file(s) were successfully uploaded. ' +
                'You may examine all the data that has been submitted by clicking the View button, ' +
                'or you can continue to upload additional files.',
            style: {
              width: 600,
              marginLeft: 335 - 600,
            },
            btn,
            duration: 0,
            onClose: () => {
              setIsNotice(false)
              onCloseNoticeUpload()
            }
          });
          setIsNotice(true);
        }
      } else if (status === 'error') {
        notification.error({
          message: `${info.file.name} file upload failed.`,
          description: info.file?.error || "Something went wrong.",
        });
      }

      setUploadFileList(fileList);
    },

  };

  const downloadExample = () => {
    axios
        .get("/files/example-excel", {responseType: "blob"})
        .then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", "example-forenseq.xlsx");
          document.body.appendChild(link);
          link.click();
        });
  };

  const removeDuplicatedFile = (id) => {
    const newDuplicateFileList = duplicateFileList.filter(e => e.id !== id);
    setDuplicateFileList(newDuplicateFileList);
  }

  const replaceDuplicatedFile = async (file, sampleId, id) => {
    const data = new FormData();
    data.append("file", file);
    try {
      await axios.post(`/files/forenseq?sampleId=${sampleId}`, data);
      notification.success({
        message: `Replaced file ${file.name} with sample ID: ${sampleId}`
      })
      removeDuplicatedFile(id)
    } catch (err) {
      console.log(err);
      notification.error({
        message: err?.response?.data?.message || "Something went wrong."
      })
    }
  }

  return (
      <div>
        <br/>
        <Row justify="center">
          <h2 onClick={() => {
            notification.open({
              key: "notice-upload",
              message: 'Uploaded successfully ',
              description: 'The file(s) were successfully uploaded. ' +
                  'You may examine all the data that has been submitted by clicking the View button, ' +
                  'or you can continue to upload additional files.',
              style: {
                width: 600,
                marginLeft: 335 - 600,
              },
              btn,
              duration: 0,
              onClose: () => {
                setIsNotice(false)
                onCloseNoticeUpload()
              }
            });
            setIsNotice(true);
          }
          }>Upload Forenseq Data</h2>
        </Row>
        <br/>
        <Row justify="center">
          <Col xs={23} sm={23} md={18} lg={16} xl={14}>
            <Alert
                style={{textAlign: "start", marginBottom: "1rem"}}
                message="If you don't know how to set up an excel file, please see our example"
                action={
                  <Button size="small" onClick={downloadExample}>
                    Example
                  </Button>
                }
                type="info"
                showIcon
                closable
            />
          </Col>
        </Row>
        <Row justify="center">
          <Col xs={23} sm={23} md={18} lg={16} xl={14}>
            {duplicateFileList?.length > 0 ?
                <div style={{border: "1px solid black", marginBottom: "1rem", padding: "1rem"}}>
                  <Row justify="center">
                    <h3>Duplicate data</h3>
                  </Row>
                  <List
                      itemLayout="horizontal"
                      dataSource={duplicateFileList}
                      renderItem={({file, sampleId, id}) => (
                          <List.Item
                              actions={
                                [
                                  <Button
                                      type="primary"
                                      key="replace"
                                      onClick={() => replaceDuplicatedFile(file, sampleId, id)}
                                  >Replace</Button>,
                                  <Button
                                      type="danger"
                                      key="cancel"
                                      onClick={() => removeDuplicatedFile(id)}
                                  >Cancel</Button>
                                ]}
                          >
                            <div>{file.name} <Typography.Text code>(Sample ID: {sampleId})</Typography.Text></div>
                          </List.Item>
                      )}
                  />
                </div> : null}
          </Col>
        </Row>
        <Row justify="center">
          <Col xs={23} sm={23} md={20} lg={18} xl={16}>
            <div style={{textAlign: "start"}}>
              <Dragger {...uploadProps}>
                <p className="ant-upload-drag-icon">
                  <InboxOutlined/>
                </p>
                <p className="ant-upload-text">
                  Click or drag file to this area to upload
                </p>
                <p className="ant-upload-hint">
                  Support for a single or bulk upload. Strictly prohibit from
                  uploading company data or other band files
                </p>
              </Dragger>
            </div>
          </Col>
        </Row>
      </div>
  );
}

export default Forenseq;
