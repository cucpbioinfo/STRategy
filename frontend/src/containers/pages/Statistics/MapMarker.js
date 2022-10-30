import {Popover, Table} from 'antd';
import React from 'react';

function MapMarker(props) {
  const content = (
      <div>
        <Table
            columns={[
              {
                title: 'Allele',
                dataIndex: 'allele',
              },
              {
                title: 'Amount',
                dataIndex: 'amount',
              }
            ]}
            dataSource={props.alleleAmountList}
            pagination={false}
            size="small"
        />
      </div>
  );

  return (
      <Popover
          content={content}
          title={`${props.province.province}(${props.province.nativeName})`}
      >
        <text
            style={{cursor: "default"}}
            x="-2.8"
            y="3.2"
            fontSize="10"
            fill="#000"
            strokeWidth="0"
        >
          {props.alleleAmountList.reduce((pv, cv) => pv + cv.amount || 0, 0)}
        </text>
      </Popover>
  );
}

export default MapMarker;