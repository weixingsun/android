#!/usr/bin/python

#python setup.py install	#BeautifulSoup
import os
import re
import csv
import sys
import codecs
import json
import string
import urllib.request
import xml.etree.ElementTree as xml
from bs4 import BeautifulSoup
from pprint import pprint
from collections import defaultdict

#<geonames>
#	<countrySubdivision>
#		<countryCode>NZ</countryCode>
#		<countryName>New Zealand</countryName>
#		<adminCode1>F7</adminCode1>
#		<adminName1>Otago</adminName1>
#		<code type="FIPS10-4">F7</code>
#		<code type="ISO3166-2">OTA</code>
#		<distance>0.0</distance>
#	</countrySubdivision>
#</geonames>
def getAdminGeoNames(txt):
	try:
		tree = xml.fromstring(txt)
		#doc = tree.getroot()
		#thingy = tree.find('adminName1')
		#root[0][1].text
		for admin in tree.iter('adminName1'):
			if(len(admin.text)>0):
				return admin.text
		#for child in tree[0]:
		#	print(child.tag, child.attrib, child.text)
		return ''
	except (RuntimeError,IndexError):
		pass
	return 'admin'
def loopInCsvGeoNames(src,dst):
	with open(src,'r') as s, open(dst, 'w') as d: # input csv file
	#with codecs.open(src, 'r', encoding='utf-8', errors='ignore') as s, codecs.open(dst, 'w', encoding='utf-8') as d:
		writer = csv.writer(d)
		reader = csv.reader(s)
		for row in reader:
			lat,lng,type,country,admin,name = getValue(row)
			url=geo_url+'lat='+lat+'&lng='+lng
			json_text=getHtml3(url).decode("utf-8")
			#print(url)
			admin=getAdminGeoNames(json_text)
			#print(row[0]+","+row[1]+","+row[2]+","+row[3]+","+admin)
			row[3] = 'NZ' # edit the 4th column 
			row[4] = admin # edit the 5th column 
			writer.writerow(row)
				
def getHtml3(url):
	with urllib.request.urlopen(url) as url:
		s = url.read()
	return s
	
def unicode_csv_reader(unicode_csv_data, dialect=csv.excel, **kwargs):
    # csv.py doesn't do Unicode; encode temporarily as UTF-8:
    csv_reader = csv.reader(utf_8_encoder(unicode_csv_data), dialect=dialect, **kwargs)
    for row in csv_reader:
        # decode UTF-8 back to Unicode, cell by cell:
        yield [unicode(cell, 'utf-8') for cell in row]

def utf_8_encoder(unicode_csv_data):
    for line in unicode_csv_data:
        yield line.encode('utf-8')
		
def loopInCsvMerge(src,dst):
	with open(dst, 'w', newline='') as f: # output csv file: wb means use binary mode
		writer = csv.writer(f)
		with open(src,'r') as csvfile: # input csv file
			reader = csv.reader(csvfile)
			#i=0
			for row in reader:
				#i+=1
				#print('#='+str(i))
				if(len(row[2])<1):
					row[2] = getFirstExistValue(row);
				writer.writerow(row)
def loopInCsvMergeType(src,dst):
	with codecs.open(src, 'r', encoding='utf-8') as s, codecs.open(dst, 'w', encoding='utf-8') as d:
		writer = csv.writer(d)
		reader = csv.reader(s)
		for row in reader:
			if(len(row[2])<1):
				row[2] = getFirstExistValue(row);
			writer.writerow(row)
def removeGBK(src,dst):
		with codecs.open(src, 'r', encoding='utf-8') as s, open(dst, 'w', newline='') as d:
			for line in s:
			   newLine=re.sub(r'[^\x00-\x7f]',r'',line)
			   d.write(newLine)
#lat,lng,amenity,historic,tourism,shop,office,leisure,craft,boundary,building,railway,man_made,name
#lat,lng,amenity,historic,tourism,shop,office,leisure,craft,boundary,building,railway,man_made,name = getValue(row)
def getFirstExistValue(row):
	#lat=row[0]
	#lng=row[1]
	#amenity=row[2]
	for x in range(2,14):
		if(len(row[x])>1):
			return row[x]
	return ""
def getValue(row):
	lat=row[0]
	lng=row[1]
	amenity=row[2]
	admin=row[3]
	country=row[4]
	name=row[5]
	return (lat,lng,amenity,admin,country,name)
