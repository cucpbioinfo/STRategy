import {Popover} from 'antd';
import React from 'react';
import NumberUtils from '../../../../_utils/NumberUtils';
import "./TableElement.css"

function TableElement(props) {
  const {value, color} = props;

  const prob = NumberUtils.toScientificNotation(value);
  const content = (<div>Probability: {prob}</div>);

  return (
      <div style={{textAlign: "center"}}>
        {value && <Popover content={content}>
          <div
              className="table-element-color"
              style={{
                backgroundColor: color,
              }}/>
        </Popover>}
      </div>
  );
}

export default TableElement;
