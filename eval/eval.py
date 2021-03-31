import argparse
import json
from os import listdir
from os.path import isdir, join

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
def get_commit_dirs(path):
  return [(d, join(path, d)) for d in listdir(path) if isdir(join(path, d))]

def get_prev_commit(commit_hash):
  pass

# analysis result class
class AnalysisResult:
  # init
  def __init__(self, commit_dir):
    self.commit_dir = commit_dir
    with open(join(commit_dir, "errors"), "r") as f:
      self.errors = set(f.read().splitlines())
  # check if this analysis result contains `error`
  def contains(self, error):
    return error in self.errors
  # get diff with other analysis result
  def diff(self, that):
    pass

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
      results = dict((c, AnalysisResult(d)) for c, d in commit_dirs)
      # 1. check target error existence
      check_error_exists(results)
      # TODO
      # 2. calc diff
      # 3. print summary
    else:
      raise Exception(f"Error: invalid path({args.dir})")
  except Exception as ex:
    print(ex)

main()
