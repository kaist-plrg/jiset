We sincerely thank the reviewers for their careful reviews and detailed
suggestions for improvement. We reflected the suggestions in the artifact and
we answered the questions from the reviewers as follows:

### Q1) Author Information
We missed to explicitly include author information in the artifact. We added
`AUTHORS.md` file in the updated version.

### Q2) DOI (Zenodo)
We already provided the following DOI created by Zenodo in the submission:
- [https://doi.org/10.5281/zenodo.5084817](https://doi.org/10.5281/zenodo.5084817)

However, since it points to a specific version of the artifact, we updated DOI
to point out all versions of the artifact as follows:
- [https://doi.org/10.5281/zenodo.5084816](https://doi.org/10.5281/zenodo.5084816)

### Q3) Docker image
In the submission, we omitted to create docker image because we believe that
our installation guide is enough to evaluate the artifact and reproduce the
evaluation results in the paper.  However, we totally agree that providing
docker image is much easier to do them.  Thus, we create the following docker
image:
```bash
$ docker run -it -m=16g --rm jhnaldo/jstar
# user: jstar / password: jstar
```

### Q4) `ValueError` during `jstar-exp` (for Reviewer A)
The `ValueError` might occur when the `$JSTAR_HOME/eval/result` contains illegal
data.  Please try again after cleaning the `result` directory as follows:
```bash
$ rm -rf $JSTAR_HOME/eval/result && mkdir $JSTAR_HOME/eval/result
```
Moreover, you don't have to perform type analysis for all versions.  We
recommend you check several random versions using the following commands:
```bash
$ jstar-exp -v <version> # add `-nr` for no refinement
```
and use given raw data in directories `raw-refine` and `raw-no-refine` in the
`eval` directory:
```bash
$ rm -rf result && mkdir result  # clean up result directory
$ cp -r raw-refine result/raw    # copy the given raw data
$ jstar-exp -s                   # create summary of raw data
```
and
```bash
$ rm -rf result && mkdir result  # clean up result directory
$ cp -r raw-no-refine result/raw # copy the given raw data
$ jstar-exp -s                   # create summary of raw data
```
