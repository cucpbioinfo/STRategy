const resolvePatternAlignment = (repeatMotif, isReverse) => {
  if (!isReverse) {
    return repeatMotif;
  }

  return resolveReversedPatternAlignment(repeatMotif)
}

const resolveReversedPatternAlignment = (repeatMotif) => {
  const motifs = convertToSequenceList(repeatMotif);
  const reverseMotifs = [];

  for (let i = motifs.length - 1; i >= 0; i--) {
    const currentMotif = motifs[i].motif;
    const currentAmount = motifs[i].amount;

    reverseMotifs.push({
      motif: resolveReverseComplementSequence(currentMotif),
      amount: currentAmount
    });
  }

  return convertToSequenceString(reverseMotifs);
}

const convertToSequenceString = (motifs) => {
  console.log(motifs)
  let sequence = "";
  for (let i = 0; i < motifs.length; i++) {
    const currentMotif = motifs[i].motif;
    let currentAmount = motifs[i].amount;
    if (currentAmount === null) {
      currentAmount = "n";
    }
    let nIndex = currentMotif.lastIndexOf("N");
    if (currentAmount === 1 || nIndex !== -1) {
      switch (nIndex) {
        case -1:
          sequence += currentMotif + " ";
          break;
        case 0:
          sequence += currentMotif + currentAmount + " ";
          break;
        default:
          sequence += "N" + currentMotif.length + " ";
      }
    } else {
      sequence += `[${currentMotif}]${currentAmount} `;
    }
  }
  return sequence;
}

const convertToSequenceList = (sequence) => {
  const seqList = sequence.split(" ").filter(e => e !== "");
  const result = [];

  for (let i = 0; i < seqList.length; i++) {
    const motif = seqList[i];
    let mtfInfo = motif.replace("[", "").split("]");
    if (mtfInfo.length === 1) {
      let nIndex = mtfInfo[0].lastIndexOf("N");
      switch (nIndex) {
        case -1:
          result.push({
            motif: mtfInfo[0],
            amount: 1
          });
          break;
        case 0:
          result.push({
            motif: "N",
            amount: Number(mtfInfo[0].slice(1))
          });
          break;
        default:
          result.push({
            motif: "N",
            amount: mtfInfo[0].length
          })
      }
    } else {
      result.push({
        motif: mtfInfo[0],
        amount: mtfInfo[1] === "n" ? "n" : Number(mtfInfo[1])
      });
    }
  }

  return result;
}

const resolveReverseComplementSequence = (sequence) => {
  return sequence.split("").reverse().map(base => reverseBase(base)).join("");
}

const reverseBase = (base) => {
  switch (base) {
    case 'A':
      return 'T';
    case 'T':
      return 'A';
    case 'C':
      return 'G';
    case 'G':
      return 'C';
    case 'a':
      return 't';
    case 't':
      return 'a';
    case 'c':
      return 'g';
    case 'g':
      return 'c';
    default:
      return base;
  }
}

const resolveReverseComplementChangingBase = (changingBase, sequenceLength) => {
  const reversedChangingBase = []

  for (let i = changingBase.length - 1; i >= 0; i--) {
    const curCB = changingBase[i];

    reversedChangingBase.push({
      from: reverseBase(curCB.from),
      to: reverseBase(curCB.to),
      position: sequenceLength - curCB.position - 1,
    })
  }

  return reversedChangingBase;
}

const getPatternAlignmentByAlleles = (reference, allele) => {
  if (!reference || reference.length === 0) {
    return "Not Referenced Found"
  }
  let idx = reference.findIndex(e => e.allele === allele);
  if (idx === -1) {
    idx = reference.findIndex(e => e.allele === 0);
    if (idx === -1) {
      return "Not Referenced Found"
    }
  }
  return convertToSequenceString(reference[idx].motifs);
}

const PatternAlignmentUtils = {
  resolvePatternAlignment,
  resolveReverseComplementSequence,
  resolveReverseComplementChangingBase,
  resolveReversedPatternAlignment,
  getPatternAlignmentByAlleles,
}

export default PatternAlignmentUtils;