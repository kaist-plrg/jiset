import React, { useState, useEffect, useRef } from 'react';
import * as jiset from '../lib/jiset'
import './App.css';

const oldLog = console.log;

const App = () => {
  const [code, setCode] = useState("");
  const [spec, setSpec] = useState<null | string>(null);
  const [log, setLog] = useState<string>("");
  const [debugging, setDebugging] = useState<boolean>(false);
  const [debugLine, setDebugLine] = useState('');
  const repl = useRef<any>(null);

  useEffect(() => {
    fetch('./src/lib/spec.json')
      .then(res => res.text())
      .then(setSpec);
  }, []);

  const run = () => {
    if (spec === null)
      return;

    jiset.setSpec(spec);
    const jsScript = jiset.parseJS(code);

    console.log = value => {
      setLog(log => log + value + '\n');
    }
    jiset.eval(jsScript);
    console.log = oldLog;
  }

  const start = () => {
    if (spec === null) return;

    jiset.setSpec(spec);
    const script = jiset.parseJS(code);

    const state = jiset.getInitialState(script);
    const webREPL: any = new jiset.WebREPL(state);

    repl.current = webREPL;
    console.log = value => {
      setLog(log => log + value + '\n');
    }
    setDebugging(true);
  }

  const handleCodeChange: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    setCode(event.target.value);
  }

  const handleDebugLineChange: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    setDebugLine(event.target.value);
  }

  const handleDebugRun = () => {
    if (debugLine === "exit") {
      repl.current = null;
      setDebugLine("");
      setDebugging(false);
      console.log("Exiting...");
      console.log = oldLog;
      return;
    }

    repl.current.execute(debugLine);
    setDebugLine("");
  }

  return (
    <div className="app-container">
      <div className="app-code">
        <textarea rows={30} cols={50} value={code} onChange={handleCodeChange} disabled={debugging}></textarea>
        <button onClick={run} disabled={debugging}>Run!</button>
        <button onClick={start}>Test</button>
        {spec === null ? <div>Loading Spec...</div> : <div>Loaded Spec</div>}
        {debugging && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'center' }}>Debugging session started</div>
            <div style={{ width: '100%', display: 'flex' }}>
              <input type="text" value={debugLine} onChange={handleDebugLineChange} style={{ flex: 1 }}/>
              <button onClick={handleDebugRun}>run</button>
            </div>
            <div className="app-algo">
              {repl.current.getAlgo("id").map((id: string) => <div>{"Current Algorithm: "+id}</div>)}
              {repl.current.getAlgo("code").map((algo: string) => <div>{algo}</div>)}
            </div>
          </div>
        )}
      </div>
      <div className="app-log">
        {log.split('\n').map((line, index) => <div key={index} className="app-log-line">{line}</div>)}
      </div>
    </div>
  )
}

export default App;
