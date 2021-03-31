import argparse
import json
import shutil
import subprocess
from os import listdir, makedirs
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

# Util
def execute_sh(cmd):
  proc = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
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
  # equality
  def __eq__(self, that):
    return isinstance(that, AnalysisResult) and self.errors == that.errors

# check if target errors exist
def check_error_exists(results):
  # get target errors
  with open("errors.json", "r") as f:
    target_errors = json.load(f)
  # check if target errors exist
  with open("errors", "w") as f:
    for target_error in target_errors:
      version = target_error["version"]
      for bug in target_error["bugs"]:
        if version in results.keys():
          if results[version].contains(bug):
            msg = print_green(f"[PASS] @ {version}: {bug}")
          else:
            msg = print_red(f"[FAIL] @ {version}: {bug}")
        else:
          msg = print_yellow(f"[YET] @ {version}: {bug}")
        f.write(f"{msg}\n") 
      
# dump diffs of analysis results
def dump_diffs(results):
  # clean results dir
  if exists("results"):
    shutil.rmtree("results")
  makedirs("results")
  # calc diff of each result and dump
  for (version, res) in results.items():
    with open(join("results", version), "w") as f:
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
        f.write(f"Same results with previous version")
      # print diff
      else:
        diff = prev_res.diff(res)
        for new_bug in sorted(diff["+"]):
          f.write(f"+{new_bug}\n")
        for old_bug in sorted(diff["-"]):
          f.write(f"-{old_bug}\n")
    
def main():
  parser = argparse.ArgumentParser(description="check injected result")
  parser.add_argument( '-d', '--dir', help="target directory")
  args = parser.parse_args()
  
  try:
    # check directory
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
      raise Exception(f"Error: invalid path({args.dir})")
  except Exception as ex:
    print(ex)

main()
