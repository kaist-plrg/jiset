import argparse
import json
import shutil
import subprocess
from os import listdir, makedirs, remove, getcwd, chdir, environ
from os.path import isdir, join, exists

# Color
CEND = '\33[0m'
CRED  = '\33[31m'
CGREEN  = '\33[32m'
CYELLOW = '\33[33m'
def print_red(msg):
  print(f"{CRED}{msg}{CEND}")
  return msg
def print_green(msg):
  print(f"{CGREEN}{msg}{CEND}")
  return msg
def print_yellow(msg):
  print(f"{CYELLOW}{msg}{CEND}")
  return msg

# Path
JISET_HOME = environ["JISET_HOME"]
EVAL_HOME = join(JISET_HOME, "eval")
LOG_HOME = join(JISET_HOME, "logs")
LOG_DIR = join(LOG_HOME, "analyze")
EVAL_LOG = join(EVAL_HOME, "log")

# Util
EVAL_LOG_POST = f"2>> {EVAL_LOG} 1>> {EVAL_LOG}"
def execute_sh(cmd, post = ""):
  proc = subprocess.Popen(f"{cmd} {post}", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
  out, err = proc.communicate()
  proc.wait()
  return out.decode("utf-8"), err.decode("utf-8")
def get_commit_dirs(path):
  return [(d, join(path, d)) for d in listdir(path) if isdir(join(path, d))]
def get_prev_commit(commit_hash):
  cmd = f"cd ../ecma262; git rev-parse {commit_hash}^1"
  out, err = execute_sh(cmd)
  return out.strip() if err == '' else None
def get_commit_date(commit_hash):
  pass
def clean_dir(path):
  if exists(path):
    shutil.rmtree(path)
  makedirs(path)
def build_jiset():
  if exists(EVAL_LOG):
    remove(EVAL_LOG)
  chdir(JISET_HOME)
  print("update...")
  execute_sh("git pull", EVAL_LOG_POST)
  execute_sh("git submodule update", EVAL_LOG_POST)
  print("build project...")
  execute_sh("sbt assembly", EVAL_LOG_POST)
  chdir(EVAL_HOME)
def run_analyze(version):
  cmd = f"jiset analyze -log -parse:version={version} -analyze:target=.*"
  execute_sh(cmd, EVAL_LOG_POST)
def get_target_errors():
  with open("errors.json", "r") as f:
    return json.load(f)

# analysis result class
class AnalysisResult:
  # init
  def __init__(self, version, commit_dir):
    self.version = version
    self.commit_dir = commit_dir
    with open(join(commit_dir, "errors"), "r") as f:
      self.errors = set(f.read().splitlines())
  # check if this analysis result contains `error`
  def contains(self, error):
    return error in self.errors
  # get diff with other analysis result
  def diff(self, that):
    return {
      "-": self.errors - that.errors,
      "+": that.errors - self.errors
    }
  # check bugs
  def check(self, bugs, f):
    version = self.version
    for bug in bugs:
      if self.contains(bug):
        msg = print_green(f"[PASS] @ {version}: {bug}")
      else:
        msg = print_red(f"[FAIL] @ {version}: {bug}")
      f.write(f"{msg}\n") 
    
  # equality
  def __eq__(self, that):
    return isinstance(that, AnalysisResult) and self.errors == that.errors

# check if target errors exist
def check_error_exists(results):
  with open("errors", "w") as f:
    # get target errors
    target_errors = get_target_errors()
    # check if target errors exist
    for target_error in target_errors:
      version = target_error["version"]
      bugs = target_error["bugs"]
      if not version in results.keys():
        for bug in bugs:
          msg = print_yellow(f"[YET] @ {version}: {bug}")
          f.write(f"{msg}\n") 
      else:
        results[version].check(bugs, f)
      
# dump diffs of analysis results
def dump_diffs(results):
  # clean results dir
  clean_dir("diffs")
  # calc diff of each result and dump
  for (version, res) in results.items():
    with open(join("diffs", version), "w") as f:
      prev_commit_hash = get_prev_commit(version)
      f.write("================================================================================\n")
      f.write(f"Version              : {version}\n")
      f.write(f"Previous Version     : {prev_commit_hash}\n")
      f.write("--------------------------------------------------------------------------------\n")
      # if not exists, then 
      if not prev_commit_hash in results.keys():
        f.write(f"No analysis result for previous version")
        continue
      prev_res = results[prev_commit_hash]
      # if analysis result same
      if prev_res == res:
        f.write(f"Same analysis result with previous version")
      # print diff
      else:
        diff = prev_res.diff(res)
        for new_bug in sorted(diff["+"]):
          f.write(f"+{new_bug}\n")
        for old_bug in sorted(diff["-"]):
          f.write(f"-{old_bug}\n")
    
def main():
  # parse arguments
  parser = argparse.ArgumentParser(description="evaluate analyzer result")
  parser.add_argument( "-d", "--dir", help="target logs directory")
  parser.add_argument( 
    "--check-target", 
    action="store_true", 
    default=False, 
    help="run analysis and check target errors"
  )
  args = parser.parse_args()
  
  # check target command
  if args.check_target:
    # build JISET
    build_jiset()
    # clean 
    clean_dir("analyze")
    # get target errors
    target_errors = get_target_errors()
    # run analysis and check if target errors exist
    with open("check-target", "w") as f:
      for target_error in target_errors:
        version = target_error["version"]
        bugs = target_error["bugs"]
        # run analyze
        print(f"run analyze({version})...")
        run_analyze(version)
        new_log_path = f"{EVAL_HOME}/analyze/{version}"
        execute_sh(f"mv {LOG_DIR} {new_log_path}")
        AnalysisResult(version, new_log_path).check(bugs, f)
        print("completed...")

  # default command
  if args.dir != None and isdir(args.dir):
     # get commit directory 
     commit_dirs = get_commit_dirs(args.dir)
     # create analysis result objects
     results = dict((c, AnalysisResult(c, d)) for c, d in commit_dirs)
     # check target error existence
     check_error_exists(results)
     # dump diffs of analysis results
     dump_diffs(results)
  else:
    print(f"No such path({args.dir})")

main()
