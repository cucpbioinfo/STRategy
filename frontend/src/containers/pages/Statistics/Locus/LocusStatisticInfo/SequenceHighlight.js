import React from "react";
import NumberUtils from "../../../../../_utils/NumberUtils";

const {uniqueId} = NumberUtils;

function SequenceHighlight(props) {
  const {
    sequence
  } = props
  const textColor = "#808080"
  const aColor = "#92D050"
  const tColor = "#FFC7CE"
  const cColor = "#8DB4E2"
  const gColor = "#FFFF00"

  const resolveDiv = (nucleotide) => {
    let backgroundColor = ""
    switch (nucleotide.toUpperCase()) {
      case "A":
        backgroundColor = aColor;
        break;
      case "T":
        backgroundColor = tColor;
        break;
      case "C":
        backgroundColor = cColor;
        break;
      case "G":
        backgroundColor = gColor;
        break;
      default:
        backgroundColor = "white"
    }

    return <div style={{
      color: textColor, backgroundColor: backgroundColor, display: "inline-block",
      fontSize: "10px", height: "16px", width: "16px", verticalAlign: "middle", textAlign: "center"
    }} key={uniqueId()}><strong>{nucleotide}</strong></div>
  }

  return (
      <div style={{wordBreak: "break-all"}}>{sequence.split("").map(n => resolveDiv(n))}</div>
  )
}

export default SequenceHighlight;