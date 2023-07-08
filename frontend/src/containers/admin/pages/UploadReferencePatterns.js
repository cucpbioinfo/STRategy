import React from 'react'
import DraggerUpload from "../../../components/upload/DraggerUpload";
import {Button, notification} from "antd";
import {useHistory} from "react-router-dom";
import {dynamicPagesConfig} from "../../../_config/roles";

function UploadReferencePatterns(props) {
  const history = useHistory();

  const handleOnSuccess = () => {
    const key = `open${Date.now()}`;
    const btn = (
        <Button type="primary" size="small" onClick={() => {
          history.push(dynamicPagesConfig.managePatternAlignment.url)
          notification.close(key)
        }}>
          View management
        </Button>
    );
    notification.open({
      message: 'Upload successfully',
      description:
          'You must re-generate the new pattern alignment after successfully uploading. ' +
          'To access the pattern alignment creation page, click "View management."',
      btn,
      key,
      duration: 8
    });
  };


  return (
      <div>
        <DraggerUpload
            infoMessage="If you don't know how to set up an excel file, please see our example"
            exampleUrl="/files/example-seq-guide"
            exampleFileName="referenced-patterns.xlsx"
            uploadUrl="/configuration/referenced-str-patterns"
            method="put"
            uploadTopic="Referenced STR patterns"
            onSuccess={handleOnSuccess}/>
      </div>
  )
}

export default UploadReferencePatterns;