import React from "react";
import {PATTERN_ALIGNMENT_COLOURS as patternAlignmentColours} from "../_config/constants";
import NumberUtils from "./NumberUtils";
import PatternAlignmentUtils from "./PatternAlignmentUtils";
import {Tooltip} from "antd";


const colorForFreqTable = (value) => {
  let color = ["#000000", "#613c1b", "#f94115", "#ff5a06", "#ff8a00"];
  let derivedValue = 0;

  if (value > 0.1) {
    derivedValue = 0;
  } else if (value > 0.01) {
    derivedValue = 1;
  } else if (value > 0.001) {
    derivedValue = 2;
  } else if (value > 0.0001) {
    derivedValue = 3;
  } else if (value <= 0.0001) {
    derivedValue = 4;
  } else {
    // undefined case
    return "#FFFFFF";
  }

  return color[derivedValue];
};

const resolveMotifWithCB = (letters, currentPos, changingBase) => {
  const cb = changingBase.find(e => e.position >= currentPos && e.position < currentPos + letters.length)
  if (cb) {
    const replacedPos = cb.position - currentPos;
    return letters.substring(0, replacedPos) + cb.from + letters.substring(replacedPos + 1, letters.length)
  } else {
    return letters
  }
}

function resolveChangingBase(letters, currentPos, changingBase) {
  const cb = changingBase.find(e => e.position >= currentPos && e.position < currentPos + letters.length)
  if (cb) {
    const replacedPos = cb.position - currentPos;
    return <span>{letters.substring(0, replacedPos)}
      <Tooltip title={`${cb.from} → ${cb.to}`}>
      <span style={{
        backgroundColor: "black",
        color: "white"
      }}><strong>{cb.to}</strong></span>
        </Tooltip>
      {letters.substring(replacedPos + 1, letters.length)}</span>
  } else {
    return letters
  }
}

function generateUniqueColor(strings) {
  const alphaColor = {};
  let k = 0;

  for (let i = 0; i < strings.length; i++) {
    if (typeof alphaColor[strings[i]] === "undefined") {
      alphaColor[strings[i]] = patternAlignmentColours[k++];
    }
  }

  return alphaColor
}


function generateAlphaColor(data, reverse = false) {
  let patternList = [];

  for (let record of data) {
    let seqAlign = PatternAlignmentUtils.resolvePatternAlignment(record.repeatMotif, reverse);
    let pattern = extractedPattern(seqAlign);
    patternList.push(...pattern);
  }

  return generateUniqueColor(patternList.map(e => e.pattern))
}

function extractedPattern(seqAlign) {
  if (!seqAlign) return [];
  let alleles = seqAlign.split(" ").filter(a => a !== "");
  let pattern = [];

  for (let i = 0; i < alleles.length; i++) {
    const allele = alleles[i];
    if (allele === "") continue;
    if (/\d/.test(allele)) {
      let tmp = allele.split("]");
      let tmp1 = tmp[0].split("[");
      let tmp2 = Object.assign({pattern: tmp1[1], number: tmp[1]});
      pattern.push(tmp2);
    } else {
      let tmp2 = Object.assign({pattern: allele.replace("[", "").replace("]", ""), number: 1});
      pattern.push(tmp2);
    }
  }
  return pattern;
}

function maskedAllele(sequence, seqAlign, changingBase, alphaColor, reverse = false) {
  if (reverse) {
    seqAlign = PatternAlignmentUtils.resolveReversedPatternAlignment(seqAlign);
    changingBase = PatternAlignmentUtils.resolveReverseComplementChangingBase(changingBase, sequence.length);
  }

  if (!seqAlign) {
    return <span style={{backgroundColor: ""}}>{sequence}</span>;
  } else {
    let pattern = extractedPattern(seqAlign);
    let final = [];

    for (let i = 0; i < pattern.length; i++) {
      for (let j = 0; j < pattern[i].number; j++) {
        final.push(pattern[i].pattern);
      }
    }

    if (final.length === 0) {
      return <span style={{backgroundColor: "#5BF13E"}}>{sequence}</span>;
    }

    let currentPos = 0;
    const displaySequences = [];
    final.forEach((letters) => {
      displaySequences.push(
          <span
              key={NumberUtils.uniqueId()}
              style={{
                backgroundColor: alphaColor[resolveMotifWithCB(letters, currentPos, changingBase)],
                border: "2px solid #FFFFFF",
                borderRadius: "5px",
                cursor: "default"
              }}>
            {resolveChangingBase(letters, currentPos, changingBase)}
          </span>
      )
      currentPos += letters.length
    });

    return displaySequences;
  }
}

const iSNPColor = {
  "A": "#BAE637",
  "T": "#69C0FF",
  "C": "#FFD666",
  "G": "#FF7A45",
  "A,A": "#BAE637",
  "A,T": "linear-gradient(90deg, rgba(186,230,55,1) 0%, rgba(105,192,255,1) 100%)",
  "A,C": "linear-gradient(90deg, rgba(186,230,55,1) 0%, rgba(255,214,102,1) 100%)",
  "A,G": "linear-gradient(90deg, rgba(186,230,55,1) 0%, rgba(255,122,69,1) 100%)",
  "T,A": "linear-gradient(90deg, rgba(105,192,255,1) 0%, rgba(186,230,55,1) 100%)",
  "T,T": "#69C0FF",
  "T,C": "linear-gradient(90deg, rgba(105,192,255,1) 0%, rgba(255,214,102,1) 100%)",
  "T,G": "linear-gradient(90deg, rgba(105,192,255,1) 0%, rgba(255,122,69,1) 100%)",
  "C,A": "linear-gradient(90deg, rgba(255,214,102,1) 0%, rgba(186,230,55,1) 100%)",
  "C,T": "linear-gradient(90deg, rgba(255,214,102,1) 0%, rgba(105,192,255,1) 100%)",
  "C,C": "#FFD666",
  "C,G": "linear-gradient(90deg, rgba(255,214,102,1) 0%, rgba(255,122,69,1) 100%)",
  "G,A": "linear-gradient(90deg, rgba(255,122,69,1) 0%, rgba(186,230,55,1) 100%)",
  "G,T": "linear-gradient(90deg, rgba(255,122,69,1) 0%, rgba(105,192,255,1) 100%)",
  "G,C": "linear-gradient(90deg, rgba(255,122,69,1) 0%, rgba(255,214,102,1) 100%)",
  "G,G": "#FF7A45",
};

const resolveRoleColor = (role) => {
  switch (role) {
    case "ROLE_LAB_USER":
      return "geekblue";
    case "ROLE_ADMIN":
      return "volcano";
    default:
      return "yellow";
  }
}

const resolveStatusColor = (status) => {
  switch (status) {
    case "ACCEPT":
      return "green";
    case "NOT_ACCEPT":
      return "geekblue";
    case "DELETE":
      return "volcano";
    default:
      return "gray";
  }
}

const ColorUtils = {
  colorForFreqTable,
  iSNPColor,
  resolveMotifWithCB,
  maskedAllele,
  generateAlphaColor,
  generateUniqueColor,
  resolveRoleColor,
  resolveStatusColor
};

export default ColorUtils;