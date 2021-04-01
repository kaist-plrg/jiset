import argparse
import json
import shutil
import subprocess
from functools import reduce
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

# Global
FIRST_VERSION = "fc85c50181b2b8d7d75f034800528d87fda6b654"
ES2018_VERSION = "59d73dc08ea371866c1d9d45843e6752f26a48e4"

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
def get_all_commits():
    cmd = f"cd {ECMA_DIR}; git rev-list HEAD"
    out, err = execute_sh(cmd)
    all_commits = out.split()
    return all_commits[:all_commits.index(ES2018_VERSION) + 1]
def get_commit_date(commit_hash):
    cmd = f"cd {ECMA_DIR}; git show -s --format=%ci {commit_hash}"
    out, err = execute_sh(cmd)
    return out.strip()
def get_remote_errors(remote_addr):
    cmd = f"rsync -a -m --include '**/errors' --include='*/' --exclude='*' {remote_addr}/eval/result/raw {RESULT_DIR}"
    out, err = execute_sh(cmd)
def get_commit_desc(commit_hash):
    date_str = get_commit_date(commit_hash)
    return date_str + "/" + commit_hash
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
    desc = get_commit_desc(version)
    print(f"run analyze({desc})...")
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
def has_result(version):
    return exists(get_version_dir(version))
def get_results(versions):
    return [AnalysisResult(v) for v in versions]

# check type enumeration
class CheckErrorType(Enum):
    SOFT = auto()
    ON_DEMAND = auto()
    FORCE = auto()

# analysis result class
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
        tp = 0
        for bug in bugs:
            if self.contains(bug):
                msg = print_green(f"[PASS] @ {self.version}: {bug}")
                tp += 1
            else:
                msg = print_red(f"[FAIL] @ {self.version}: {bug}")
            f.write(f"{msg}\n")
        return tp
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

# dump bug diffs
def dump_bug_diffs():
    versions = get_versions()
    errors_map = dict([(v, AnalysisResult(v).errors) for v in versions])
    errors = reduce(lambda acc, v: acc.union(errors_map[v]), errors_map, set())
    # sort version in ASC
    sorted_versions = [v for v in reversed(get_all_commits()) if v in versions]
    first_version = sorted_versions[0]
    results = dict([(e, []) for e in errors])
    # get diffs of each error
    for error in errors:
        found, created_at = False, f"Before {first_version}"
        for version in sorted_versions:
            contained = error in errors_map[version]
            if contained and not found:
                if version != first_version:
                    created_at = version
                found = True
            elif not contained and found:
                results[error].append((created_at, version))
                found = False
        # handle last version
        if found:
            results[error].append((created_at, "Not Fixed"))
    # dump results
    with open(join(RESULT_DIR, "bug-diffs.json"), "w") as f:
        pretty_results = []
        for e in results:
            infos = []
            for created_at, deleted_at in results[e]:
                infos.append({"created_at": created_at, "deleted_at": deleted_at})
            pretty_results.append({"errors": e, "infos": infos})
        json.dump(pretty_results, f, indent=2)
    return len(errors)

# dump diff summary
def dump_diff_summary():
    versions = get_versions()
    results_map = dict([(v, AnalysisResult(v)) for v in versions])
    # sort version in DESC
    sorted_versions = [v for v in get_all_commits() if v in versions]
    first_version = sorted_versions[-1]
    with open(join(RESULT_DIR, "diff-summary.tsv"), "w") as f:
        writeln = lambda cells: f.write("\t".join(cells) + "\n")
        size = lambda s: str(len(s))
        # columns: version | + | - | # of errors
        writeln(["version", "+", "-", "# of errors"])
        for i in range(len(sorted_versions)):
            version = sorted_versions[i]
            result = results_map[version]
            error_size = size(result.errors)
            if version == first_version:
                writeln([first_version, "-", "-", error_size])
            else:
                prev_result = results_map[sorted_versions[i+1]]
                diff = prev_result.diff(result)
                writeln([version, size(diff["+"]), size(diff["-"]), error_size]) 

# dump stats
def dump_stat():
    # check if target errors exist
    tp = check_errors(CheckErrorType.SOFT)
    # dump diffs of analysis results
    dump_diffs()
    # dump bug diffs of analysis results
    p = dump_bug_diffs()
    # dump diff summary
    dump_diff_summary()
    # print precision
    print(f"precision: {tp}/{p}")

# run analysis and check if target errors exist
def check_errors(option):
    print(f"check errors(option: {option})...")
    tp = 0
    with open(join(RESULT_DIR, "errors.log"), "w") as f:
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
            tp += AnalysisResult(version).check(bugs, f)
    print("check errors completed.")
    return tp

