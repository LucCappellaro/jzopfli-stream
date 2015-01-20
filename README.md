jzopfli-stream
=======
This project implements a ZipOutputStream using the [Zopfli](https://code.google.com/p/zopfli/) algorithm.

Advatanges:
1) Better compression
2) Easy to use (drop-in replacement)
3) Zip archive which allows to compress multiple files in one archive.
4) Streaming mode which allows to compress very large files
5) Low memory consumption


Default memory consumption (configurable):
input buffer = window + masterblock + 1
output buffer = masterblock
32768b+20000000b+1b+20000000b=40032769b ~ 40MB


Note:
This is Work in progress. which is **not** ready for production.