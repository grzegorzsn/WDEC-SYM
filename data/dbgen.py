import sys
import os
import pandas as pd
import csv

root = "xlsdatasets"

xlsfiles = list()

for path, subdirs, files in os.walk(root):
    for name in files:
		if name.endswith(".xls"):
			xlsfiles.append(os.path.join(path, name))

sheets = list()
for file in xlsfiles:			
	sheet = pd.read_excel(file)
	sheets.append(sheet)

db = list()

for sheet in sheets:
	try:
		col1 = sheet.columns[1]
		col2 = sheet.columns[2]
		volume = sheet[col1][4]
		quality = sheet[col1][5]
		price = sheet[col1][6]
		commCosts = sum(sheet[col1][7:9]) # sum of commercials costs 
		profit = sheet[col2][2]
		db.append((volume, quality, price, commCosts, profit))
	except:
		print "Exception during proccessing sheet: "
		print sheet
	
with open('db.csv','wb') as out:
    csv_out=csv.writer(out)
    for row in db:
        csv_out.writerow(row)