# strictly check target errors
def strict_check_errors():
    versions = get_all_commits()
    print(f"strict-check errors...")
    with open(join(RESULT_DIR, "strict-errors.log"), "w") as f:
        # get target errors
        for target_error in get_target_errors():
            version = target_error["version"]
            bugs = target_error["bugs"]
            # handle first version
            if version == versions[0] and has_result(version):
                AnalysisResult(version).check(bugs, f)
                continue
            next_version = versions[versions.index(version)-1]
            # log YET if analysis result is not found
            if not has_result(version) or not has_result(next_version):
                for bug in bugs:
                    msg = print_yellow(f"[YET] @ {version}: {bug}")
                    f.write(f"{msg}\n")
            # strict check
            else:
                result = AnalysisResult(version)
                next_result = AnalysisResult(next_version)
                for bug in bugs:
                    if result.contains(bug) and not next_result.contains(bug):
                        msg = print_green(f"[PASS] @ {version}: {bug}")
                        f.write(f"{msg}\n")
                    else:
                        msg = print_red(f"[FAIL] @ {version}: {bug}")
                        f.write(f"{msg}\n")
    print(f"strict-check completed.")

# sparsely run analyzer based on previous analysis result
def sparse_run(log_f):
    print(f"run-sparse started...")
    versions = get_all_commits()
    # calc sparse targets
    targets = [False] * len(versions)
    # always analyze recent, es2018 commit
    targets[0], targets[-1] = True, True
    for i, version in enumerate(versions):
        if version == ES2018_VERSION:
            break
        prev_version = versions[i+1]
        # if no analysis result, add to targets
        if not has_result(version) or not has_result(prev_result):
            targets[i] = True
        else:
            prev_result, result = get_results([prev_version, version])
            # if diff is not empty, add to targets
            if prev_result != result:
                targets[i] = True
    # run analyzer sparsely
    i, analyzed = 0, set()
    def analyze_once(v):
        if not v in analyzed:
            run_analyze(v)
            analyzed.add(v)
            log_f.write(v + "\n")
    def analyze_range(i0, i1):
        for v in versions[i0:i1]:
            analyze_once(v)
    while True:
        if versions[i] == ES2018_VERSION:
            analyze_once(version)
            break
        version = versions[i]
        nt_i = targets.index(True, i+1)
        # get nt_version and prev_version
        nt_version, prev_version = versions[nt_i], versions[i+1]
        # analyze 3 versions
        analyze_once(version)
        analyze_once(prev_version)
        analyze_once(nt_version)
        if nt_i - i <= 2:
            i = nt_i
            continue
        # compare nt_result and prev_result diff with previous results
        nt_result, prev_result = get_results([nt_version, prev_version])
        # if diff is still not found
        if nt_result == prev_result:
            for v in versions[i+2:nt_i]:
                desc = get_commit_desc(v)
                print(f"Skip {desc}...")
        else:
            analyze_range(i+2, nt_i)
        # move to next target
        i = nt_i
    print(f"run-sparse completed.")

# entry
def main():
    # parse arguments
    parser = argparse.ArgumentParser(description="evaluate analyzer result (run all versions if there are no options)")
    parser.add_argument( "--clean", action="store_true", default=False, help="clean result/* and run analysis to all versions" )
    parser.add_argument( "-s", "--stat", action="store_true", default=False, help="dump status of result/raw/*" )
    parser.add_argument( "-v", "--version", help="run analyzer to target version")
    parser.add_argument( "-c", "--check", action="store_true", default=False, help="check errors.json based on CACHED result/raw/*" )
    parser.add_argument( "-sc", "--scheck", action="store_true", default=False, help="strictly check errors.json based on result/raw/*" )
    parser.add_argument( "-fc", "--fcheck", action="store_true", default=False, help="check errors.json based on NEW result/raw/*" )
    parser.add_argument( "--stride", help="run analyzer based on stride(OFFSET/STRIDE)")
    parser.add_argument( "--sparse", action="store_true", default=False, help="run analyzer sparsely based on diff" )
    parser.add_argument( "-g", "--grep", type=lambda items:[item for item in items.split(",")], help="grep $JISET_HOME/eval/result/raw/*/error from remote")
    args = parser.parse_args()

    # make directory
    if args.clean or args.grep or not exists(RESULT_DIR):
        clean_dir(RESULT_DIR)
    if not exists(RAW_DIR):
        makedirs(RAW_DIR)
    if not exists(DIFF_DIR):
        makedirs(DIFF_DIR)

    # build JISET
    if not args.stat and not args.grep:
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
    # command strict-check
    elif args.scheck:
        strict_check_errors()
    # command run
    elif args.version != None:
        run_analyze(args.version)
    # command grep
    elif args.grep != None:
        for addr in args.grep:
            get_remote_errors(addr)
    # command sparse
    elif args.sparse:
        with open(join(RESULT_DIR, "analyzed"), "w") as f:
            sparse_run(f)
    # command all
    else:
        # run all versions
        versions = get_all_commits()
        if args.stride != None:
            offset, stride = [int(n) for n in args.stride.split("/")]
            versions = versions[offset::stride]
        with open(join(RESULT_DIR, "analyzed"), "w") as f:
            for version in versions:
                run_analyze(version)
                f.write(version + "\n")

# run main
main()
