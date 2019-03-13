
#!/bin/bash
for i in 1 2 3
do
   sleep 1
   1>&2 echo Goodbye, World $i times!
done