import React from 'react'
import DraggerUpload from "../../../components/upload/DraggerUpload";

export function ManageCoreLoci(props) {
  const handleOnSuccess = () => {

  };

  return (
      <div>
        <DraggerUpload
            infoMessage="If you don't know how to set up an excel file, please see our example"
            exampleUrl="/files/core-loci"
            exampleFileName="core-loci.xlsx"
            uploadUrl="/core-loci/"
            method="put"
            uploadTopic="Manage Core Loci"
            onSuccess={handleOnSuccess}/>
      </div>
  )
}

export default ManageCoreLoci;