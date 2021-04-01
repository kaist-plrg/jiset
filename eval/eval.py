import argparse
import json
import shutil
import subprocess
from enum import Enum, auto
from os import listdir, makedirs, remove, getcwd, chdir, environ
from os.path import isdir, join, exists

# Color
CEND    = '\33[0m'
CRED    = '\33[31m'
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
LOG_DIR = join(JISET_HOME, "logs", "analyze")
ECMA_DIR = join(JISET_HOME, "ecma262")
RESULT_DIR = join(EVAL_HOME, "result")
RAW_DIR = join(RESULT_DIR, "raw")
DIFF_DIR = join(RESULT_DIR, "diff")
EVAL_LOG = join(RESULT_DIR, "log")

# Shell util
EVAL_LOG_POST = f"2>> {EVAL_LOG} 1>> {EVAL_LOG}"
def execute_sh(cmd, post = ""):
    proc = subprocess.Popen(f"{cmd} {post}", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = proc.communicate()
    proc.wait()
    return out.decode("utf-8"), err.decode("utf-8")
def get_head_commit():
    cmd = f"cd {ECMA_DIR}; git rev-parse HEAD"
    out, err = execute_sh(cmd)
    return out.strip() if err == '' else None
def get_prev_commit(commit_hash):
    cmd = f"cd {ECMA_DIR}; git rev-parse {commit_hash}^1"
    out, err = execute_sh(cmd)
    return out.strip() if err == '' else None
def get_commit_date(commit_hash):
    pass
def clean_dir(path):
    if exists(path):
        shutil.rmtree(path)
    makedirs(path)

# Util
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
    print(f"run analyze({version})...")
    cmd = f"jiset analyze -time -log -silent -parse:version={version} -analyze:target=.*"
    execute_sh(cmd, EVAL_LOG_POST)
    execute_sh(f"mkdir -p {RAW_DIR}")
    version_dir = get_version_dir(version)
    execute_sh(f"rm -rf {version_dir}")
    execute_sh(f"cp -r {LOG_DIR} {version_dir}")
    print("completed...")
def get_target_errors():
    with open(join(EVAL_HOME, "errors.json"), "r") as f:
        return json.load(f)
def get_version_dir(version):
    return join(RAW_DIR, version)
def get_versions():
    return [v for v in listdir(RAW_DIR) if isdir(join(RAW_DIR, v))]

# analysis result class
class CheckErrorType(Enum):
    SOFT = auto()
    ON_DEMAND = auto()
    FORCE = auto()

class AnalysisResult:
    # init
    def __init__(self, version):
        self.version = version
        with open(join(get_version_dir(version), "errors"), "r") as f:
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
        for bug in bugs:
            if self.contains(bug):
                msg = print_green(f"[PASS] @ {self.version}: {bug}")
            else:
                msg = print_red(f"[FAIL] @ {self.version}: {bug}")
            f.write(f"{msg}\n")

    # equality
    def __eq__(self, that):
        return isinstance(that, AnalysisResult) and self.errors == that.errors

# dump diffs of analysis results
def dump_diffs():
    print(f"remove diff directory: {DIFF_DIR}")
    clean_dir(DIFF_DIR)
    print(f"calc diff for current results...")
    # calc diff of each versions and dump
    versions = get_versions()
    for version in versions:
        prev_version = get_prev_commit(version)
        if not prev_version in versions:
            continue
        with open(join(DIFF_DIR, version), "w") as f:
            f.write("================================================================================\n")
            f.write(f"Version          : {version}\n")
            f.write(f"Previous Version : {prev_version}\n")
            f.write("--------------------------------------------------------------------------------\n")
            # if previous version result doesn't exist, then
            if not prev_version in versions:
                f.write(f"No analysis result for previous version")
                continue
            res = AnalysisResult(version)
            prev_res = AnalysisResult(prev_version)
            # if analysis result same
            if prev_res == res:
                f.write(f"Same analysis result with previous version")
            # otherwise print diff
            else:
                diff = prev_res.diff(res)
                for new_bug in sorted(diff["+"]):
                    f.write(f"+{new_bug}\n")
                for old_bug in sorted(diff["-"]):
                    f.write(f"-{old_bug}\n")
    print(f"calc diff completed.")

# dump stats
def dump_stat():
    # check if target errors exist
    check_errors(CheckErrorType.SOFT)
    # dump diffs of analysis results
    dump_diffs()

# run analysis and check if target errors exist
def check_errors(option):
    print(f"check errors(option: {option})...")
    with open(join(RESULT_DIR, "errors"), "w") as f:
        # get target errors
        for target_error in get_target_errors():
            version = target_error["version"]
            bugs = target_error["bugs"]
            # if analysis result for version not exist,
            if not exists(get_version_dir(version)):
                # if SOFT mode, dump YET and continue
                if option == CheckErrorType.SOFT:
                    for bug in bugs:
                        msg = print_yellow(f"[YET] @ {version}: {bug}")
                        f.write(f"{msg}\n")
                    continue
                # otherwise, run analysis
                run_analyze(version)
            # if force mode run analysis
            elif option == CheckErrorType.FORCE:
                run_analyze(version)
            # check results and dump
            AnalysisResult(version).check(bugs, f)
    print("check errors completed.")

# entry
def main():
    # parse arguments
    parser = argparse.ArgumentParser(description="evaluate analyzer result (run all versions if there are no options)")
    parser.add_argument( "--clean", action="store_true", default=False, help="clean result/* and run analysis to all versions" )
    parser.add_argument( "-s", "--stat", action="store_true", default=False, help="dump status of result/raw/*" )
    parser.add_argument( "-v", "--version", help="run analyzer to target version")
    parser.add_argument( "-c", "--check", action="store_true", default=False, help="check errors.json based on cached results/raw/*" )
    parser.add_argument( "-fc", "--fcheck", action="store_true", default=False, help="check errors.json based on new results/raw/*" )
    args = parser.parse_args()

    # make directory
    if args.clean or not exists(RESULT_DIR):
        clean_dir(RESULT_DIR)
    if not exists(RAW_DIR):
        makedirs(RAW_DIR)
    if not exists(DIFF_DIR):
        makedirs(DIFF_DIR)

    # build JISET
    if not args.stat:
        build_jiset()

    # command stat
    if args.stat:
        dump_stat()
    # command check
    elif args.check:
        check_errors(CheckErrorType.ON_DEMAND)
    # command force-check
    elif args.fcheck:
        check_errors(CheckErrorType.FORCE)
    # command run
    elif args.version != None:
        run_analyze(args.version)
    # command all
    else:
        # run all versions and dump stat
        version = get_head_commit()
        while version != None:
            run_analyze(version)
            version = get_prev_commit(version)
        dump_stat()

# run main
main()
