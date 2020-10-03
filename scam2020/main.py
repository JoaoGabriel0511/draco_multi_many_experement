import os
import glob
import sys
import subprocess

arg = sys.argv
mdg_size = arg[1] if arg[1] != "all" else "*"

for mdg in glob.glob("graphs/"+mdg_size+"/*.mdg"):
	mdg_name = mdg.rsplit('.', 1)[0].rsplit('/')[2]
	f = open('output.txt', 'a')
	f.write(mdg_name)
	f.write("\n")
	f.close()
	# print("perf stat -o perf/"+mdg_name+".bunch --append java -cp 'bunch/Bunch-3.5.jar:bunch/' BunchAPITest "+mdg+" 2>> output.txt")
	# print("perf stat -o perf/"+mdg_name+".draco --append ./draco/main < "+mdg+" > draco/dot/"+mdg_name+".dot 2>> output.txt")
	# Draco
	subprocess.call("/usr/bin/time -o time/"+mdg_name+".draco --append ./draco/main < "+mdg+" > draco/dot/"+mdg_name+".dot 2>> output.txt", shell=True)
	# Bunch
	subprocess.call("/usr/bin/time -o time/"+mdg_name+".bunch --append java -cp 'bunch/Bunch-3.5.jar:bunch/' BunchAPITest "+mdg+" >> output.txt", shell=True)
