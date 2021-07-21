import React, { useState, useMemo } from 'react';

interface AlgoWithIndent {
  description: string;
  indentSize: number;
};

interface AlgoNode {
  description: string;
  subAlgos: AlgoNode[];
};

function encodeWithIndent(algo: string): AlgoWithIndent {
  const description = algo.trimLeft();
  const indentSize = algo.length - description.length;

  return { description, indentSize };
};

function parseAlgo(algos: AlgoWithIndent[], start?: number, end?: number): AlgoNode[] {
  if (start === undefined || end === undefined)
    return parseAlgo(algos, 0, algos.length);

  if (algos.length === 0 || start === end)
    return [];

  const baseIndent = algos[start].indentSize;
  let topLevel: number[] = [];
  for (let i = start; i < end; i++)
    if (algos[i].indentSize === baseIndent)
      topLevel.push(i);

  return topLevel.map((algoIdx, index) => {
    const startIndex = algoIdx;
    const endIndex = (topLevel.length - 1 === index) ? end : topLevel[index + 1];
    return {
      description: algos[algoIdx].description,
      subAlgos: parseAlgo(algos, startIndex + 1, endIndex)
    };
  });
};

interface SpecChildProps {
  algos: AlgoNode[];
  style?: React.CSSProperties;
}

const SpecChild: React.FC<SpecChildProps> = ({ algos, style }) => {
  const [visibleStates, setVisibleStates] = useState<boolean[]>(Array(algos.length).fill(false));

  const handleCollapseToggle =
    (index: number) =>
    (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
      setVisibleStates(arr => arr.map((v, idx) => idx === index ? !v : v));

      // prevent parent element from listening to the event.
      // otherwise the parent will toggle visibility as well.
      event.stopPropagation();
    }

  return (
    <div>
      {algos.map(({ description, subAlgos }, index) => (
        <div key={index} style={style} onClick={subAlgos.length > 0 ? handleCollapseToggle(index) : undefined}>
          <div>{description}</div>
          {visibleStates[index]
            ? <SpecChild algos={subAlgos} style={{ marginLeft: '28px' }} />
            : subAlgos.length > 0
              ? <div style={{ borderLeft: 'dashed black 1px', height: '16px', margin: '8px 4px' }} />
              : null
          }
        </div>
      ))}
    </div>
  );
}

interface Props {
  algos: string[];
};

const SpecBox: React.FC<Props> = ({ algos }) => {
  const parsedAlgos = parseAlgo(algos.map(encodeWithIndent));

  return (
    <div>
      <SpecChild algos={parsedAlgos} />
    </div>
  );
}

export default SpecBox;