def getKeyValue(row):
	for (k,v) in row.items():
		if(k=='lat'):
			lat=v
		if(k=='lng'):
			lng=v
	return (lat,lng)
def printCatLinks(url,dist_file):
	html_page = getHtml3(url)
	soup = BeautifulSoup(html_page)
	for link in soup.findAll('a', href=re.compile('^http://www.ebay.com/sch/Watches')):
		appendTxt(dist_file,link.get('href'))

def findGood(cat_url,good_file):
	str_html=getHtml3(cat_url)
	soup = BeautifulSoup(str_html)
	for link in soup.findAll('a', href=re.compile('^http://www.ebay.com/itm')):
		str_href=link.get('href')
		appendTxt(good_file,str_href)
def printGoodLinks(cat_file,good_file):
	loopInFile(findGood,cat_file,good_file)

def appendTxt(file,text):
	with open(file, "a") as out_file:
		out_file.write(str(text)+"\n")

def loopInFile(func,src_file,dist_file):
	with open(src_file) as f:
		for line in f:
			func(line,dist_file)
def findSeller(good_url,dist_file):
	str_html=getHtml3(good_url)
	soup = BeautifulSoup(str_html)
	for link in soup.findAll('a', href=re.compile('^http://www.ebay.com/usr')):
		str_href=link.get('href')
		userid=getUserByUrl(str_href)
		appendTxt(dist_file,userid)
	
def getUserByUrl(url):
	forehead=url.split('?')[0]
	userid=forehead.split('/')[-1]
	return userid
	
def deleteFile(file_path):
	try:
		os.remove(file_path)
	except IOError as e:
		print("OS error: {0}".format(e))
	except ValueError:
		print("Could not convert data to an integer.")
	except:
		print("Unexpected error:", sys.exc_info()[0])
		raise
#############################################################################
#loopInCsvMergeType('nz_poi_type_all.csv','nz_poi_type_all_utf8_merge.csv')

geo_url='http://api.geonames.org/countrySubdivision?username=weixingsun&'	#lat=-46.3865651&lng=169.7818566
#loopInCsvGeoNames('nz_poi_type_all_utf8_trimed2.csv','nz_write_geonames2.csv')
loopInCsvGeoNames('nz_poi_type_all_utf8_trimed3.csv','nz_write_geonames3.csv')

#num=sys.argv[len(sys.argv)-1]
#cat_file='C:/Users/SUN/Desktop/www/ebay_watch/temp/cat'+num+'.txt'
#good_file='C:/Users/SUN/Desktop/www/ebay_watch/temp/links'+num+'.txt'
#test_file='C:/Users/SUN/Desktop/www/google_poi_admin/dst/test.xml'
#test_admin_file='C:/Users/SUN/Desktop/www/google_poi_admin/dst/test_admin.txt'
#google_url='http://maps.googleapis.com/maps/api/geocode/json?latlng='  #39.761442,-104.801158	#-43.525827,172.584113	#-46.3865651,169.7818566
#mapquest_url='http://www.mapquestapi.com/geocoding/v1/reverse?key=YOUR_KEY_HERE&callback=renderReverse&location='	#39.761442,-104.801158
#test_url='http://maps.googleapis.com/maps/api/geocode/json?latlng=-46.3865651,169.7818566'
#test_url2='http://api.geonames.org/countrySubdivision?username=weixingsun&lat=-46.3865651&lng=169.7818566'
#geo_url='http://api.geonames.org/countrySubdivision?username=weixingsun&'	#lat=-46.3865651&lng=169.7818566
#deleteFile(test_admin_file)
#json_text = getHtml3(test_url2).decode("utf-8")
#appendTxt(test_file,json_text)
#admin = getAdminGeoNames(json_text)
#columns = defaultdict(list)
#removeGBK('nz_poi_type_all_utf8.csv','nz_poi_type_no_gbk.csv')
#loopInCsvMerge('nz_poi_type_no_gbk.csv','nz_poi_type_all_merge.csv')
#print(columns['name'])
#print(columns['lat'])
#print(columns['lng'])
#appendTxt(test_admin_file,admin)
#printCatLinks(cat_url,cat_file)
#printGoodLinks(cat_file,good_file)
#loopInFile(findSeller,good_file,user_file)