#!/usr/bin/env python3
import shutil
import os
import sys

dr = sys.argv[1]

for root, dirs, files in os.walk(dr):
    for file in files:
        if file == "vector.svg":
            spl = root.split("/"); newname = spl[-1]; sup = ("/").join(spl[:-1])
            shutil.move(root+"/"+file, sup+"/"+newname+".svg"); shutil.rmtree(root)