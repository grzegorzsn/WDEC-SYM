# Examples of regression use:
# http://statsmodels.sourceforge.net/devel/examples/notebooks/generated/ols.html
# http://statsmodels.sourceforge.net/devel/regression.html
# http://stackoverflow.com/questions/11479064/multiple-linear-regression-in-python

import csv
import numpy as np
import statsmodels.api as sm

def read_lines(filename):
    with open(filename, 'rU') as data:
        reader = csv.reader(data)
        for row in reader:
            yield [ float(i) for i in row ]

def column(table, i):
	clmn = list()
	for row in table:
		clmn.append(row[i])
	return clmn

def reg_m(y, x):
    ones = np.ones(len(x[0]))
    X = sm.add_constant(np.column_stack((x[0], ones)))
    for ele in x[1:]:
        X = sm.add_constant(np.column_stack((ele, X)))
    results = sm.GLSAR(y, X).fit()
    return results
db = list(read_lines("db.csv"))
x1 = column(db, 0)
x2 = column(db, 1)
x3 = column(db, 2)
x4 = column(db, 3)

y = column(db, 4)

reg = reg_m(y, [x1,x2,x3,x4])
print reg.summary()

print reg

dbr = reg.predict(db)

for i in range(0, len(db)):
	print str(db[i]) + str(dbr[i])
	
	
# for r in db:
	# r.append(reg.score(r[:4]))
	# dbr.append(r)
	
# print dbr