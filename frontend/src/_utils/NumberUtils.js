import React from 'react'

let id = 0;

const toScientificNotation = (num) => {
  const numComponents = Number(num).toExponential(2).replace(/e\+?/, ' x 10^').split("^");
  return <div>{numComponents[0]}<sup>{numComponents[1]}</sup></div>
}

const uniqueId = () => {
  return ++id;
}

function isNumeric(str) {
  if (typeof str != "string") return false
  return !isNaN(str) && !isNaN(parseFloat(str))
}

const NumberUtils = {
  toScientificNotation,
  uniqueId,
  isNumeric,
}

export default NumberUtils;