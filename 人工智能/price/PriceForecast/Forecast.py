import boto3
from io import StringIO
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from scipy import stats
from scipy.stats import norm
from sklearn import preprocessing
from sklearn.model_selection import train_test_split
from sklearn import linear_model,svm,gaussian_process
from sklearn.ensemble import RandomForestRegressor
import numpy as np
import warnings
warnings.filterwarnings('ignore')
data_train = pd.read_csv("./train.csv")
# 分析特征
# print(data_train["SalePrice"].describe())
# sns.displot(data_train["SalePrice"])
# plt.show()
# print(data_train)

cols = ['OverallQual','GrLivArea',"GarageCars",'TotalBsmtSF',"FullBath",'TotRmsAbvGrd','YearBuilt']
x = data_train[cols].values
y = data_train["SalePrice"].values
x_scaled = preprocessing.StandardScaler().fit_transform(x)
y_scaled = preprocessing.StandardScaler().fit_transform(y.reshape(-1,1))
X_train,X_test,y_train,y_test = train_test_split(x_scaled,y_scaled,test_size = 0.33 ,random_state = 42)

clfs = {
    'svm':svm.SVR(),
    'RandomForestRegressor': RandomForestRegressor(n_estimators=400),
    'BayesianRidge':linear_model.BayesianRidge()
}
for clf in clfs:
    try:
        clfs[clf].fit(X_train,y_train)
        y_pred = clfs[clf].predict(X_test)
        print(clf + "cost:" + str(np.sum(y_pred - y_test)/len(y_pred)))
    except Exception as e:
        print(clf + "Error:")
        print(str(e))
clf = RandomForestRegressor(n_estimators=400)
clf.fit(X_train,y_train)
rfr = clf
data_test = pd.read_csv("./test.csv")
cols2 = ['OverallQual', 'GrLivArea','FullBath','TotRmsAbvGrd','YearBuilt']
cars = data_test['GarageCars'].fillna(1.766118)
bsmt = data_test['TotalBsmtSF'].fillna(1046.117970)
data_test_x = pd.concat([data_test[cols2],cars,bsmt],axis= 1)
data_test_x.isnull().sum()
x = data_test_x.values
y_te_pred = rfr.predict(x)
print(y_te_pred)
print(y_te_pred.shape)
print(x.shape)

prediction = pd.DataFrame(y_te_pred, columns= ['SalePrice'])
result = pd.concat([data_test['Id'],prediction], axis= 1)
result.to_csv('./predictions.csv',index= False)
csv_buffer = StringIO()
result.to_csv(csv_buffer)
s3_resource = boto3.resource('s3',aws_access_key_id='261EB1C4FC64A6CC7C9D',
         aws_secret_access_key= 'W0YyQjczMTVCNkQ3NEI4RkE0REU5QjE3QjcyQUYw')
s3_resource.Object('chenjiajun', 'prediction.csv').put(Body = csv_buffer.getvalue())


