const {src, dest, watch, series, parallel} = require('gulp');
var ts = require('gulp-typescript');
var tsProject = ts.createProject('tsconfig.json');

const compile = (cb) => {
  tsProject.src()
    .pipe(tsProject())
    .on('error', (_err) => {/* ignore compliation error */ console.log(_err)})
    .js.pipe(dest('dist'));
  cb();
}

const watchFiles = (cb) => {
  watch(['src/*.ts'], compile);
  cb();
}

exports.default = watchFiles;