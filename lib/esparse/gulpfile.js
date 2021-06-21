const fs = require('fs');
const {task, src, dest, lastRun, watch, series} = require('gulp');
const eslint = require('gulp-eslint');
const mocha = require('gulp-mocha');
const uglify = require('gulp-uglify');

const src_files = 'src/**/*.js';
const test_files = 'test/**/*.test.js';

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

const logfile = '.test.log';
task('test', function() {
  return src(test_files, {read: false})
    .pipe(mocha({ reporter: 'progress' }))
    .on('error', () => {
    })
    .once('end', () => {
      if (fs.existsSync(logfile)) {
        console.log(fs.readFileSync(logfile, 'utf8'));
      }
    })
});

task('watch', function() {
  return watch([src_files, test_files], series('pass', 'eslint', 'test'));
});

task('default', series('pass', 'eslint', 'test', 'watch'));
