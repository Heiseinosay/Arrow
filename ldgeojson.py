#!/bin/python3
import json
import sys
import os

# NOTE: This script converts geojsonl.json to line delimited geojson with a numbered ID
if len(sys.argv) == 1:
    print("No Input file provided")
    sys.exit(1)

def find_separator(val: str) -> str:
    for i in val:
        if i == '/':
            return '/'
        elif i == '\\':
            return '\\'
    return '/'

cwd = os.getcwd()
out = os.getcwd()
path_file = ""
filename = ""
i = 1
while i < len(sys.argv):
    arg = sys.argv[i]
    if arg == "--out":
        out = cwd + '/' + sys.argv[i+1]
        if out[-1] == '/':
            out = out[0:-1]
        i+=1
    else:
        path_file = arg
        filename = arg.split(find_separator(arg))[-1]
    i+=1

# print(out)
print(f"Converting: {filename}")
# print(filename.split('.')[0])
# print(path_file+'\n')
# sys.exit(0)
jsondump = ""

isExit = False
with open(f"{cwd}/{path_file}") as file:
    data = json.load(file)
    try:
        id = 0
        for i in data["features"]:
            i["id"] = id
            jsondump+=json.dumps(i)+'\n'
            id+=1
    except:
        isExit = True
        print("No \"features\" key")

if isExit:
    sys.exit(1)

outfile = filename.split('.')[0]

print(f"Output File: {out}/{outfile}.geojson.ld")
with open(f"{out}/{outfile}.geojson.ld",'w') as file:
    file.write(jsondump)
