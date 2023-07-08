import React, {useState} from 'react';
import {Input} from "antd";

export function CoreLociField({value = {}, onChange, disabled, id}) {
  const [allele1, setAllele1] = useState("");
  const [allele2, setAllele2] = useState("");

  const triggerChange = (changedValue) => {
    onChange?.({
      allele1,
      allele2,
      ...value,
      ...changedValue,
    });
  };

  const onChangeAllele1 = (e) => {
    const allele1 = e.target.value;
    setAllele1(allele1);
    triggerChange({
      allele1
    })
  }

  const onChangeAllele2 = (e) => {
    const allele2 = e.target.value;
    setAllele2(allele2);
    triggerChange({
      allele2
    })
  }

  return (
      <span id={id}>
      <Input
          type="text"
          disabled={disabled}
          value={value.allele1 || allele1}
          onChange={onChangeAllele1}
          style={{
            width: "50%",
          }}
      />
      <Input
          type="text"
          disabled={disabled}
          value={value.allele2 || allele2}
          onChange={onChangeAllele2}
          style={{
            width: "50%",
          }}
      />
    </span>
  )
}

export default CoreLociField;