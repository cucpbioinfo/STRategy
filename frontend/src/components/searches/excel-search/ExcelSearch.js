import {Alert, Button, Col, notification, Progress, Row, Upload} from "antd";
import React, {useState} from "react";
import {FileTextOutlined} from "@ant-design/icons";
import {API_BASE_URL} from "../../../_config/constants";
import axios from "axios";
import SearchResult from "../search-result/SearchResult";

const {Dragger} = Upload;

function ExcelSearch() {
  const [isClicked, setIsClicked] = useState(false);
  const [percentUpload, setPercentUpload] = useState(0);
  const [statusUpload, setStatusUpload] = useState("active");
  const [showProgressBar, setShowProgressBar] = useState(false);
  const [matchedSample, setMatchedSample] = useState({
    amount: 0,
    total: 0,
    sampleDetails: [],
    countryCount: [],
    provinceCount: [],
    raceCount: [],
    regionCount: [],
  });

  const propsDrag = {
    name: "file",
    action: `${API_BASE_URL}/samples/person/excel`,
    showUploadList: false,
    listType: "picture",
    onChange(info) {
      const {status} = info.file;
      setShowProgressBar(true);
      setStatusUpload("active")
      if (status === "done") {
        notification.success({
          message: `${info.file.name} file uploaded successfully.`,
          description: "",
        });
        const res = info.file.response;
        const {body} = res;

        setMatchedSample({
          amount: body.amount,
          sampleDetails: body.sampleDetails,
          countryCount: body.countryCount,
          raceCount: body.raceCount,
          total: body.total,
        });
        setPercentUpload(100);
        setIsClicked(true);
      } else if (status === "error") {
        notification.error({
          message: `${info.file.name} file upload failed.`,
          description: info.file?.error?.message || "Something went wrong.",
        });
        setStatusUpload("exception");
        setPercentUpload(100);
      } else if (status === "uploading") {
        setPercentUpload(50)
      }
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

  return (
      <Col>
        <Row justify="center">
          <Col xs={23} sm={20} md={16} lg={12} xl={10} xxl={8}>
            <Alert
                style={{textAlign: "start", marginBottom: "1rem"}}
                message="If you don't know how to set up an Excel, please see our example"
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
          <Col xs={23} sm={20} md={16} lg={12} xl={10} xxl={8}>
            <div>
              <Dragger {...propsDrag}>
                <p className="ant-upload-drag-icon">
                  <FileTextOutlined/>
                </p>
                <p className="ant-upload-text">
                  Click or drag an excel file to this area to upload
                </p>
                <p className="ant-upload-hint">
                  Search using your excel file
                </p>
              </Dragger>
            </div>
          </Col>
        </Row>
        {showProgressBar ?
            <Row justify="center">
              <Col xs={23} sm={20} md={16} lg={12} xl={10} xxl={8}>
                <Row justify="center">
                  <Progress
                      percent={percentUpload}
                      status={statusUpload}
                  />
                </Row>
              </Col>
            </Row> : null}
        <SearchResult
            isClicked={isClicked}
            matchedSample={matchedSample}
        />
      </Col>
  );
}

export default ExcelSearch;
