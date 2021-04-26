import json
from nsetools import Nse
from flask import Flask, request, jsonify
from firebase import firebase  
from flask_cors import CORS, cross_origin

app = Flask(__name__)
cors = CORS(app)

@app.route('/api/<code>',methods=['GET'])
@cross_origin()
def price(code):
	#print("got request")
	#print(code)
	nse = Nse()
	stock_code = nse.get_stock_codes()
	#stocks = ['20MICRONS', '3IINFOTECH','3MINDIA','8KMILES','A2ZINFRA','AARTIDRUGS','AARTIIND']
	dic = {}
	#for code in stocks:
	out = nse.get_quote(code)
	dic[code] = out['lastPrice']
	print(dic)
	with open("sample.json", "w") as outfile: 
	    json.dump(dic, outfile)
	with open('sample.json') as data_file:
		data = json.load(data_file)
	#print(data)
	return data

if __name__ == '__main__':
    try:
        app.run( port=5000, debug=True, threaded=True)
    except:
        print("Server is exited unexpectedly. Please contact server admin.")

