import React, { useState, useEffect } from 'react';
import jiset from '../lib/jiset.js'
import './App.css';

const App = () => {
  const [code, setCode] = useState("");
  const [spec, setSpec] = useState<null | string>(null);
  const [log, setLog] = useState<string>("");

  useEffect(() => {
    fetch('./src/data/spec.json')
      .then(res => res.text())
      .then(setSpec);
  }, []);

  const run = () => {
    if (spec === null)
      return;

    jiset.setSpec(spec);
    const jsScript = jiset.parseJS(code);

    const oldLog = console.log;
    console.log = value => {
      setLog(log => log + value + '\n');
    }
    jiset.eval(jsScript);
    console.log = oldLog;
  }

  const handleChange: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    setCode(event.target.value);
  }

  return (
    <div className="app-container">
      <div className="app-code">
        <textarea rows={20} cols={50} value={code} onChange={handleChange}></textarea>
        <button onClick={run}>Run!</button>
        {spec === null ? <div>Loading Spec...</div> : <div>Loaded Spec</div>}
      </div>
      <div className="app-log">
        {log.split('\n').map((line, index) => <div key={index} className="app-log-line">{line}</div>)}
      </div>
    </div>
  )
}

export default App;
