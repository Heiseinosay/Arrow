#!/bin/python3
import json
import sys
import os

# NOTE: This script to convert geojsonl.json to line delimited geojson with a numbered ID
if len(sys.argv) == 1:
    print("No Input file provided")
    sys.exit(1)

geojson_file = sys.argv[1]
cwd = os.getcwd()
jsondump = ""

with open(f"{cwd}/{geojson_file}") as file:
    data = json.load(file)
    try:
        id = 0
        for i in data["features"]:
            i["id"] = id
            jsondump+=json.dumps(i)+'\n'
            id+=1
    except:
        raise Exception("No \"features\" key")

outfile = geojson_file.split('.')

with open(f"{cwd}/{outfile[0]}.geojson.ld",'w') as file:
    file.write(jsondump)
