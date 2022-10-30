import React from 'react'
import {ManageAllUser} from "./ManageAllUser";
import addHeaders from "../../hoc/addHeader";

export function ManageNewUser() {
  return <ManageAllUser userStatus="NOT_ACCEPT"/>
}

export default addHeaders(ManageNewUser, "Approve new users");
