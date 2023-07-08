import {Row} from "antd";
import React from 'react';

function addHeaders(WrappedComponent, header) {
  return class extends React.Component {
    render() {
      return (
          <>
            <Row align="center" style={{marginTop: "20px"}}>
              <h2>{header}</h2>
            </Row>
            <br/>
            <WrappedComponent {...this.props}/>
          </>
      );
    }
  };
}

export default addHeaders;