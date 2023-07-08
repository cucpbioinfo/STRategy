import React from "react";
import DraggerUpload from "../../../components/upload/DraggerUpload";

function CEData() {
  return <DraggerUpload
      infoMessage="If you don't know how to set up a text file, please see our example"
      exampleUrl="/files/example-cedata"
      exampleFileName="example-cedata.txt"
      uploadUrl="/files/cedata"
      uploadTopic="Upload CE Data"/>;
}

export default CEData;
