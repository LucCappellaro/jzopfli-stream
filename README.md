jzopfli-stream
=======
This project implements a ZipOutputStream using the [Zopfli](https://code.google.com/p/zopfli/) algorithm.

## Advatanges:
<ol>
<li>Better compression</li>
<li>Easy to use (drop-in replacement)</li>
<li>Zip archive which allows to compress multiple files in one archive.</li>
<li>Streaming mode which allows to compress very large files</li>
<li>Low memory consumption</li>
</ol>


Default memory consumption (configurable):
```
input buffer = window + masterblock + 1
output buffer = masterblock
32768B + 20000000B + 1B + 20000000B = 40032769B ~ 40MB
```

## Note:
This is Work in progress. which is **not** ready for production.