import {InboxOutlined} from "@ant-design/icons";
import {Alert, Button, Col, notification, Row, Upload} from "antd";
import axios from "../../_config/axios";
import React, {useState} from "react";

function DraggerUpload(props) {
  const [uploadFileList, setUploadFileList] = useState([]);

  const {Dragger} = Upload;
  const {
    onSuccess = () => {
    },
    onFailed = () => {
    }
  } = props;
  const {uploadUrl, uploadTopic, infoMessage, exampleUrl, exampleFileName, method = "post"} = props;

  const customRequest = async ({onSuccess, onError, file}) => {
    const data = new FormData();
    data.append("file", file);
    try {
      switch (method.toLowerCase()) {
        case "put":
          await axios.put(uploadUrl, data);
          break;
        case "post":
          await axios.post(uploadUrl, data);
          break;
        default:
          await axios.post(uploadUrl, data);
      }
      onSuccess("Upload successful.");
    } catch (err) {
      console.log(err);
      onError(err?.response?.data?.message || "Something went wrong.");
    }
  };

  const uploadProps = {
    customRequest,
    multiple: true,
    fileList: uploadFileList,
    showUploadList: {showDownloadIcon: false, showRemoveIcon: false},
    listType: "picture",
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
        onSuccess(info);
      } else if (status === 'error') {
        notification.error({
          message: `${info.file.name} file upload failed.`,
          description: info.file?.error || "Something went wrong.",
        });
        onFailed(info);
      }

      setUploadFileList(fileList);
    },

  };

  const downloadExample = () => {
    axios
        .get(exampleUrl, {responseType: "blob"})
        .then((response) => {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", exampleFileName);
          document.body.appendChild(link);
          link.click();
        });
  };

  return (
      <div>
        {uploadTopic ?
            <><br/>
              <Row justify="center">
                <h2>{uploadTopic}</h2>
              </Row>
              <br/>
            </> : null
        }
        <Row justify="center">
          <Col span={14}>
            <Alert
                style={{textAlign: "start", marginBottom: "1rem"}}
                message={infoMessage}
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
          <Col span={20}>
            <div>
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

export default DraggerUpload;
