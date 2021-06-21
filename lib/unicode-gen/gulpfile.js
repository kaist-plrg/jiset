const fs = require('fs');
const {task, src, dest, lastRun, watch, series} = require('gulp');
const eslint = require('gulp-eslint');
const uglify = require('gulp-uglify');

const src_files = 'src/**/*.js';

task('pass', function() {
  return src(src_files)
    .pipe(dest('dist'))
});

task('compress', function() {
  return src(src_files)
    .pipe(uglify())
    .pipe(dest('dist'))
});

task('eslint', function() {
  return src(src_files, {since: lastRun('eslint')})
    .pipe(eslint())
    .pipe(eslint.format())
});

task('watch', function() {
  return watch([src_files], series('pass', 'eslint'));
});

task('default', series('pass', 'eslint', 'watch'));
