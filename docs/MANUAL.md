# Manual

Input can be like the following input file:

```
	sample1	sample2	sample3
sample1	1.0	0.5	0.9
sample2	0.5	1.0	0.5
sample3	0.9	0.5	1.0
```

Example run:
```bash
java -jar FindOverlapMatch-version.jar \
-i input.txt \
-c 0.9 \
-o outputFile
```
Will yield the following file:
```
sample1	(sample3,0.9)
sample2	
sample3	(sample1,0.9)
```
With `--use_same_names` set it should be:

```
sample1	(sample1,1.0)	(sample3,0.9)
sample2	(sample2,1.0)
sample3	(sample1,0.9)	(sample3,1.0)
```